package com.karasu256.vcnative.ui.deployments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.deployments.create.CreateDeploymentEvent
import com.karasu256.vcnative.ui.deployments.create.CreateDeploymentSheet
import com.karasu256.vcnative.ui.deployments.create.CreateDeploymentViewModel
import com.karasu256.vcnative.ui.deployments.create.GitHubUnlinkSheet
import com.karasu256.vcnative.ui.deployments.create.ProjectSelectDialog
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.shell.LocalShellBars
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.shell.RootNavigator
import com.karasu256.vcnative.ui.shell.ShellGraph
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.DeploymentDetailScreenDestination
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Destination<ShellGraph>(start = true)
@Composable
fun DeploymentsScreen(
    rootNavigator: RootNavigator,
    viewModel: DeploymentsViewModel = hiltViewModel(),
    createViewModel: CreateDeploymentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val createState by createViewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val bars = LocalShellBars.current
    val fabMargin = with(LocalDensity.current) { NeuTheme.spacing.lg.roundToPx() }
    var fabHeight by remember { mutableIntStateOf(0) }
    val createSheetState = rememberModalBottomSheetState()

    LaunchedEffect(createViewModel) {
        createViewModel.events.collect { event ->
            when (event) {
                is CreateDeploymentEvent.OpenUri -> uriHandler.openUri(event.uri.toString())
                CreateDeploymentEvent.Deployed -> scope.launch { createSheetState.hide() }
                    .invokeOnCompletion { createViewModel.dismissSheet() }
            }
        }
    }

    if (createState.isSheetOpen) {
        CreateDeploymentSheet(
            state = createState,
            onProjectClick = createViewModel::openProjectDialog,
            onBranchToggle = createViewModel::toggleBranch,
            onGitHubClick = {
                if (createState.isGitHubLinked) createViewModel.openGitHubSheet() else createViewModel.requestGitHubLink()
            },
            onDeploy = createViewModel::deploy,
            onDismissRequest = createViewModel::dismissSheet,
            sheetState = createSheetState,
        )
    }
    if (createState.isProjectDialogOpen) {
        ProjectSelectDialog(
            projects = createState.projects,
            onSelect = createViewModel::selectProject,
            onDismissRequest = createViewModel::dismissProjectDialog,
        )
    }
    if (createState.isGitHubSheetOpen) {
        GitHubUnlinkSheet(
            onUnlink = createViewModel::unlinkGitHub,
            onDismissRequest = createViewModel::dismissGitHubSheet,
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DeploymentsList(
            deployments = uiState.deployments,
            isLoadingMore = uiState.isLoadingMore,
            isRefreshing = uiState.isRefreshing,
            onLoadMore = viewModel::loadMore,
            onRefresh = viewModel::refresh,
            onDeploymentClick = { deployment ->
                rootNavigator.navigator.navigate(DeploymentDetailScreenDestination(deploymentId = deployment.id))
            },
            modifier = Modifier.fillMaxSize(),
            listState = listState,
        )
        DeploymentUpdateNotice(
            visible = uiState.newDeployments.isNotEmpty(),
            branch = uiState.newDeployments.firstOrNull()?.branch,
            onClick = {
                viewModel.showNewDeployments()
                scope.launch { listState.animateScrollToItem(0) }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = NeuTheme.spacing.md),
        )
        NeuButton(
            onClick = createViewModel::openSheet,
            contentPadding = NeuTheme.fab.contentPadding,
            shape = NeuTheme.fab.shape,
            depth = NeuTheme.fab.depth,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = NeuTheme.spacing.lg, bottom = NeuTheme.spacing.lg)
                .size(NeuTheme.fab.size)
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
                tint = LocalNeuContentColor.current,
            )
        }
    }
}
