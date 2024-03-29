package work.racka.reluct.compose.common.components.bottomSheet.addEditGoal

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import work.racka.reluct.common.model.domain.goals.Goal
import work.racka.reluct.common.model.domain.goals.GoalInterval
import work.racka.reluct.common.model.domain.goals.GoalType
import work.racka.reluct.common.model.util.time.TimeUtils
import work.racka.reluct.common.model.util.time.TimeUtils.plus
import work.racka.reluct.compose.common.components.SharedRes
import work.racka.reluct.compose.common.components.buttons.ReluctButton
import work.racka.reluct.compose.common.components.resources.stringResource
import work.racka.reluct.compose.common.components.textfields.ReluctTextField
import work.racka.reluct.compose.common.components.util.imePadding
import work.racka.reluct.compose.common.components.util.navigationBarsPadding
import work.racka.reluct.compose.common.theme.Dimens
import work.racka.reluct.compose.common.theme.Shapes

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun LazyColumnAddEditGoal(
    goal: Goal,
    onUpdateGoal: (Goal) -> Unit,
    onDiscard: () -> Unit,
    onSave: (goal: Goal) -> Unit,
    onShowAppPicker: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val focusRequest = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var goalNameError by remember { mutableStateOf(false) }

    val localDateTimeRange by remember(goal.goalDuration.timeRangeInMillis) {
        derivedStateOf {
            goal.goalDuration.timeRangeInMillis?.let { range ->
                val start = TimeUtils.epochMillisToLocalDateTime(range.first)
                val end = TimeUtils.epochMillisToLocalDateTime(range.last)
                start..end
            } ?: run {
                val start = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                val end = start.plus(days = 1)
                println(start..end)
                return@run start..end
            }
        }
    }

    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement
            .spacedBy(Dimens.MediumPadding.size),
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth()
    ) {
        item {
            ReluctTextField(
                value = goal.name,
                hint = stringResource(SharedRes.strings.goal_name_text),
                isError = goalNameError,
                errorText = stringResource(SharedRes.strings.goal_name_error_text),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequest.moveFocus(FocusDirection.Next) }
                ),
                onTextChange = { text ->
                    onUpdateGoal(goal.copy(name = text))
                }
            )
        }

        item {
            ReluctTextField(
                modifier = Modifier
                    .height(200.dp),
                value = goal.description,
                hint = stringResource(SharedRes.strings.description_hint),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                onTextChange = { text ->
                    onUpdateGoal(goal.copy(description = text))
                }
            )
        }

        // Goal Type Selector
        item {
            AddEditGoalItemTitle(text = stringResource(SharedRes.strings.goal_type_text))
            Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
            GoalTypeSelector(
                selectedGoalType = goal.goalType,
                onSelectGoalType = { type -> onUpdateGoal(goal.copy(goalType = type)) }
            )
        }

        // Show Apps Selector
        if (goal.goalType == GoalType.AppScreenTimeGoal) {
            item {
                AddEditGoalItemTitle(
                    modifier = Modifier
                        .animateItemPlacement(),
                    text = stringResource(SharedRes.strings.app_list_header),
                )
                Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
                SelectedAppsCard(
                    modifier = Modifier.animateItemPlacement(),
                    apps = goal.relatedApps,
                    onEditApps = onShowAppPicker
                )
            }
        }

        // Target Value Manipulation
        goalTargetValuePicker(
            keyboardController = keyboardController,
            goalType = goal.goalType,
            targetValue = goal.targetValue,
            onUpdateTargetValue = { onUpdateGoal(goal.copy(targetValue = it)) }
        )

        // Goal Interval Selector
        item {
            AddEditGoalItemTitle(
                modifier = Modifier.animateItemPlacement(),
                text = stringResource(SharedRes.strings.interval_text)
            )
            Spacer(modifier = Modifier.height(Dimens.SmallPadding.size))
            GoalIntervalSelector(
                modifier = Modifier.animateItemPlacement(),
                selectedGoalInterval = goal.goalDuration.goalInterval,
                onSelectGoalInterval = { interval ->
                    val duration = goal.goalDuration.let {
                        if (interval == GoalInterval.Custom) {
                            it.copy(goalInterval = interval)
                        } else {
                            it.copy(goalInterval = interval, timeRangeInMillis = null)
                        }
                    }
                    onUpdateGoal(goal.copy(goalDuration = duration))
                }
            )
        }

        // Goal Interval Duration Selection
        goalDurationPicker(
            dateTimeRange = localDateTimeRange,
            currentDaysOfWeek = goal.goalDuration.selectedDaysOfWeek,
            goalInterval = goal.goalDuration.goalInterval,
            onDateTimeRangeChange = { dateTimeRange ->
                val start = dateTimeRange.start.toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds()
                val end = dateTimeRange.endInclusive.toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds()
                val duration = goal.goalDuration.copy(timeRangeInMillis = start..end)
                onUpdateGoal(goal.copy(goalDuration = duration))
            },
            onUpdateDaysOfWeek = { daysOfWeek ->
                val duration = goal.goalDuration.copy(selectedDaysOfWeek = daysOfWeek)
                onUpdateGoal(goal.copy(goalDuration = duration))
            }
        )

        // Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReluctButton(
                    buttonText = stringResource(SharedRes.strings.discard_button_text),
                    icon = Icons.Rounded.DeleteSweep,
                    onButtonClicked = onDiscard,
                    shape = Shapes.large,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    buttonColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(Dimens.MediumPadding.size))
                ReluctButton(
                    buttonText = stringResource(SharedRes.strings.save_button_text),
                    icon = Icons.Rounded.Save,
                    shape = Shapes.large,
                    buttonColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onButtonClicked = {
                        val isTitleBlank = goal.name.isBlank()
                        goalNameError = isTitleBlank
                        if (!isTitleBlank) onSave(goal)
                    }
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }
}

/*
@Preview
@Composable
private fun AddEditGoalPreview() {
    ReluctAppTheme {
        Surface(color = MaterialTheme.colorScheme.background.copy(.7f)) {
            LazyColumnAddEditGoal(
                goal = PreviewData.goals.last(),
                onUpdateGoal = {},
                onDiscard = {},
                onSave = {},
                onShowAppPicker = {}
            )
        }
    }
}*/
