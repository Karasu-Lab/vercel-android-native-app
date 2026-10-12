package com.karasu256.vcnative.ui.logs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.RefreshBox
import com.karasu256.vcnative.ui.components.Refreshable
import com.karasu256.vcnative.ui.deployments.create.ProjectIcon
import com.karasu256.vcnative.ui.deployments.create.ProjectItem
import com.karasu256.vcnative.ui.shell.ShellGraph
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.Skeleton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.ProjectDeploymentsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<ShellGraph>
@Composable
fun ProjectListScreen(
    navigator: DestinationsNavigator,
    viewModel: ProjectListViewModel = hiltViewModel(),
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    ProjectList(
        projects = projects,
        isRefreshing = isRefreshing,
        refreshable = viewModel,
        onSelect = { item ->
            navigator.navigate(
                ProjectDeploymentsScreenDestination(
                    projectId = item.project.id,
                    projectName = item.project.name,
                )
            )
        },
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun ProjectList(
    projects: List<ProjectItem>?,
    isRefreshing: Boolean,
    refreshable: Refreshable,
    onSelect: (ProjectItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    RefreshBox(
        refreshable = refreshable,
        isRefreshing = isRefreshing,
        modifier = modifier,
    ) {
        when {
            projects == null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                repeat(6) {
                    Skeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = NeuTheme.spacing.lg, vertical = NeuTheme.spacing.sm)
                            .height(48.dp)
                    )
                }
            }

            projects.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stringResource(R.string.no_projects))
            }

            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = projects, key = { it.project.id }) { item ->
                    ListItem(
                        headlineContent = { Text(text = item.project.name) },
                        modifier = Modifier.clickable { onSelect(item) },
                        leadingContent = { ProjectIcon(project = item, size = 32.dp) },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                            )
                        },
                    )
                }
            }
        }
    }
}
