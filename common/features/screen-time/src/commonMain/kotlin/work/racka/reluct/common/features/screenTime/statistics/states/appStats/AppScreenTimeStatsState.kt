package work.racka.reluct.common.features.screenTime.statistics.states.appStats

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import work.racka.reluct.common.features.screenTime.statistics.states.ScreenTimeStatsSelectedInfo
import work.racka.reluct.common.model.domain.limits.AppTimeLimit
import work.racka.reluct.common.model.domain.usagestats.AppUsageStats
import work.racka.reluct.common.model.util.time.Week

data class AppScreenTimeStatsState(
    val selectedInfo: ScreenTimeStatsSelectedInfo = ScreenTimeStatsSelectedInfo(),
    val weeklyData: WeeklyAppUsageStatsState = WeeklyAppUsageStatsState.Empty,
    val dailyData: DailyAppUsageStatsState = DailyAppUsageStatsState.Empty,
    val appSettingsState: AppSettingsState = AppSettingsState.Nothing
)

sealed class WeeklyAppUsageStatsState(
    val usageStats: ImmutableMap<Week, AppUsageStats>,
    val formattedTotalTime: String
) {
    data class Data(
        private val weeklyUsageStats: ImmutableMap<Week, AppUsageStats>,
        private val weeklyFormattedTotalTime: String
    ) : WeeklyAppUsageStatsState(weeklyUsageStats, weeklyFormattedTotalTime)

    class Loading(
        weeklyUsageStats: ImmutableMap<Week, AppUsageStats> = persistentMapOf(),
        weeklyFormattedTotalTime: String = "..."
    ) : WeeklyAppUsageStatsState(weeklyUsageStats, weeklyFormattedTotalTime)

    object Empty : WeeklyAppUsageStatsState(persistentMapOf(), "...")
}

sealed class DailyAppUsageStatsState {
    data class Data(
        val usageStat: AppUsageStats,
        val dayText: String = usageStat.dateFormatted
    ) : DailyAppUsageStatsState()

    object Empty : DailyAppUsageStatsState()
}

sealed class AppSettingsState {
    object Nothing : AppSettingsState()
    object Loading : AppSettingsState()
    data class Data(
        val appTimeLimit: AppTimeLimit,
        val isDistractingApp: Boolean,
        val isPaused: Boolean
    ) : AppSettingsState()
}
