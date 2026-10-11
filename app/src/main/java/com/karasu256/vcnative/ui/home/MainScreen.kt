package com.karasu256.vcnative.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuButtonVariant
import com.karasu256.vcnative.ui.components.NeuText
import com.karasu256.vcnative.ui.components.NeuTextField
import com.karasu256.vcnative.ui.components.unfocus
import com.karasu256.vcnative.ui.theme.LocalNeuContentColor
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.MainScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination<RootGraph>(start = true)
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    var apiKey by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = NeuTheme.spacing
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                MainEvent.NavigateHome -> navigator.navigate(HomeScreenDestination) {
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
            NeuText(
                text = stringResource(R.string.app_name),
                style = NeuTheme.typography.headline,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(spacing.md))
            NeuText(
                text = stringResource(R.string.api_key_description),
                style = NeuTheme.typography.bodyMedium,
                color = NeuTheme.colors.onSurfaceMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(spacing.xl))
            NeuTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
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
                    NeuButton(
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
                                    ?.let { apiKey = it }
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        variant = NeuButtonVariant.Neutral,
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Image(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = stringResource(R.string.paste),
                            colorFilter = ColorFilter.tint(LocalNeuContentColor.current),
                        )
                    }
                },
            )
            uiState.errorRes?.let { errorRes ->
                Spacer(Modifier.height(spacing.md))
                NeuText(
                    text = stringResource(errorRes),
                    style = NeuTheme.typography.bodyMedium,
                    color = NeuTheme.colors.danger,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(spacing.xl))
            NeuButton(
                onClick = { viewModel.login(apiKey) },
                modifier = Modifier.fillMaxWidth(),
                enabled = apiKey.isNotBlank(),
                loading = uiState.isLoading,
            ) {
                NeuText(text = stringResource(R.string.save_and_verify))
            }
        }
    }
}
