package work.racka.reluct.common.features.tasks.statistics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import work.racka.reluct.common.features.tasks.usecases.interfaces.GetDailyTasksUseCase
import work.racka.reluct.common.features.tasks.usecases.interfaces.GetWeekRangeFromOffset
import work.racka.reluct.common.features.tasks.usecases.interfaces.GetWeeklyTasksUseCase
import work.racka.reluct.common.features.tasks.usecases.interfaces.ModifyTaskUseCase
import work.racka.reluct.common.model.domain.tasks.Task
import work.racka.reluct.common.model.states.tasks.DailyTasksState
import work.racka.reluct.common.model.states.tasks.TasksEvents
import work.racka.reluct.common.model.states.tasks.TasksStatisticsState
import work.racka.reluct.common.model.states.tasks.WeeklyTasksState
import work.racka.reluct.common.model.util.time.WeekUtils

internal class TasksStatisticsImpl(
    private val modifyTasksUsesCase: ModifyTaskUseCase,
    private val getWeeklyTasksUseCase: GetWeeklyTasksUseCase,
    private val getDailyTasksUseCase: GetDailyTasksUseCase,
    private val getWeekRangeFromOffset: GetWeekRangeFromOffset,
    private val scope: CoroutineScope,
) : TasksStatistics {

    private val weekOffset: MutableStateFlow<Int> = MutableStateFlow(0)
    private val selectedWeekText: MutableStateFlow<String> = MutableStateFlow("...")
    private val selectedDay: MutableStateFlow<Int> =
        MutableStateFlow(WeekUtils.currentDayOfWeek().isoDayNumber)
    private val weeklyTasksState: MutableStateFlow<WeeklyTasksState> =
        MutableStateFlow(WeeklyTasksState.Loading())
    private val dailyTasksState: MutableStateFlow<DailyTasksState> =
        MutableStateFlow(DailyTasksState.Loading())

    private lateinit var collectDailyTasksJob: Job
    private lateinit var collectWeeklyTasksJob: Job


    override val uiState: StateFlow<TasksStatisticsState> = combine(
        weekOffset, selectedWeekText, selectedDay, weeklyTasksState, dailyTasksState
    ) { weekOffset, selectedWeekText, selectedDay, weeklyTasksState, dailyTasksState ->
        TasksStatisticsState(
            weekOffset = weekOffset,
            selectedWeekText = selectedWeekText,
            selectedDay = selectedDay,
            weeklyTasksState = weeklyTasksState,
            dailyTasksState = dailyTasksState
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksStatisticsState()
    )

    private val _events: Channel<TasksEvents> = Channel()
    override val events: Flow<TasksEvents>
        get() = _events.receiveAsFlow()

    init {
        getData()
    }

    private fun getData() {
        getWeeklyData()
        getDailyData()
    }

    private fun cancelCurrentJobs() {
        collectDailyTasksJob.cancel()
        collectWeeklyTasksJob.cancel()
    }

    private fun getDailyData() {
        collectDailyTasksJob = scope.launch {
            getDailyTasksUseCase(weekOffset = weekOffset.value,
                dayIsoNumber = selectedDay.value).collectLatest { tasks ->
                if (tasks.completedTasks.isNotEmpty() || tasks.pendingTasks.isNotEmpty()) {
                    dailyTasksState.update {
                        DailyTasksState.Data(tasks = tasks, dayTextValue = tasks.dateFormatted)
                    }
                } else dailyTasksState.update {
                    DailyTasksState.Empty(dayTextValue = tasks.dateFormatted)
                }
            }
        }
    }

    val che = Channel<Int>()

    private fun getWeeklyData() {
        collectWeeklyTasksJob = scope.launch {
            selectedWeekText.update { getWeekRangeFromOffset(weekOffset.value) }
            getWeeklyTasksUseCase(weekOffset = weekOffset.value).collectLatest { weeklyTasks ->
                if (weeklyTasks.isNotEmpty()) {
                    var totalTasksCount = 0
                    weeklyTasks.entries.forEach {
                        totalTasksCount += (it.value.completedTasksCount + it.value.pendingTasksCount)
                    }
                    weeklyTasksState.update {
                        WeeklyTasksState.Data(tasks = weeklyTasks, totalTaskCount = totalTasksCount)
                    }
                } else weeklyTasksState.update { WeeklyTasksState.Empty(tasks = weeklyTasks) }
            }
        }
    }

    override fun selectDay(selectedDayIsoNumber: Int) {
        dailyTasksState.update { DailyTasksState.Loading(dayTextValue = it.dayText) }
        selectedDay.update { selectedDayIsoNumber }
        cancelCurrentJobs()
        getDailyData()
    }

    override fun updateWeekOffset(weekOffsetValue: Int) {
        weeklyTasksState.update { WeeklyTasksState.Loading(totalTaskCount = it.totalWeekTasksCount) }
        weekOffset.update { weekOffsetValue }
        cancelCurrentJobs()
        getData()
    }

    override fun toggleDone(task: Task, isDone: Boolean) {
        modifyTasksUsesCase.toggleTaskDone(task, isDone)
        _events.trySend(TasksEvents.ShowMessageDone(isDone))
    }

    override fun navigateToTaskDetails(taskId: String) {
        _events.trySend(TasksEvents.Navigation.NavigateToTaskDetails(taskId))
    }
}