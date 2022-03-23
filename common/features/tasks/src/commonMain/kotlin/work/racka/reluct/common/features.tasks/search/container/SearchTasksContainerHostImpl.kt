package work.racka.reluct.common.features.tasks.search.container

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import work.racka.reluct.common.features.tasks.search.repository.SearchTasksRepository
import work.racka.reluct.common.model.states.tasks.TasksSideEffect
import work.racka.reluct.common.model.states.tasks.TasksState

class SearchTasksContainerHostImpl(
    private val searchTasks: SearchTasksRepository,
    scope: CoroutineScope
) : SearchTasksContainerHost, ContainerHost<TasksState, TasksSideEffect> {

    override val container: Container<TasksState, TasksSideEffect> =
        scope.container(TasksState.EmptySearchTask)

    override val uiState: StateFlow<TasksState>
        get() = container.stateFlow

    override val sideEffect: Flow<TasksSideEffect>
        get() = container.sideEffectFlow

    override fun searchTasks(query: String) = intent {
        searchTasks.search(query).collectLatest { taskList ->
            reduce { TasksState.SearchTask(taskList) }
        }
    }

    override fun toggleDone(taskId: Long, isDone: Boolean) = intent {
        searchTasks.toggleDone(taskId, isDone)
        postSideEffect(TasksSideEffect.TaskDone(isDone))
    }

    override fun navigateToTaskDetails(taskId: Long) = intent {
        postSideEffect(TasksSideEffect.Navigation.NavigateToTaskDetails(taskId))
    }

    override fun goBack() = intent {
        postSideEffect(TasksSideEffect.Navigation.GoBack)
    }
}