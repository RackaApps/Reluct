package work.racka.reluct.ui.screens.tasks.statistics

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.launch
import work.racka.common.mvvm.koin.decompose.commonViewModel
import work.racka.reluct.common.features.tasks.statistics.TasksStatisticsViewModel
import work.racka.reluct.common.model.states.tasks.TasksEvents
import work.racka.reluct.compose.common.components.SharedRes
import work.racka.reluct.ui.screens.ComposeRenderer

class TasksStatisticsComponent(
    componentContext: ComponentContext,
    private val onShowDetails: (taskId: String) -> Unit,
) : ComponentContext by componentContext, ComposeRenderer {

    private val viewModel by commonViewModel<TasksStatisticsViewModel>()

    @Composable
    override fun Render(modifier: Modifier) {
        val snackbarState = remember { SnackbarHostState() }
        val uiState = viewModel.uiState.collectAsState()
        val events = viewModel.events.collectAsState(initial = TasksEvents.Nothing)

        HandleEvents(eventsState = events, snackbarState = snackbarState)

        TasksStatisticsUI(
            modifier = modifier,
            snackbarState = snackbarState,
            uiState = uiState,
            onSelectDay = viewModel::selectDay,
            onTaskClicked = { onShowDetails(it.id) },
            onToggleTaskDone = viewModel::toggleDone,
            onUpdateWeekOffset = viewModel::updateWeekOffset
        )
    }

    @Composable
    private fun HandleEvents(
        eventsState: State<TasksEvents>,
        snackbarState: SnackbarHostState,
    ) {
        LaunchedEffect(eventsState.value) {
            when (val events = eventsState.value) {
                is TasksEvents.ShowMessageDone -> {
                    val msg = if (events.isDone) {
                        StringDesc.ResourceFormatted(
                            SharedRes.strings.task_marked_as_done,
                            events.msg
                        ).localized()
                    } else {
                        StringDesc.ResourceFormatted(
                            SharedRes.strings.task_marked_as_not_done,
                            events.msg
                        ).localized()
                    }
                    launch {
                        snackbarState.showSnackbar(
                            message = msg,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
