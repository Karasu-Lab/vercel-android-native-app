package com.karasu256.vcnative.ui.settings.security

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.security.BiometricAuthenticator
import com.karasu256.vcnative.ui.settings.SettingsScaffold
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun SecurityScreen(
    navigator: DestinationsNavigator,
    viewModel: SecurityViewModel = hiltViewModel(),
) {
    val enabled by viewModel.biometricLockEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current as FragmentActivity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val promptTitle = stringResource(R.string.biometric_prompt_title)
    val negativeText = stringResource(R.string.cancel)
    val unavailableMessage = stringResource(R.string.error_biometric_unavailable)

    val onToggle: () -> Unit = {
        when {
            enabled -> viewModel.setBiometricLockEnabled(false)

            !BiometricAuthenticator.isAvailable(context) -> {
                scope.launch { snackbarHostState.showSnackbar(unavailableMessage) }
            }

            else -> BiometricAuthenticator.authenticate(activity, promptTitle, negativeText) { success ->
                if (success) viewModel.setBiometricLockEnabled(true)
            }
        }
    }

    SettingsScaffold(
        title = stringResource(R.string.security),
        onBack = { navigator.navigateUp() },
        snackbarHostState = snackbarHostState,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.biometric_enable)) },
                modifier = Modifier.clickable(onClick = onToggle),
                supportingContent = {
                    Text(text = stringResource(R.string.biometric_enable_description))
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null)
                },
                trailingContent = { Switch(checked = enabled, onCheckedChange = null) },
            )
        }
    }
}
