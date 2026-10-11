package com.karasu256.vcnative.ui.shell

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.animation.core.animate
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.activitys.AppThemeViewModel
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
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ShellScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
fun ShellScreen(
    navigator: DestinationsNavigator,
    viewModel: ShellViewModel = hiltViewModel(),
    appThemeViewModel: AppThemeViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appTheme by appThemeViewModel.theme.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val tabNavigator = navController.rememberDestinationsNavigator()
    val currentDestination by navController.currentDestinationAsState()
    val currentTab = ShellTab.entries.firstOrNull { it.destination == currentDestination } ?: ShellTab.Deployments
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val spacing = NeuTheme.spacing
    val insets = WindowInsets.systemBars.union(WindowInsets.displayCutout)
    val motion = NeuTheme.motion
    var barsCollapse by rememberSaveable { mutableFloatStateOf(0f) }
    var bottomBarHeight by remember { mutableIntStateOf(0) }
    var settleJob by remember { mutableStateOf<Job?>(null) }
    val barsScrollConnection = remember(motion) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y != 0f && bottomBarHeight > 0) {
                    settleJob?.cancel()
                    barsCollapse = (barsCollapse - available.y / bottomBarHeight).coerceIn(0f, 1f)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                settleJob?.cancel()
                settleJob = scope.launch {
                    animate(
                        initialValue = barsCollapse,
                        targetValue = if (barsCollapse >= 0.5f) 1f else 0f,
                        animationSpec = motion.barsSettle,
                    ) { value, _ -> barsCollapse = value }
                }
                return Velocity.Zero
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ShellEvent.LoggedOut -> navigator.navigate(MainScreenDestination) {
                    popUpTo(ShellScreenDestination) { inclusive = true }
                }
            }
        }
    }

    when (uiState.sheet) {
        ShellSheet.User -> UserSheet(
            user = uiState.user,
            avatar = uiState.avatar,
            onLogout = viewModel::logout,
            onDismissRequest = viewModel::dismissSheet,
        )
        ShellSheet.Theme -> ThemeSheet(
            current = appTheme,
            onSelect = appThemeViewModel::select,
            onDismissRequest = viewModel::dismissSheet,
        )
        null -> Unit
    }

    ShellDrawer(
        drawerState = drawerState,
        avatar = uiState.avatar,
        onUserClick = { viewModel.showSheet(ShellSheet.User) },
        onThemeClick = { viewModel.showSheet(ShellSheet.Theme) },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(barsScrollConnection),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ShellHeader(
                    title = stringResource(currentTab.labelRes),
                    onMenuClick = { scope.launch { drawerState.open() } },
                    collapseFraction = barsCollapse,
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
                    .align(Alignment.BottomCenter)
                    .onSizeChanged { bottomBarHeight = it.height }
                    .offset { IntOffset(0, (bottomBarHeight * barsCollapse).roundToInt()) },
                shape = NeuTheme.bar.shape,
                depth = NeuTheme.bar.depth,
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
                                tabNavigator.navigate(tab.destination) {
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
