package com.karasu256.vcnative.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.components.NeuButton
import com.karasu256.vcnative.ui.components.NeuText
import com.karasu256.vcnative.ui.components.NeuTextField
import com.karasu256.vcnative.ui.components.unfocus
import com.karasu256.vcnative.ui.theme.NeuTheme

@Composable
fun MainScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    var apiKey by rememberSaveable { mutableStateOf("") }
    val spacing = NeuTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .unfocus()
            .safeDrawingPadding()
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
                ),
            )
            Spacer(Modifier.height(spacing.xl))
            NeuButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = apiKey.isNotBlank(),
            ) {
                NeuText(text = stringResource(R.string.save_and_verify))
            }
        }
    }
}
