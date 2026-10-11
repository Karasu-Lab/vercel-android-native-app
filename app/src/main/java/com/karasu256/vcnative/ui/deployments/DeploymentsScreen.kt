package com.karasu256.vcnative.ui.deployments

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.ui.shell.ShellGraph
import com.ramcosta.composedestinations.annotation.Destination

@Destination<ShellGraph>(start = true)
@Composable
fun DeploymentsScreen(
    viewModel: DeploymentsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DeploymentsList(
        deployments = uiState.deployments,
        isLoadingMore = uiState.isLoadingMore,
        isRefreshing = uiState.isRefreshing,
        onLoadMore = viewModel::loadMore,
        onRefresh = viewModel::refresh,
        modifier = Modifier.fillMaxSize(),
    )
}
