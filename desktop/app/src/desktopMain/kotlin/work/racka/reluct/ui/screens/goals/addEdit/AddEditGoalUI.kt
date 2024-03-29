package work.racka.reluct.ui.screens.goals.addEdit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import work.racka.reluct.common.features.goals.addEditGoal.states.AddEditGoalState
import work.racka.reluct.common.features.goals.addEditGoal.states.GoalAppsState
import work.racka.reluct.common.features.goals.addEditGoal.states.ModifyGoalState
import work.racka.reluct.common.model.domain.appInfo.AppInfo
import work.racka.reluct.common.model.domain.goals.Goal
import work.racka.reluct.compose.common.components.SharedRes
import work.racka.reluct.compose.common.components.bottomSheet.addEditGoal.LazyColumnAddEditGoal
import work.racka.reluct.compose.common.components.buttons.OutlinedReluctButton
import work.racka.reluct.compose.common.components.buttons.ReluctButton
import work.racka.reluct.compose.common.components.dialogs.DiscardPromptDialog
import work.racka.reluct.compose.common.components.images.LottieAnimationWithDescription
import work.racka.reluct.compose.common.components.resources.stringResource
import work.racka.reluct.compose.common.components.topBar.ReluctSmallTopAppBar
import work.racka.reluct.compose.common.components.util.EditTitles
import work.racka.reluct.compose.common.theme.Dimens
import work.racka.reluct.compose.common.theme.Shapes
import work.racka.reluct.ui.screens.screenTime.components.ManageAppsDialog

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun AddEditGoalUI(
    snackbarState: SnackbarHostState,
    uiState: State<AddEditGoalState>,
    onSave: () -> Unit,
    onCreateNewGoal: () -> Unit,
    onSyncRelatedApps: () -> Unit,
    onUpdateGoal: (goal: Goal) -> Unit,
    onModifyApps: (appInfo: AppInfo, isAdd: Boolean) -> Unit,
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val modifyGoalState = remember { derivedStateOf { uiState.value.modifyGoalState } }

    val titles = getTitles(modifyGoalStateProvider = { modifyGoalState.value })

    val canGoBack by remember {
        derivedStateOf {
            modifyGoalState.value !is ModifyGoalState.Data
        }
    }
    val openExitDialog = remember { mutableStateOf(false) }
    val openRelatedAppsDialog = remember { mutableStateOf(false) }

    // Call this when you trying to Go Back safely!
    fun goBackAttempt(canGoBack: Boolean) {
        if (canGoBack) onGoBack() else openExitDialog.value = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ReluctSmallTopAppBar(
                title = titles.value.appBarTitle,
                navigationIcon = {
                    IconButton(onClick = { goBackAttempt(canGoBack) }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = Dimens.MediumPadding.size)
                .fillMaxSize()
        ) {
            AnimatedContent(
                targetState = modifyGoalState.value,
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { targetState ->
                when (targetState) {
                    is ModifyGoalState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is ModifyGoalState.NotFound -> {
                        LottieAnimationWithDescription(
                            lottieResource = SharedRes.files.no_data,
                            imageSize = 300.dp,
                            description = stringResource(SharedRes.strings.goal_not_found_text),
                            descriptionTextStyle = MaterialTheme.typography.bodyLarge
                        )
                    }
                    is ModifyGoalState.Saved -> {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement
                                .spacedBy(Dimens.MediumPadding.size)
                        ) {
                            LottieAnimationWithDescription(
                                lottieResource = SharedRes.files.task_saved,
                                imageSize = 300.dp,
                                description = null,
                                descriptionTextStyle = MaterialTheme.typography.bodyLarge
                            )
                            ReluctButton(
                                buttonText = stringResource(SharedRes.strings.new_goal_text),
                                icon = Icons.Rounded.Add,
                                shape = Shapes.large,
                                buttonColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                onButtonClicked = onCreateNewGoal
                            )
                            OutlinedReluctButton(
                                buttonText = stringResource(SharedRes.strings.exit_text),
                                icon = Icons.Rounded.ArrowBack,
                                shape = Shapes.large,
                                borderColor = MaterialTheme.colorScheme.primary,
                                onButtonClicked = onGoBack
                            )
                        }
                    }
                    else -> {}
                }
            }

            EditGoalList(
                getModifyGoalState = { modifyGoalState.value },
                onUpdateGoal = onUpdateGoal,
                onGoBack = { goBackAttempt(canGoBack) },
                onSyncRelatedApps = onSyncRelatedApps,
                onOpenAppsDialog = { openRelatedAppsDialog.value = true },
                onSave = onSave
            )
        }
    }

    // Manage Related Apps
    ManageAppsDialog(
        openDialog = openRelatedAppsDialog,
        onDismiss = { openRelatedAppsDialog.value = false },
        isLoadingProvider = { uiState.value.appsState is GoalAppsState.Loading },
        topItemsHeading = stringResource(SharedRes.strings.selected_apps_text),
        bottomItemsHeading = stringResource(SharedRes.strings.apps_text),
        topItems = { uiState.value.appsState.selectedApps },
        bottomItems = { uiState.value.appsState.unselectedApps },
        onTopItemClicked = { app -> onModifyApps(app, false) },
        onBottomItemClicked = { app -> onModifyApps(app, true) }
    )

    // Discard Dialog
    DiscardPromptDialog(
        dialogTitleProvider = { titles.value.dialogTitle },
        dialogTextProvider = { SharedRes.strings.delete_goal_message.localized() },
        openDialog = openExitDialog,
        onClose = { openExitDialog.value = false },
        onPositiveClick = onGoBack
    )
}

@Composable
private fun EditGoalList(
    getModifyGoalState: () -> ModifyGoalState,
    onUpdateGoal: (goal: Goal) -> Unit,
    onGoBack: () -> Unit,
    onSyncRelatedApps: () -> Unit,
    onOpenAppsDialog: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val goalState = getModifyGoalState()

    if (goalState is ModifyGoalState.Data) {
        LazyColumnAddEditGoal(
            modifier = modifier,
            goal = goalState.goal,
            onUpdateGoal = onUpdateGoal,
            onDiscard = onGoBack,
            onSave = { onSave() },
            onShowAppPicker = {
                onOpenAppsDialog()
                onSyncRelatedApps()
            }
        )
    }
}

@Composable
private fun getTitles(modifyGoalStateProvider: () -> ModifyGoalState) = remember {
    derivedStateOf {
        when (val goalState = modifyGoalStateProvider()) {
            is ModifyGoalState.Data -> {
                if (goalState.isEdit) {
                    EditTitles(
                        appBarTitle = SharedRes.strings.edit_goal_text.localized(),
                        dialogTitle = SharedRes.strings.discard_changes_text.localized()
                    )
                } else {
                    EditTitles(
                        appBarTitle = SharedRes.strings.add_goal_text.localized(),
                        dialogTitle = SharedRes.strings.discard_goal_text.localized()
                    )
                }
            }
            else -> EditTitles(
                appBarTitle = "• • •",
                dialogTitle = SharedRes.strings.discard_goal_text.localized()
            )
        }
    }
}
