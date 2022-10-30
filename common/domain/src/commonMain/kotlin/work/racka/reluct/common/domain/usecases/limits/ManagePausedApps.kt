package work.racka.reluct.common.domain.usecases.limits

import kotlinx.coroutines.flow.Flow
import work.racka.reluct.common.model.domain.appInfo.AppInfo

interface ManagePausedApps {
    /**
     * Returns a [Flow] of pair of two Lists typed [AppInfo]
     * The first value of the pair is a list of Paused apps
     * The second value of the pair is a list of Non Paused Apps present in the user device
     */
    suspend fun invoke(): Flow<Pair<List<AppInfo>, List<AppInfo>>>

    suspend fun pauseApp(packageName: String)
    suspend fun unPauseApp(packageName: String)
    suspend fun isPaused(packageName: String): Boolean
}