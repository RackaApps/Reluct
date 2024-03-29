package work.racka.reluct.android.screens.tasks.search

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import work.racka.common.mvvm.koin.compose.getCommonViewModel
import work.racka.reluct.android.screens.R
import work.racka.reluct.common.features.tasks.searchTasks.SearchTasksViewModel
import work.racka.reluct.common.model.states.tasks.TasksEvents

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun TasksSearchScreen(
    onNavigateToTaskDetails: (taskId: String) -> Unit,
    onBackClicked: () -> Unit,
) {
    val snackbarState = remember { SnackbarHostState() }

    val viewModel: SearchTasksViewModel = getCommonViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val events = viewModel.events.collectAsStateWithLifecycle(initialValue = TasksEvents.Nothing)

    val context = LocalContext.current
    HandleEvents(
        context = context,
        eventsState = events,
        snackbarState = snackbarState,
        navigateToTaskDetails = { id -> onNavigateToTaskDetails(id) },
        goBack = onBackClicked
    )

    TasksSearchUI(
        snackbarState = snackbarState,
        uiState = uiState,
        fetchMoreData = viewModel::fetchMoreData,
        onSearch = viewModel::search,
        onTaskClicked = { viewModel.navigateToTaskDetails(it.id) },
        onToggleTaskDone = viewModel::toggleDone
    )
}

@Composable
private fun HandleEvents(
    context: Context,
    eventsState: State<TasksEvents>,
    snackbarState: SnackbarHostState,
    navigateToTaskDetails: (taskId: String) -> Unit,
    goBack: () -> Unit,
) {
    LaunchedEffect(eventsState.value) {
        when (val events = eventsState.value) {
            is TasksEvents.ShowMessage -> {
                launch {
                    snackbarState.showSnackbar(
                        message = events.msg,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            is TasksEvents.ShowMessageDone -> {
                val msg = if (events.isDone) {
                    context.getString(R.string.task_marked_as_done, events.msg)
                } else {
                    context.getString(R.string.task_marked_as_not_done, events.msg)
                }
                launch {
                    snackbarState.showSnackbar(
                        message = msg,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            is TasksEvents.Navigation.NavigateToTaskDetails -> navigateToTaskDetails(events.taskId)
            is TasksEvents.Navigation.GoBack -> goBack()
            else -> {}
        }
    }
}
