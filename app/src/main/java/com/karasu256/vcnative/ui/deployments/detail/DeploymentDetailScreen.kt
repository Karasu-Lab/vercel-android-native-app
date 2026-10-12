package com.karasu256.vcnative.ui.deployments.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.PrimaryButton
import com.karasu256.vcnative.ui.theme.SecondaryButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun DeploymentDetailScreen(
    deploymentId: String,
    navigator: DestinationsNavigator,
    viewModel: DeploymentDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val spacing = NeuTheme.spacing
    val detail = uiState.detail

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = detail?.projectName ?: stringResource(R.string.deployment_detail)) },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            DeploymentThumbnail(
                thumbnail = uiState.thumbnail,
                isLoading = uiState.isThumbnailLoading,
                contentDescription = detail?.projectName,
            )
            PrimaryButton(
                onClick = { detail?.siteUrl?.let(uriHandler::openUri) },
                modifier = Modifier.fillMaxWidth(),
                enabled = detail?.siteUrl != null,
            ) {
                Text(text = stringResource(R.string.view_site))
            }
            SecondaryButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.view_logs))
            }
            DeploymentDetailCard(detail = detail)
        }
    }
}
