package work.racka.reluct.common.app.usage.stats.model

data class DataUsageStats(
    val appsUsageList: List<DataAppUsageInfo>,
    val unlockCount: Long,
)
