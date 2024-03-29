package work.racka.reluct.android.navigation.navhost.graphs.screentime

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import work.racka.reluct.android.navigation.transitions.scaleInEnterTransition
import work.racka.reluct.android.navigation.transitions.scaleInPopEnterTransition
import work.racka.reluct.android.navigation.transitions.scaleOutExitTransition
import work.racka.reluct.android.navigation.transitions.scaleOutPopExitTransition
import work.racka.reluct.android.navigation.util.NavHelpers
import work.racka.reluct.android.navigation.util.NavHelpers.popBackStackOrExitActivity
import work.racka.reluct.android.screens.screentime.appStatsDetails.AppScreenTimeStatsScreen
import work.racka.reluct.common.core.navigation.composeDestinations.screentime.AppScreenTimeStatsArgs
import work.racka.reluct.common.core.navigation.composeDestinations.screentime.AppScreenTimeStatsDestination
import work.racka.reluct.compose.common.components.util.BarsVisibility

@ExperimentalAnimationApi
fun NavGraphBuilder.appScreenTimeStatsNavGraph(
    navController: NavHostController,
    barsVisibility: BarsVisibility
) {
    navigation(
        route = AppScreenTimeStatsDestination.destination,
        startDestination = AppScreenTimeStatsDestination.route
    ) {
        // App Screen Time Stats
        composable(
            route = AppScreenTimeStatsDestination.route,
            arguments = AppScreenTimeStatsDestination.args,
            deepLinks = AppScreenTimeStatsDestination.deepLinks,
            enterTransition = { scaleInEnterTransition() },
            exitTransition = { scaleOutExitTransition() },
            popEnterTransition = { scaleInPopEnterTransition() },
            popExitTransition = { scaleOutPopExitTransition() }
        ) { backStackEntry ->
            // Is safe to cast since this composable will always be inside an activity
            val activity = LocalContext.current as Activity

            AppScreenTimeStatsScreen(
                packageName = NavHelpers
                    .getStringArgs(
                        backStackEntry,
                        AppScreenTimeStatsArgs.PackageName.name
                    ) ?: "",
                onBackClicked = { navController.popBackStackOrExitActivity(activity) }
            )

            SideEffect { barsVisibility.bottomBar.hide() }
        }
    }
}
