package com.karasu256.vcnative.ui.envvars

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.EnvironmentTarget
import com.karasu256.vcnative.data.model.EnvironmentVariable
import com.karasu256.vcnative.ui.components.RefreshBox
import com.karasu256.vcnative.ui.deployments.create.ProjectItem
import com.karasu256.vcnative.ui.theme.ActionFab
import com.karasu256.vcnative.ui.theme.ConfirmDialog
import com.karasu256.vcnative.ui.theme.FloatingSnackbarHost
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.Skeleton
import com.karasu256.vcnative.ui.theme.TabBar
import com.karasu256.vcnative.ui.theme.TabItem
import kotlinx.coroutines.flow.MutableStateFlow

private val NoLinkableProjects = MutableStateFlow<List<ProjectItem>?>(null)

private val NoLinkableSharedVariables = MutableStateFlow<List<EnvironmentVariable>?>(null)

/**
 * The environment variable list used by both the project and the shared screens. Only the
 * [viewModel] differs between them.
 */
@Composable
fun EnvironmentVariablesContent(
    viewModel: EnvironmentVariablesViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val linkableProjects by (viewModel.linkableProjects ?: NoLinkableProjects).collectAsStateWithLifecycle()
    val linkableSharedVariables by
        (viewModel.linkableSharedVariables ?: NoLinkableSharedVariables).collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTarget by rememberSaveable { mutableStateOf(EnvironmentTarget.Production) }
    val visibleVariables = remember(uiState.variables, selectedTarget) {
        uiState.variables?.filter { selectedTarget in it.targets }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is EnvironmentVariablesEvent.Message ->
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
            }
        }
    }

    uiState.sheet?.let { sheet ->
        EnvironmentVariableSheet(
            state = sheet,
            onSave = viewModel::save,
            onDismissRequest = viewModel::dismissSheet,
            isLinkSupported = viewModel.linkableProjects != null,
            projects = linkableProjects,
            isSharedLinkSupported = viewModel.linkableSharedVariables != null,
            sharedVariables = linkableSharedVariables,
            onSharedVariableSelect = viewModel::selectSharedVariable,
        )
    }

    uiState.pendingDelete?.let { variable ->
        ConfirmDialog(
            title = stringResource(
                if (variable.isShared) {
                    R.string.confirm_unlink_environment_variable_title
                } else {
                    R.string.confirm_delete_environment_variable_title
                }
            ),
            message = stringResource(
                if (variable.isShared) {
                    R.string.confirm_unlink_environment_variable_message
                } else {
                    R.string.confirm_delete_environment_variable_message
                },
                variable.key,
            ),
            confirmLabel = stringResource(R.string.confirm),
            dismissLabel = stringResource(R.string.cancel),
            onConfirm = viewModel::confirmDelete,
            onDismissRequest = viewModel::dismissDelete,
            isRunning = uiState.isDeleting,
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        TabBar(
            selectedIndex = selectedTarget.ordinal,
            tabCount = EnvironmentTarget.entries.size,
        ) {
            EnvironmentTarget.entries.forEach { target ->
                TabItem(
                    label = stringResource(target.labelRes),
                    selected = target == selectedTarget,
                    onClick = { selectedTarget = target },
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            RefreshBox(
                refreshable = viewModel,
                isRefreshing = uiState.isRefreshing,
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    visibleVariables == null -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        repeat(6) {
                            Skeleton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = NeuTheme.spacing.lg, vertical = NeuTheme.spacing.sm)
                                    .height(48.dp)
                            )
                        }
                    }

                    visibleVariables.isEmpty() -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = stringResource(R.string.no_environment_variables))
                    }

                    else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = visibleVariables, key = { it.id }) { variable ->
                            SwipeableEnvironmentVariableTile(
                                variable = variable,
                                onEdit = { viewModel.openEdit(variable) },
                                onRemove = { viewModel.requestDelete(variable) },
                            )
                        }
                    }
                }
            }
            FloatingSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = NeuTheme.spacing.lg, vertical = NeuTheme.spacing.md),
            )
            ActionFab(
                onClick = { viewModel.openCreate(selectedTarget) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = NeuTheme.spacing.lg, bottom = NeuTheme.spacing.lg),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_environment_variable),
                )
            }
        }
    }
}
