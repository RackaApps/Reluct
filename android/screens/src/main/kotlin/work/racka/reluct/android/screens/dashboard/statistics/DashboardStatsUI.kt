package work.racka.reluct.android.screens.dashboard.statistics

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import work.racka.reluct.android.screens.R
import work.racka.reluct.android.screens.screentime.components.ShowAppTimeLimitDialog
import work.racka.reluct.android.screens.screentime.components.getWeeklyDeviceScreenTimeChartData
import work.racka.reluct.android.screens.tasks.components.getWeeklyTasksBarChartData
import work.racka.reluct.android.screens.util.BottomBarVisibilityHandler
import work.racka.reluct.android.screens.util.getSnackbarModifier
import work.racka.reluct.common.features.screenTime.statistics.states.allStats.ScreenTimeStatsState
import work.racka.reluct.common.features.screenTime.statistics.states.allStats.WeeklyUsageStatsState
import work.racka.reluct.common.model.domain.usagestats.AppUsageInfo
import work.racka.reluct.common.model.states.tasks.TasksStatisticsState
import work.racka.reluct.common.model.states.tasks.WeeklyTasksState
import work.racka.reluct.compose.common.components.buttons.OutlinedReluctButton
import work.racka.reluct.compose.common.components.cards.appUsageEntry.AppUsageEntryBase
import work.racka.reluct.compose.common.components.cards.headers.ListGroupHeadingHeader
import work.racka.reluct.compose.common.components.cards.statistics.BarChartDefaults
import work.racka.reluct.compose.common.components.cards.statistics.screenTime.ScreenTimeStatisticsCard
import work.racka.reluct.compose.common.components.cards.statistics.tasks.TasksStatisticsCard
import work.racka.reluct.compose.common.components.util.BarsVisibility
import work.racka.reluct.compose.common.components.util.rememberScrollContext
import work.racka.reluct.compose.common.theme.Dimens
import work.racka.reluct.compose.common.theme.Shapes

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardStatsUI(
    mainScaffoldPadding: PaddingValues,
    barsVisibility: BarsVisibility,
    snackbarState: SnackbarHostState,
    screenTimeUiState: State<ScreenTimeStatsState>,
    tasksStatsUiState: State<TasksStatisticsState>,
    onTasksSelectDay: (day: Int) -> Unit,
    onScreenTimeSelectDay: (day: Int) -> Unit,
    onSelectAppTimeLimit: (packageName: String) -> Unit,
    onSaveTimeLimit: (hours: Int, minutes: Int) -> Unit,
    onAppUsageInfoClick: (appInfo: AppUsageInfo) -> Unit,
    onViewAllScreenTimeStats: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scrollContext = rememberScrollContext(listState = listState)

    BottomBarVisibilityHandler(
        scrollContext = scrollContext,
        barsVisibility = barsVisibility
    )

    // Bar Charts
    val barColor = BarChartDefaults.barColor
    // Screen Time Chart
    val screenTimeChartData = getWeeklyDeviceScreenTimeChartData(
        weeklyStatsProvider = { screenTimeUiState.value.weeklyData.usageStats },
        isLoadingProvider = { screenTimeUiState.value.weeklyData is WeeklyUsageStatsState.Loading },
        barColor = barColor
    )

    // Tasks Stats Chart
    val tasksChartData = getWeeklyTasksBarChartData(
        weeklyTasksProvider = { tasksStatsUiState.value.weeklyTasksState.weeklyTasks },
        isLoadingProvider = { tasksStatsUiState.value.weeklyTasksState is WeeklyTasksState.Loading },
        barColor = barColor
    )

    val showAppTimeLimitDialog = remember { mutableStateOf(false) }

    val snackbarModifier = getSnackbarModifier(
        mainPadding = mainScaffoldPadding,
        scrollContext = scrollContext
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarState) { data ->
                Snackbar(
                    modifier = snackbarModifier.value,
                    shape = RoundedCornerShape(10.dp),
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .animateContentSize()
                .padding(padding)
                .padding(horizontal = Dimens.MediumPadding.size)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement
                    .spacedBy(Dimens.SmallPadding.size)
            ) {
                // Screen Time
                stickyHeader {
                    ListGroupHeadingHeader(text = stringResource(R.string.screen_time_text))
                }

                // Screen Time Chart
                item {
                    ScreenTimeStatisticsCard(
                        chartData = screenTimeChartData,
                        selectedDayText = { screenTimeUiState.value.dailyData.dayText },
                        selectedDayScreenTime = {
                            screenTimeUiState.value.dailyData.usageStat.formattedTotalScreenTime
                        },
                        weeklyTotalScreenTime = { screenTimeUiState.value.weeklyData.formattedTotalTime },
                        selectedDayIsoNumber = { screenTimeUiState.value.selectedInfo.selectedDay },
                        onBarClicked = { onScreenTimeSelectDay(it) },
                        weekUpdateButton = {
                            // Show 2 Apps
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                screenTimeUiState.value.dailyData.usageStat.appsUsageList.take(3)
                                    .forEach { item ->
                                        AppUsageEntryBase(
                                            modifier = Modifier
                                                .padding(vertical = Dimens.SmallPadding.size)
                                                .fillMaxWidth()
                                                .clip(Shapes.large)
                                                .clickable { onAppUsageInfoClick(item) },
                                            appUsageInfo = item,
                                            onTimeSettingsClick = {
                                                onSelectAppTimeLimit(item.packageName)
                                                showAppTimeLimitDialog.value = true
                                            },
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                OutlinedReluctButton(
                                    modifier = Modifier.padding(vertical = Dimens.SmallPadding.size),
                                    buttonText = stringResource(id = R.string.view_all_text),
                                    icon = null,
                                    onButtonClicked = onViewAllScreenTimeStats,
                                    borderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    buttonTextStyle = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    )
                }

                // Tasks Stats
                stickyHeader {
                    ListGroupHeadingHeader(text = stringResource(R.string.tasks_text))
                }

                // Tasks Chart
                item {
                    TasksStatisticsCard(
                        chartData = tasksChartData,
                        selectedDayText = { tasksStatsUiState.value.dailyTasksState.dayText },
                        selectedDayTasksDone = {
                            tasksStatsUiState.value.dailyTasksState.dailyTasks.completedTasksCount
                        },
                        selectedDayTasksPending = {
                            tasksStatsUiState.value.dailyTasksState.dailyTasks.pendingTasksCount
                        },
                        totalWeekTaskCount = { tasksStatsUiState.value.weeklyTasksState.totalWeekTasksCount },
                        selectedDayIsoNumber = { tasksStatsUiState.value.selectedDay },
                        onBarClicked = { onTasksSelectDay(it) },
                        weekUpdateButton = {}
                    )
                }

                // Bottom Space for spaceBy
                item {
                    Spacer(
                        modifier = Modifier.padding(mainScaffoldPadding)
                    )
                }
            }
        }
    }

    // Dialogs
    // App Time Limit Dialog
    ShowAppTimeLimitDialog(
        openDialog = showAppTimeLimitDialog,
        limitStateProvider = { screenTimeUiState.value.appTimeLimit },
        onSaveTimeLimit = onSaveTimeLimit,
        onClose = { showAppTimeLimitDialog.value = false }
    )
}
