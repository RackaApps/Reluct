package work.racka.reluct.common.data.usecases.app_usage

import work.racka.reluct.common.model.domain.usagestats.AppUsageStats

interface GetWeeklyAppUsageInfoUseCase {
    suspend operator fun invoke(weekOffset: Int, packageName: String): List<AppUsageStats>
}