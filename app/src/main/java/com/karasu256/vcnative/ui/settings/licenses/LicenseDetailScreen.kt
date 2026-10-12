package com.karasu256.vcnative.ui.settings.licenses

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.ui.settings.SettingsScaffold
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.mikepenz.markdown.m3.Markdown
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun LicenseDetailScreen(
    licenseId: String,
    navigator: DestinationsNavigator,
    viewModel: LicenseDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScaffold(
        title = uiState.license?.name.orEmpty(),
        onBack = { navigator.navigateUp() },
    ) {
        uiState.content?.let { content ->
            Markdown(
                content = content,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(NeuTheme.spacing.lg),
            )
        }
    }
}
