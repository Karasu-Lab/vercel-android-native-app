package com.karasu256.vcnative.ui.envvars

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.shell.ShellHeader
import com.karasu256.vcnative.ui.theme.BarSurface
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.TabButton
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.EnvironmentProjectsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ProjectEnvironmentVariablesScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SharedEnvironmentVariablesScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator

enum class EnvironmentShellTab(
    val destination: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
) {
    Projects(
        EnvironmentProjectsScreenDestination,
        Icons.Default.Folder,
        R.string.environment_variables_projects,
    ),
    Shared(
        SharedEnvironmentVariablesScreenDestination,
        Icons.Default.Groups,
        R.string.environment_variables_shared,
    ),
}

/**
 * Environment variables live outside of the main shell. They get their own header, which
 * replaces the drawer with a back button, and their own bottom navigation.
 */
@Destination<RootGraph>
@Composable
fun EnvironmentShellScreen(
    navigator: DestinationsNavigator,
) {
    val navController = rememberNavController()
    val tabNavigator = navController.rememberDestinationsNavigator()
    val currentDestination by navController.currentDestinationAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val spacing = NeuTheme.spacing
    val insets = WindowInsets.systemBars.union(WindowInsets.displayCutout)

    val currentTab = when (currentDestination) {
        EnvironmentProjectsScreenDestination,
        ProjectEnvironmentVariablesScreenDestination -> EnvironmentShellTab.Projects

        SharedEnvironmentVariablesScreenDestination -> EnvironmentShellTab.Shared
        else -> null
    }
    val entry = backStackEntry?.takeIf { it.destination.route == currentDestination?.route }
    val title = if (currentDestination == ProjectEnvironmentVariablesScreenDestination && entry != null) {
        ProjectEnvironmentVariablesScreenDestination.argsFrom(entry).projectName
    } else {
        stringResource(R.string.environment_variables)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ShellHeader(
            title = title,
            onNavigationClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    navigator.navigateUp()
                }
            },
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            navigationLabel = stringResource(R.string.back),
        )
        DestinationsNavHost(
            navGraph = NavGraphs.environmentShell,
            navController = navController,
            defaultTransitions = EnvironmentShellNavTransitions,
            modifier = Modifier
                .weight(1f)
                .windowInsetsPadding(insets.only(WindowInsetsSides.Horizontal)),
        )
        BarSurface(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(insets.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                    .padding(vertical = spacing.md),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EnvironmentShellTab.entries.forEach { tab ->
                    TabButton(
                        selected = tab == currentTab,
                        onClick = {
                            if (tab == currentTab) {
                                navController.popBackStack(tab.destination.route, inclusive = false)
                            } else {
                                tabNavigator.navigate(tab.destination) {
                                    popUpTo(NavGraphs.environmentShell) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.labelRes),
                        )
                    }
                }
            }
        }
    }
}
