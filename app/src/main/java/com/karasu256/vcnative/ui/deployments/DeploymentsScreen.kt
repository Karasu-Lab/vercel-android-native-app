package com.karasu256.vcnative.ui.deployments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.ui.shell.RootNavigator
import com.karasu256.vcnative.ui.shell.ShellGraph
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.DeploymentDetailScreenDestination
import kotlinx.coroutines.launch

@Destination<ShellGraph>(start = true)
@Composable
fun DeploymentsScreen(
    rootNavigator: RootNavigator,
    viewModel: DeploymentsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

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
    }
}
