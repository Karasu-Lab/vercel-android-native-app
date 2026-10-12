package com.karasu256.vcnative.ui.envvars

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.ui.logs.ProjectList
import com.karasu256.vcnative.ui.logs.ProjectListViewModel
import com.karasu256.vcnative.ui.theme.NeuFadeTransitions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.ProjectEnvironmentVariablesScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<EnvironmentShellGraph>(start = true, style = NeuFadeTransitions::class)
@Composable
fun EnvironmentProjectsScreen(
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
                ProjectEnvironmentVariablesScreenDestination(
                    projectId = item.project.id,
                    projectName = item.project.name,
                )
            )
        },
        modifier = Modifier.fillMaxSize(),
    )
}
