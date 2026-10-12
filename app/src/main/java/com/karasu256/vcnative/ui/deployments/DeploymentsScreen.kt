package com.karasu256.vcnative.ui.deployments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.Project
import com.karasu256.vcnative.ui.deployments.create.CreateDeploymentHost
import com.karasu256.vcnative.ui.shell.LocalShellBars
import com.karasu256.vcnative.ui.shell.RootNavigator
import com.karasu256.vcnative.ui.shell.ShellGraph
import com.karasu256.vcnative.ui.theme.ActionFab
import com.karasu256.vcnative.ui.theme.FloatingSnackbarHost
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.TabBar
import com.karasu256.vcnative.ui.theme.TabItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.DeploymentDetailScreenDestination
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private class CreateDeploymentSession(val initialProject: Project?)

@Destination<ShellGraph>(start = true)
@Composable
fun DeploymentsScreen(
    rootNavigator: RootNavigator,
    viewModel: DeploymentsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val bars = LocalShellBars.current
    val fabMargin = with(LocalDensity.current) { NeuTheme.spacing.lg.roundToPx() }
    var fabHeight by remember { mutableIntStateOf(0) }
    var createSession by remember { mutableStateOf<CreateDeploymentSession?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableStateOf(DeploymentTab.All) }
    val visibleDeployments = remember(uiState.deployments, selectedTab) {
        uiState.deployments.filter(selectedTab::accepts)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DeploymentsEvent.Message -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                }
            }
        }
    }

    uiState.pendingAction?.let { action ->
        DeploymentActionDialog(
            action = action,
            isRunning = uiState.isActionRunning,
            onConfirm = viewModel::confirmAction,
            onDismissRequest = viewModel::dismissAction,
        )
    }

    createSession?.let { session ->
        key(session) {
            CreateDeploymentHost(
                initialProject = session.initialProject,
                onClosed = { createSession = null },
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabBar(
            selectedIndex = selectedTab.ordinal,
            tabCount = DeploymentTab.entries.size,
        ) {
            DeploymentTab.entries.forEach { tab ->
                TabItem(
                    label = stringResource(tab.labelRes),
                    selected = tab == selectedTab,
                    onClick = { selectedTab = tab },
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            DeploymentsList(
                deployments = visibleDeployments,
                isLoadingMore = uiState.isLoadingMore,
                isRefreshing = uiState.isRefreshing,
                onLoadMore = viewModel::loadMore,
                refreshable = viewModel,
                onDeploymentClick = { deployment ->
                    rootNavigator.navigator.navigate(DeploymentDetailScreenDestination(deploymentId = deployment.id))
                },
                onDeploymentAction = viewModel::requestAction,
                modifier = Modifier.fillMaxSize(),
                listState = listState,
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = NeuTheme.spacing.lg, vertical = NeuTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(NeuTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DeploymentUpdateNotice(
                    visible = uiState.newDeployments.isNotEmpty(),
                    branch = uiState.newDeployments.firstOrNull()?.branch,
                    onClick = {
                        viewModel.showNewDeployments()
                        scope.launch { listState.animateScrollToItem(0) }
                    },
                )
                FloatingSnackbarHost(hostState = snackbarHostState)
            }
            ActionFab(
                onClick = { createSession = CreateDeploymentSession(uiState.filterProject) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = NeuTheme.spacing.lg, bottom = NeuTheme.spacing.lg)
                    .onSizeChanged { fabHeight = it.height }
                    .offset {
                        val shown = -bars.bottomBarHeight * (1f - bars.collapseFraction)
                        val hidden = (fabHeight + fabMargin) * bars.collapseFraction
                        IntOffset(0, (shown + hidden).roundToInt())
                    },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_deployment),
                )
            }
        }
    }
}
