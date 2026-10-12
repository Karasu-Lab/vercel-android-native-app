package com.karasu256.vcnative.ui.envvars

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.karasu256.vcnative.ui.theme.NeuFadeTransitions
import com.ramcosta.composedestinations.annotation.Destination

@Destination<EnvironmentShellGraph>(style = NeuFadeTransitions::class)
@Composable
fun SharedEnvironmentVariablesScreen(
    viewModel: SharedEnvironmentVariablesViewModel = hiltViewModel(),
) {
    EnvironmentVariablesContent(viewModel = viewModel)
}
