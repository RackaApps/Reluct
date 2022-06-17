package work.racka.reluct.android.screens.tasks.search

import android.content.Context
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import work.racka.reluct.android.screens.R
import work.racka.reluct.common.features.tasks.viewmodels.SearchTasksViewModel
import work.racka.reluct.common.model.states.tasks.TasksEvents

@Composable
fun TasksSearchScreen(
    onNavigateToTaskDetails: (taskId: String) -> Unit,
    onBackClicked: () -> Unit,
) {
    val scaffoldState = rememberScaffoldState()

    val viewModel: SearchTasksViewModel by viewModel()
    val uiState by viewModel.host.uiState.collectAsState()
    val events by viewModel.host.events.collectAsState(initial = TasksEvents.Nothing)

    val context = LocalContext.current

    LaunchedEffect(events) {
        handleEvents(
            context = context,
            events = events,
            scope = this,
            scaffoldState = scaffoldState,
            navigateToTaskDetails = { taskId -> onNavigateToTaskDetails(taskId) },
            goBack = onBackClicked
        )
    }

    TasksSearchUI(scaffoldState = scaffoldState,
        uiState = uiState,
        fetchMoreData = { viewModel.host.fetchMoreData() },
        onSearch = { viewModel.host.search(it) },
        onTaskClicked = { viewModel.host.navigateToTaskDetails(it.id) },
        onToggleTaskDone = { isDone, task ->
            viewModel.host.toggleDone(task, isDone)
        }
    )
}

private fun handleEvents(
    context: Context,
    events: TasksEvents,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navigateToTaskDetails: (taskId: String) -> Unit,
    goBack: () -> Unit,
) {
    when (events) {
        is TasksEvents.ShowMessage -> {
            scope.launch {
                val result = scaffoldState.snackbarHostState.showSnackbar(
                    message = events.msg,
                    duration = SnackbarDuration.Short
                )
            }
        }
        is TasksEvents.ShowMessageDone -> {
            val msg = if (events.isDone) context.getString(R.string.task_marked_as_done, events.msg)
            else context.getString(R.string.task_marked_as_not_done, events.msg)
            scope.launch {
                val result = scaffoldState.snackbarHostState.showSnackbar(
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