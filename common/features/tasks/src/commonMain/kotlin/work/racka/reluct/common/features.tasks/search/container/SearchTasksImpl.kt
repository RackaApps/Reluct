package work.racka.reluct.common.features.tasks.search.container

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import work.racka.reluct.common.features.tasks.search.repository.SearchTasksRepository
import work.racka.reluct.common.model.states.tasks.TasksSideEffect
import work.racka.reluct.common.model.states.tasks.TasksState

internal class SearchTasksImpl(
    private val searchTasks: SearchTasksRepository,
    private val scope: CoroutineScope
) : SearchTasks {

    private val _uiState: MutableStateFlow<TasksState> =
        MutableStateFlow(TasksState.Loading)
    private val _events: Channel<TasksSideEffect> = Channel()

    override val uiState: StateFlow<TasksState>
        get() = _uiState

    override val events: Flow<TasksSideEffect>
        get() = _events.receiveAsFlow()

    override fun searchTasks(query: String) {
        scope.launch {
            searchTasks.search(query).collectLatest { taskList ->
                _uiState.update { TasksState.SearchTask(taskList) }
            }
        }
    }

    override fun toggleDone(taskId: Long, isDone: Boolean) {
        searchTasks.toggleDone(taskId, isDone)
        _events.trySend(TasksSideEffect.TaskDone(isDone))
    }

    override fun navigateToTaskDetails(taskId: Long) {
        _events.trySend(TasksSideEffect.Navigation.NavigateToTaskDetails(taskId))
    }

    override fun goBack() {
        _events.trySend(TasksSideEffect.Navigation.GoBack)
    }
}