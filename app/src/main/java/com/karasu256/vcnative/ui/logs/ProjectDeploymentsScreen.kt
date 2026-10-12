package com.karasu256.vcnative.ui.logs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.DeploymentState
import com.karasu256.vcnative.ui.components.RefreshBox
import com.karasu256.vcnative.ui.deployments.DeploymentInfo
import com.karasu256.vcnative.ui.deployments.DeploymentInfoListTile
import com.karasu256.vcnative.ui.deployments.DeploymentsViewModel
import com.karasu256.vcnative.ui.shell.ShellGraph
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.SectionHeader
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.DeploymentLogsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<ShellGraph>
@Composable
fun ProjectDeploymentsScreen(
    projectId: String,
    projectName: String,
    navigator: DestinationsNavigator,
    viewModel: DeploymentsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deployments = uiState.deployments
    val listState = rememberLazyListState()
    val currentOnLoadMore by rememberUpdatedState(viewModel::loadMore)
    val reachedEnd by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(reachedEnd, deployments.size) {
        if (reachedEnd) currentOnLoadMore()
    }

    val openLogs = { deployment: DeploymentInfo ->
        navigator.navigate(
            DeploymentLogsScreenDestination(
                projectId = projectId,
                deploymentId = deployment.id,
                title = projectName,
            )
        )
    }

    RefreshBox(
        refreshable = viewModel,
        isRefreshing = uiState.isRefreshing && deployments.isNotEmpty(),
        modifier = Modifier.fillMaxSize(),
    ) {
        when {
            deployments.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.isRefreshing) {
                    CircularProgressIndicator()
                } else {
                    Text(text = stringResource(R.string.no_deployments))
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
            ) {
                val production = deployments.firstOrNull {
                    it.target == "production" && it.state == DeploymentState.Ready
                }
                val latest = deployments.first()
                val past = deployments.filter { it.id != production?.id && it.id != latest.id }

                if (production != null) {
                    item(key = "production-header") {
                        SectionHeader(title = stringResource(R.string.production_deployment))
                    }
                    item(key = "production-${production.id}") {
                        DeploymentInfoListTile(
                            deployment = production,
                            onClick = { openLogs(production) },
                        )
                    }
                }
                item(key = "latest-header") {
                    SectionHeader(title = stringResource(R.string.latest_deployment))
                }
                item(key = "latest-${latest.id}") {
                    DeploymentInfoListTile(
                        deployment = latest,
                        onClick = { openLogs(latest) },
                    )
                }
                if (past.isNotEmpty()) {
                    item(key = "past-header") {
                        SectionHeader(title = stringResource(R.string.past_deployments))
                    }
                    items(items = past, key = { "past-${it.id}" }) { deployment ->
                        DeploymentInfoListTile(
                            deployment = deployment,
                            onClick = { openLogs(deployment) },
                        )
                    }
                }
                if (uiState.isLoadingMore) {
                    item(key = "loading-more") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(NeuTheme.spacing.lg),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
