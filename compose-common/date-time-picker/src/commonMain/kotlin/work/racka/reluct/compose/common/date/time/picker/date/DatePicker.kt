package work.racka.reluct.compose.common.date.time.picker.date

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import work.racka.reluct.common.model.util.time.TimeUtils.toDayOfWeekShortenedString
import work.racka.reluct.common.model.util.time.TimeUtils.toMonthShortenedString
import work.racka.reluct.common.model.util.time.TimeUtils.toMonthString
import work.racka.reluct.compose.common.date.time.picker.core.DateTimeDialog
import work.racka.reluct.compose.common.date.time.picker.core.DateTimeDialogState
import work.racka.reluct.compose.common.date.time.picker.core.rememberDateTimeDialogState
import work.racka.reluct.compose.common.date.time.picker.util.currentLocalDate
import work.racka.reluct.compose.common.date.time.picker.util.isSmallDevice
import work.racka.reluct.compose.common.pager.core.ExperimentalPagerApi
import work.racka.reluct.compose.common.pager.core.HorizontalPager
import work.racka.reluct.compose.common.pager.core.PagerState
import work.racka.reluct.compose.common.pager.core.rememberPagerState

/**
 * @brief A date picker body layout
 *
 * @param initialDate time to be shown to the user when the dialog is first shown.
 * Defaults to the current date if this is not set
 * @param yearRange the range of years the user should be allowed to pick from
 * @param waitForPositiveButton if true the [onDateChange] callback will only be called when the
 * positive button is pressed, otherwise it will be called on every input change
 * @param onDateChange callback with a LocalDateTime object when the user completes their input
 * @param allowedDateValidator when this returns true the date will be selectable otherwise it won't be
 */
@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    dialogState: DateTimeDialogState = rememberDateTimeDialogState(),
    initialDate: LocalDate = currentLocalDate(),
    title: String = "SELECT DATE",
    colors: DatePickerColors = DatePickerDefaults.colors(),
    yearRange: IntRange = IntRange(2000, 2100),
    waitForPositiveButton: Boolean = true,
    allowedDateValidator: (LocalDate) -> Boolean = { true },
    onDateChange: (LocalDate) -> Unit = {},
) {
    val datePickerState = remember {
        DatePickerState(initialDate, colors, yearRange)
    }

    DateTimeDialog(
        modifier = modifier,
        isVisible = dialogState.showing,
        onCloseDialog = { dialogState.hide() },
        onPositiveButtonClicked = {
            dialogState.hide()
            if (waitForPositiveButton) onDateChange(datePickerState.selected)
        },
        containerColor = colors.dialogBackgroundColor,
        shape = dialogState.dialogProperties.shape,
        positiveButtonText = dialogState.buttonText.positiveText,
        negativeButtonText = dialogState.buttonText.negativeText
    ) {
        DatePickerImpl(title = title, state = datePickerState, allowedDateValidator)
    }

    if (!waitForPositiveButton) {
        DisposableEffect(datePickerState.selected) {
            onDateChange(datePickerState.selected)
            onDispose { }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun DatePickerImpl(
    title: String,
    state: DatePickerState,
    allowedDateValidator: (LocalDate) -> Boolean,
) {
    val pagerState = rememberPagerState(
        initialPage = (state.selected.year - state.yearRange.first) * 12 + state.selected.monthNumber - 1
    )

    Column(Modifier.fillMaxWidth()) {
        CalendarHeader(title, state)
        HorizontalPager(
            count = (state.yearRange.last - state.yearRange.first + 1) * 12,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.height(336.dp)
        ) { page ->
            val viewDate = remember {
                LocalDate(
                    year = state.yearRange.first + page / 12,
                    monthNumber = page % 12 + 1,
                    dayOfMonth = 1
                )
            }

            Column {
                CalendarViewHeader(viewDate, state, pagerState)
                Box {
                    androidx.compose.animation.AnimatedVisibility(
                        state.yearPickerShowing,
                        modifier = Modifier
                            .zIndex(0.7f)
                            .clipToBounds(),
                        enter = slideInVertically(initialOffsetY = { -it }),
                        exit = slideOutVertically(targetOffsetY = { -it })
                    ) {
                        YearPicker(viewDate, state, pagerState)
                    }

                    CalendarView(viewDate, state, allowedDateValidator)
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun YearPicker(
    viewDate: LocalDate,
    state: DatePickerState,
    pagerState: PagerState,
) {
    val gridState = rememberLazyGridState((viewDate.year - state.yearRange.first) / 3)
    val coroutineScope = rememberCoroutineScope()

    LazyVerticalGrid(
        modifier = Modifier.background(state.colors.dialogBackgroundColor),
        columns = GridCells.Fixed(3),
        state = gridState,
    ) {
        itemsIndexed(state.yearRange.toList()) { _, item ->
            val selected = remember { item == viewDate.year }
            YearPickerItem(year = item, selected = selected, colors = state.colors) {
                if (!selected) {
                    coroutineScope.launch {
                        pagerState.scrollToPage(
                            pagerState.currentPage + (item - viewDate.year) * 12
                        )
                    }
                }
                state.yearPickerShowing = false
            }
        }
    }
}

@Composable
private fun YearPickerItem(
    year: Int,
    selected: Boolean,
    colors: DatePickerColors,
    onClick: () -> Unit,
) {
    Box(Modifier.size(88.dp, 52.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(72.dp, 36.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.dateBackgroundColor(selected).value)
                .clickable(
                    onClick = onClick,
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                year.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = colors.dateTextColor(selected).value
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CalendarViewHeader(
    viewDate: LocalDate,
    state: DatePickerState,
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()
    val month = remember { viewDate.month.toMonthString() }
    val yearDropdownIcon = remember(state.yearPickerShowing) {
        if (state.yearPickerShowing) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
    }

    Box(
        Modifier
            .padding(top = 16.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
            .height(24.dp)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .clickable(onClick = { state.yearPickerShowing = !state.yearPickerShowing })
        ) {
            Text(
                "$month ${viewDate.year}",
                modifier = Modifier
                    .paddingFromBaseline(top = 16.dp)
                    .wrapContentSize(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium,
                color = state.colors.calendarHeaderTextColor
            )

            Spacer(Modifier.width(4.dp))
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                Icon(
                    yearDropdownIcon,
                    contentDescription = "Year Selector",
                    tint = state.colors.calendarHeaderTextColor
                )
            }
        }

        Row(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous Month",
                modifier = Modifier
                    .testTag("dialog_date_prev_month")
                    .size(24.dp)
                    .background(state.colors.headerBackgroundColor, CircleShape)
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage - 1 >= 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        }
                    ),
                tint = state.colors.headerTextColor
            )

            Spacer(modifier = Modifier.width(24.dp))

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Next Month",
                modifier = Modifier
                    .testTag("dialog_date_next_month")
                    .size(24.dp)
                    .background(state.colors.headerBackgroundColor, CircleShape)
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage + 1 < pagerState.pageCount) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                    ),
                tint = state.colors.headerTextColor
            )
        }
    }
}

@Composable
private fun CalendarView(
    viewDate: LocalDate,
    state: DatePickerState,
    allowedDateValidator: (LocalDate) -> Boolean,
) {
    Column(
        Modifier
            .padding(start = 12.dp, end = 12.dp)
            .testTag("dialog_date_calendar")
    ) {
        DayOfWeekHeader(state)
        val calendarDatesData = remember { getDates(viewDate) }
        val datesList = remember { IntRange(1, calendarDatesData.second).toList() }
        val possibleSelected = remember(state.selected) {
            viewDate.year == state.selected.year && viewDate.month == state.selected.month
        }

        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.height(240.dp)) {
            for (x in 0 until calendarDatesData.first) {
                item { Box(Modifier.size(40.dp)) }
            }

            items(datesList) {
                val selected = remember(state.selected) {
                    possibleSelected && it == state.selected.dayOfMonth
                }
                val date = viewDate.withDayOfMonth(it)
                val enabled = allowedDateValidator(date)
                DateSelectionBox(it, selected, state.colors, enabled) {
                    state.selected = date
                }
            }
        }
    }
}

@Composable
private fun DateSelectionBox(
    date: Int,
    selected: Boolean,
    colors: DatePickerColors,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .testTag("dialog_date_selection_$date")
            .size(40.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                onClick = { if (enabled) onClick() },
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.toString(),
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.dateBackgroundColor(selected).value)
                .wrapContentSize(Alignment.Center)
                .alpha(if (enabled) ContentAlpha.high else ContentAlpha.disabled),
            style = MaterialTheme.typography.labelMedium,
            color = colors.dateTextColor(selected).value
        )
    }
}

@Composable
private fun DayOfWeekHeader(state: DatePickerState) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            DatePickerState.dayHeaders.forEach { day ->
                item {
                    Box(Modifier.size(40.dp)) {
                        Text(
                            day,
                            modifier = Modifier
                                .alpha(0.8f)
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                            style = MaterialTheme.typography.bodyMedium,
                            color = state.colors.calendarHeaderTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(title: String, state: DatePickerState) {
    val month = remember(state.selected) { state.selected.month.toMonthShortenedString() }
    val day = remember(state.selected) { state.selected.dayOfWeek.toDayOfWeekShortenedString() }

    Box(
        Modifier
            .background(state.colors.headerBackgroundColor)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp)) {
            Text(
                text = title,
                modifier = Modifier.paddingFromBaseline(top = if (isSmallDevice()) 24.dp else 32.dp),
                color = state.colors.headerTextColor,
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = if (isSmallDevice()) 0.dp else 64.dp)
            ) {
                Text(
                    text = "$day, $month ${state.selected.dayOfMonth}",
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = state.colors.headerTextColor,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Spacer(Modifier.height(if (isSmallDevice()) 8.dp else 16.dp))
        }
    }
}

/**
 * Returns this date with another day of month.
 *
 * @throws IllegalArgumentException if the resulting date is invalid or exceeds the platform-specific boundary.
 */
private fun LocalDate.withDayOfMonth(dayOfMonth: Int) = LocalDate(this.year, this.month, dayOfMonth)

/**
 * The beginning of the next month.
 */
private val LocalDate.nextMonth
    get() = withDayOfMonth(1).plus(1, DateTimeUnit.MONTH)

/**
 * Get the LocalDate for the end of the month
 */
private val LocalDate.atEndOfMonth get() = nextMonth - DatePeriod(days = 1)

private fun getDates(date: LocalDate): Pair<Int, Int> {
    val numDays = date.atEndOfMonth.dayOfMonth
    val firstDay = date.withDayOfMonth(1).dayOfWeek.isoDayNumber % 7

    return Pair(firstDay, numDays)
}
