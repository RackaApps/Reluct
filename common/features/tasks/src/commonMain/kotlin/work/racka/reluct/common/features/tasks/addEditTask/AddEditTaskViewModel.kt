package work.racka.reluct.common.features.tasks.addEditTask

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import work.racka.common.mvvm.viewmodel.CommonViewModel
import work.racka.reluct.common.domain.usecases.tasks.ManageTaskLabels
import work.racka.reluct.common.domain.usecases.tasks.ModifyTaskUseCase
import work.racka.reluct.common.features.tasks.util.Constants
import work.racka.reluct.common.model.domain.tasks.EditTask
import work.racka.reluct.common.model.domain.tasks.TaskLabel
import work.racka.reluct.common.model.states.tasks.AddEditTaskState
import work.racka.reluct.common.model.states.tasks.ModifyTaskState
import work.racka.reluct.common.model.states.tasks.TasksEvents

class AddEditTaskViewModel(
    private val modifyTaskUseCase: ModifyTaskUseCase,
    private val manageTaskLabels: ManageTaskLabels,
    private val taskId: String?
) : CommonViewModel() {

    private val modifyTaskState: MutableStateFlow<ModifyTaskState> =
        MutableStateFlow(ModifyTaskState.Loading)
    private val availableTaskLabels: MutableStateFlow<ImmutableList<TaskLabel>> =
        MutableStateFlow(persistentListOf())

    val uiState: StateFlow<AddEditTaskState> = combine(
        modifyTaskState,
        availableTaskLabels
    ) { modifyTaskState, availableTaskLabels ->
        AddEditTaskState(
            modifyTaskState = modifyTaskState,
            availableTaskLabels = availableTaskLabels
        )
    }.stateIn(
        scope = vmScope,
        initialValue = AddEditTaskState(),
        started = SharingStarted.WhileSubscribed(5_000L)
    )

    private val eventsChannel: Channel<TasksEvents> = Channel()
    val events: Flow<TasksEvents>
        get() = eventsChannel.receiveAsFlow()

    init {
        getTask(taskId)
        getTaskLabels()
    }

    fun saveCurrentTask() {
        vmScope.launch {
            val taskState = modifyTaskState.value
            if (taskState is ModifyTaskState.Data) {
                modifyTaskUseCase.saveTask(taskState.task)
                val result = eventsChannel.trySend(TasksEvents.ShowMessage(Constants.TASK_SAVED))
                result.onSuccess {
                    /**
                     * Go back after saving if you are just editing a Task and the [taskId]
                     * is not null else just show the TaskSaved State for adding more tasks
                     */
                    /*if (taskId != null) {
                        eventsChannel.send(TasksEvents.Navigation.GoBack)
                    } else {
                        modifyTaskState.update { ModifyTaskState.Saved }
                    }*/
                    // Just exit the page
                    eventsChannel.send(TasksEvents.Navigation.GoBack)
                }
            }
        }
    }

    fun newTask() {
        modifyTaskState.update {
            ModifyTaskState.Data(
                isEdit = false,
                task = DefaultTasks.emptyEditTask()
            )
        }
    }

    fun updateCurrentTask(task: EditTask) {
        modifyTaskState.update { ModifyTaskState.Data(isEdit = taskId != null, task = task) }
    }

    fun saveLabel(label: TaskLabel) {
        vmScope.launch {
            manageTaskLabels.addLabel(label)
        }
    }

    fun deleteLabel(label: TaskLabel) {
        vmScope.launch {
            manageTaskLabels.deleteLabel(label.id)
        }
    }

    private fun getTaskLabels() {
        vmScope.launch {
            manageTaskLabels.allLabels().collectLatest { labels ->
                availableTaskLabels.update { labels }
            }
        }
    }

    private fun getTask(taskId: String?) {
        vmScope.launch {
            when (taskId) {
                null -> modifyTaskState.update {
                    ModifyTaskState.Data(isEdit = false, task = DefaultTasks.emptyEditTask())
                }
                else -> {
                    when (val task = modifyTaskUseCase.getTaskToEdit(taskId).firstOrNull()) {
                        null -> modifyTaskState.update { ModifyTaskState.NotFound }
                        else -> modifyTaskState.update {
                            ModifyTaskState.Data(
                                isEdit = true,
                                task = task
                            )
                        }
                    }
                }
            }
        }
    }
}
