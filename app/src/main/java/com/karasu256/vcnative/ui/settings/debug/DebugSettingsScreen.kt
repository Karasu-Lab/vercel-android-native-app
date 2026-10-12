package com.karasu256.vcnative.ui.settings.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.settings.SettingsScaffold
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun DebugSettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: DebugSettingsViewModel = hiltViewModel(),
) {
    val enabled by viewModel.networkLoggingEnabled.collectAsStateWithLifecycle()

    SettingsScaffold(
        title = stringResource(R.string.debug_settings),
        onBack = { navigator.navigateUp() },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.network_logging_enable)) },
                modifier = Modifier.clickable { viewModel.setNetworkLoggingEnabled(!enabled) },
                supportingContent = {
                    Text(text = stringResource(R.string.network_logging_description))
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null)
                },
                trailingContent = { Switch(checked = enabled, onCheckedChange = null) },
            )
        }
    }
}
