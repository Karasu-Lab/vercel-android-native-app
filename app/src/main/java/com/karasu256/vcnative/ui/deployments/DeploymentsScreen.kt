package com.karasu256.vcnative.ui.deployments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.karasu256.vcnative.ui.shell.ShellGraph
import com.ramcosta.composedestinations.annotation.Destination

@Destination<ShellGraph>(start = true)
@Composable
fun DeploymentsScreen() {
    Box(Modifier.fillMaxSize())
}
