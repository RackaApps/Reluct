package work.racka.reluct.android.navigation.navhost.graphs.screentime

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import work.racka.reluct.android.navigation.R
import work.racka.reluct.android.navigation.toptabs.screentime.ScreenTimeTabBar
import work.racka.reluct.android.navigation.toptabs.screentime.ScreenTimeTabDestination
import work.racka.reluct.android.navigation.transitions.scaleInEnterTransition
import work.racka.reluct.android.navigation.transitions.scaleInPopEnterTransition
import work.racka.reluct.android.navigation.transitions.scaleOutExitTransition
import work.racka.reluct.android.navigation.transitions.scaleOutPopExitTransition
import work.racka.reluct.android.navigation.util.NavHelpers.navigateNavBarElements
import work.racka.reluct.android.screens.screentime.limits.ScreenTimeLimitsScreen
import work.racka.reluct.android.screens.screentime.statistics.ScreenTimeStatisticsScreen
import work.racka.reluct.common.core.navigation.composeDestinations.screentime.AppScreenTimeStatsDestination
import work.racka.reluct.common.core.navigation.composeDestinations.screentime.ScreenTimeLimitsDestination
import work.racka.reluct.common.core.navigation.composeDestinations.screentime.ScreenTimeStatsDestination
import work.racka.reluct.compose.common.components.topBar.ReluctPageHeading
import work.racka.reluct.compose.common.components.util.BarsVisibility

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
internal fun ScreenTimeNavHost(
    mainNavController: NavHostController,
    barsVisibility: BarsVisibility,
    mainScaffoldPadding: PaddingValues,
    navController: NavHostController = rememberAnimatedNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val tabPage = remember(currentDestination) {
        derivedStateOf {
            getCurrentTab(currentDestination)
        }
    }

    Scaffold(
        topBar = {
            ScreenTimeTopBar(
                tabPage = tabPage,
                updateTabPage = {
                    navController.navigateNavBarElements(it.route)
                },
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        AnimatedNavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            route = ScreenTimeStatsDestination.destination,
            startDestination = ScreenTimeStatsDestination.route
        ) {
            // Statistics
            composable(
                route = ScreenTimeStatsDestination.route,
                // Transition animations
                enterTransition = { scaleInEnterTransition() },
                exitTransition = { scaleOutExitTransition() },
                popEnterTransition = { scaleInPopEnterTransition() },
                popExitTransition = { scaleOutPopExitTransition() }
            ) {
                ScreenTimeStatisticsScreen(
                    mainScaffoldPadding = mainScaffoldPadding,
                    barsVisibility = barsVisibility,
                    onNavigateToAppUsageInfo = { packageName ->
                        mainNavController.navigate(
                            AppScreenTimeStatsDestination.argsRoute(
                                packageName
                            )
                        )
                    }
                )
            }

            // Limits
            composable(
                route = ScreenTimeLimitsDestination.route,
                // Transition animations
                enterTransition = { scaleInEnterTransition() },
                exitTransition = { scaleOutExitTransition() },
                popEnterTransition = { scaleInPopEnterTransition() },
                popExitTransition = { scaleOutPopExitTransition() }
            ) {
                ScreenTimeLimitsScreen(
                    mainScaffoldPadding = mainScaffoldPadding,
                    barsVisibility = barsVisibility,
                    onNavigateToAppUsageInfo = { packageName ->
                        mainNavController.navigate(
                            AppScreenTimeStatsDestination.argsRoute(
                                packageName
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ScreenTimeTopBar(
    tabPage: State<ScreenTimeTabDestination>,
    updateTabPage: (ScreenTimeTabDestination) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .statusBarsPadding()
            .fillMaxWidth(),
        verticalArrangement = Arrangement
            .spacedBy(16.dp)
    ) {
        ReluctPageHeading(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = stringResource(id = R.string.screen_time_destination_text),
            extraItems = {}
        )

        LazyRow {
            item {
                ScreenTimeTabBar(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    tabPage = tabPage,
                    onTabSelected = {
                        updateTabPage(it)
                    }
                )
            }
        }
    }
}

private fun getCurrentTab(currentDestination: NavDestination?): ScreenTimeTabDestination {
    val destinations = ScreenTimeTabDestination.values()
    destinations.forEach { item ->
        val isSelected = currentDestination?.hierarchy?.any {
            it.route == item.route
        } ?: false
        if (isSelected) return item
    }
    return destinations.first()
}
