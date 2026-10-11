package com.karasu256.vcnative.ui.deployments.create

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.karasu256.vcnative.data.model.Project
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeploymentHost(
    initialProject: Project?,
    onClosed: () -> Unit,
) {
    val parent = checkNotNull(LocalViewModelStoreOwner.current)
    val owner = remember(parent) { SheetViewModelStoreOwner(parent) }
    DisposableEffect(owner) {
        onDispose { owner.viewModelStore.clear() }
    }

    val viewModel = hiltViewModel<CreateDeploymentViewModel, CreateDeploymentViewModel.Factory>(
        viewModelStoreOwner = owner,
    ) { factory -> factory.create(initialProject) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateDeploymentEvent.OpenUri -> uriHandler.openUri(event.uri.toString())
                CreateDeploymentEvent.Deployed -> scope.launch { sheetState.hide() }
                    .invokeOnCompletion { onClosed() }
            }
        }
    }

    CreateDeploymentSheet(
        state = state,
        onProjectClick = viewModel::openProjectDialog,
        onBranchToggle = viewModel::toggleBranch,
        onGitHubClick = {
            if (state.isGitHubLinked) viewModel.openGitHubSheet() else viewModel.requestGitHubLink()
        },
        onDeploy = viewModel::deploy,
        onDismissRequest = onClosed,
        sheetState = sheetState,
    )
    if (state.isProjectDialogOpen) {
        ProjectSelectDialog(
            projects = state.projects,
            onSelect = viewModel::selectProject,
            onDismissRequest = viewModel::dismissProjectDialog,
        )
    }
    if (state.isGitHubSheetOpen) {
        GitHubUnlinkSheet(
            onUnlink = viewModel::unlinkGitHub,
            onDismissRequest = viewModel::dismissGitHubSheet,
        )
    }
}

private class SheetViewModelStoreOwner(
    parent: ViewModelStoreOwner,
) : ViewModelStoreOwner, HasDefaultViewModelProviderFactory {
    private val parentFactory = parent as HasDefaultViewModelProviderFactory

    override val viewModelStore = ViewModelStore()

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = parentFactory.defaultViewModelProviderFactory

    override val defaultViewModelCreationExtras: CreationExtras
        get() = parentFactory.defaultViewModelCreationExtras
}
