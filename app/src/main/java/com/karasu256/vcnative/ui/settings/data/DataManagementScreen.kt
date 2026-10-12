package com.karasu256.vcnative.ui.settings.data

import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.settings.SettingsScaffold
import com.karasu256.vcnative.ui.theme.ConfirmDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ShellScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun DataManagementScreen(
    navigator: DestinationsNavigator,
    viewModel: DataManagementViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                DataManagementEvent.CacheDeleted ->
                    snackbarHostState.showSnackbar(context.getString(R.string.cache_deleted))

                DataManagementEvent.AppDataDeleted -> navigator.navigate(MainScreenDestination) {
                    popUpTo(ShellScreenDestination) { inclusive = true }
                }
            }
        }
    }

    if (uiState.isConfirmingDeleteAppData) {
        ConfirmDialog(
            title = stringResource(R.string.confirm_delete_app_data_title),
            message = stringResource(R.string.confirm_delete_app_data_message),
            confirmLabel = stringResource(R.string.confirm),
            dismissLabel = stringResource(R.string.cancel),
            onConfirm = viewModel::deleteAppData,
            onDismissRequest = viewModel::dismissDeleteAppData,
            isRunning = uiState.isRunning,
        )
    }

    SettingsScaffold(
        title = stringResource(R.string.data_management),
        onBack = { navigator.navigateUp() },
        snackbarHostState = snackbarHostState,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.delete_app_data)) },
                modifier = Modifier.clickable(onClick = viewModel::requestDeleteAppData),
                leadingContent = {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
                },
                trailingContent = uiState.appDataSizeBytes?.let { bytes ->
                    { Text(text = Formatter.formatShortFileSize(context, bytes)) }
                },
            )
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.delete_cache)) },
                modifier = Modifier.clickable(onClick = viewModel::deleteCache),
                leadingContent = {
                    Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null)
                },
                trailingContent = uiState.cacheSizeBytes?.let { bytes ->
                    { Text(text = Formatter.formatShortFileSize(context, bytes)) }
                },
            )
        }
    }
}
