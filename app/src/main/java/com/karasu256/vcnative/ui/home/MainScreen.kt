package com.karasu256.vcnative.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.unfocus
import com.karasu256.vcnative.ui.theme.InputField
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.PrimaryButton
import com.karasu256.vcnative.ui.theme.ToolbarButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ShellScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = NeuTheme.spacing
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                MainEvent.NavigateShell -> navigator.navigate(ShellScreenDestination) {
                    popUpTo(MainScreenDestination) { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .unfocus()
            .windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.displayCutout))
            .padding(horizontal = spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = NeuTheme.typography.headline,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(spacing.md))
            Text(
                text = stringResource(R.string.api_key_description),
                style = NeuTheme.typography.bodyMedium,
                color = NeuTheme.colors.onSurfaceMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(spacing.xl))
            InputField(
                value = uiState.apiKey,
                onValueChange = viewModel::onApiKeyChange,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.api_key_label),
                placeholder = stringResource(R.string.api_key_placeholder),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false,
                ),
                visualTransformation = PasswordVisualTransformation(),
                trailing = {
                    ToolbarButton(
                        onClick = {
                            scope.launch {
                                clipboard.getClipEntry()
                                    ?.clipData
                                    ?.takeIf { it.itemCount > 0 }
                                    ?.getItemAt(0)
                                    ?.coerceToText(context)
                                    ?.toString()
                                    ?.trim()
                                    ?.takeIf { it.isNotEmpty() }
                                    ?.let(viewModel::onApiKeyChange)
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = stringResource(R.string.paste),
                        )
                    }
                },
            )
            uiState.errorRes?.let { errorRes ->
                Spacer(Modifier.height(spacing.md))
                Text(
                    text = stringResource(errorRes),
                    style = NeuTheme.typography.bodyMedium,
                    color = NeuTheme.colors.danger,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(spacing.xl))
            PrimaryButton(
                onClick = viewModel::login,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.apiKey.isNotBlank(),
                loading = uiState.isLoading,
            ) {
                Text(text = stringResource(R.string.save_and_verify))
            }
        }
    }
}
