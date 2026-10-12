package com.karasu256.vcnative.ui.envvars

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination

@Destination<EnvironmentShellGraph>
@Composable
fun ProjectEnvironmentVariablesScreen(
    projectId: String,
    projectName: String,
    viewModel: ProjectEnvironmentVariablesViewModel = hiltViewModel(),
) {
    EnvironmentVariablesContent(viewModel = viewModel)
}
