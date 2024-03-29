package work.racka.reluct.common.core.navigation.composeDestinations.tasks

import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import work.racka.reluct.common.core.navigation.destination.NavDefaults
import work.racka.reluct.common.core.navigation.destination.NavDestination

object PendingTasksDestination :
    NavDestination {
    private const val APP_URI = NavDefaults.APP_DESTINATION_URI
    private const val BASE_URI = "pending_tasks"
    override val route: String = "$BASE_URI-route"
    override val destination: String = "$BASE_URI-destination"

    override val deepLinks: List<NavDeepLink>
        get() = listOf(
            navDeepLink {
                uriPattern = pendingTasksDeeplink()
            }
        )

    fun pendingTasksDeeplink() = "$APP_URI/$BASE_URI"
}
