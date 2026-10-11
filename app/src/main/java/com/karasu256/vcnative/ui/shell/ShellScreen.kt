package com.karasu256.vcnative.ui.shell

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuButtonVariant
import com.karasu256.vcnative.ui.components.NeuSurface
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.DeploymentsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import kotlinx.coroutines.launch

enum class ShellTab(
    val destination: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
) {
    Deployments(DeploymentsScreenDestination, Icons.Default.RocketLaunch, R.string.deployments),
    Settings(SettingsScreenDestination, Icons.Default.Settings, R.string.settings),
}

@Destination<RootGraph>
@Composable
fun ShellScreen() {
    val navController = rememberNavController()
    val navigator = navController.rememberDestinationsNavigator()
    val currentDestination by navController.currentDestinationAsState()
    val currentTab = ShellTab.entries.firstOrNull { it.destination == currentDestination } ?: ShellTab.Deployments
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val spacing = NeuTheme.spacing
    val insets = WindowInsets.systemBars.union(WindowInsets.displayCutout)

    ShellDrawer(drawerState = drawerState) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                ShellHeader(
                    title = stringResource(currentTab.labelRes),
                    onMenuClick = { scope.launch { drawerState.open() } },
                )
                DestinationsNavHost(
                    navGraph = NavGraphs.shell,
                    navController = navController,
                    modifier = Modifier
                        .weight(1f)
                        .windowInsetsPadding(insets.only(WindowInsetsSides.Horizontal)),
                )
            }
            NeuSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RectangleShape,
            ) {
                Row(
                    modifier = Modifier
                        .windowInsetsPadding(insets.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                        .padding(vertical = spacing.md),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ShellTab.entries.forEach { tab ->
                        NeuButton(
                            onClick = {
                                navigator.navigate(tab.destination) {
                                    popUpTo(NavGraphs.shell) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            variant = if (tab == currentTab) NeuButtonVariant.Primary else NeuButtonVariant.Neutral,
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Image(
                                imageVector = tab.icon,
                                contentDescription = stringResource(tab.labelRes),
                                colorFilter = ColorFilter.tint(LocalNeuContentColor.current),
                            )
                        }
                    }
                }
            }
        }
    }
}
