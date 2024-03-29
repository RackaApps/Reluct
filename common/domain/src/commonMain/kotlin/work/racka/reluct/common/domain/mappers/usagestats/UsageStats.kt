package work.racka.reluct.common.domain.mappers.usagestats

import kotlinx.collections.immutable.toImmutableList
import work.racka.reluct.common.app.usage.stats.model.DataUsageStats
import work.racka.reluct.common.domain.usecases.appInfo.GetAppInfo
import work.racka.reluct.common.model.domain.usagestats.UsageStats
import work.racka.reluct.common.model.util.time.StatisticsTimeUtils
import work.racka.reluct.common.model.util.time.TimeUtils

/**
 * If showShortIntervalAsDay is true then it will show Today, Yesterday, Tomorrow
 * or a day instead of the full date when the interval is within a 3 days difference.
 * If false it will show the full date even if the interval is within a 3 days difference
 */
suspend fun DataUsageStats.asUsageStats(
    weekOffset: Int,
    dayIsoNumber: Int,
    showIntervalAsDay: Boolean = true,
    getAppInfo: GetAppInfo
): UsageStats {
    val selectedDayDateTimeString = StatisticsTimeUtils.selectedDayDateTimeString(
        weekOffset = weekOffset,
        selectedDayIsoNumber = dayIsoNumber
    )
    val totalScreenTime = appsUsageList.sumOf { it.timeInForeground }
    return UsageStats(
        appsUsageList = this.appsUsageList.map { it.asAppUsageInfo(getAppInfo) }.toImmutableList(),
        dateFormatted = TimeUtils.getFormattedDateString(
            dateTime = selectedDayDateTimeString,
            showShortIntervalAsDay = showIntervalAsDay
        ),
        totalScreenTime = totalScreenTime,
        formattedTotalScreenTime = TimeUtils
            .getFormattedTimeDurationString(totalScreenTime),
        unlockCount = this.unlockCount
    )
}
