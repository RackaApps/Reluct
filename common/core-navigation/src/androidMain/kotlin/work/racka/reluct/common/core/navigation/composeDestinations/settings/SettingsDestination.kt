package work.racka.reluct.common.core.navigation.composeDestinations.settings

import work.racka.reluct.common.core.navigation.destination.NavDestination

object SettingsDestination : NavDestination {
    private const val BASE_URI = "settings"
    override val route: String = "$BASE_URI-route"
    override val destination: String = "$BASE_URI-destination"
}
