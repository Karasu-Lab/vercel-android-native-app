package com.karasu256.vcnative.ui.envvars

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.EnvironmentTarget
import com.karasu256.vcnative.data.model.EnvironmentVariable
import com.karasu256.vcnative.data.model.EnvironmentVariableInput
import com.karasu256.vcnative.ui.components.unfocus
import com.karasu256.vcnative.ui.deployments.create.ProjectItem
import com.karasu256.vcnative.ui.theme.ChipRow
import com.karasu256.vcnative.ui.theme.DropdownButton
import com.karasu256.vcnative.ui.theme.InputField
import com.karasu256.vcnative.ui.theme.NeuTheme
import com.karasu256.vcnative.ui.theme.PrimaryButton
import com.karasu256.vcnative.ui.theme.ToggleChip
import com.karasu256.vcnative.ui.theme.ToolbarButton

private const val SecretPlaceholder = "********"

/**
 * Bottom sheet shared by the "add" and "edit" flows. The initial values are filled from [state]
 * and the value is filled in once it has been fetched. The save button is pinned below the
 * scrolling fields so it is visible as soon as the sheet opens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentVariableSheet(
    state: EnvironmentVariableSheetState,
    onSave: (EnvironmentVariableInput) -> Unit,
    onDismissRequest: () -> Unit,
    isLinkSupported: Boolean = false,
    projects: List<ProjectItem>? = null,
    isSharedLinkSupported: Boolean = false,
    sharedVariables: List<EnvironmentVariable>? = null,
    onSharedVariableSelect: (EnvironmentVariable?) -> Unit = {},
) {
    val spacing = NeuTheme.spacing
    val variable = state.variable
    var key by rememberSaveable(variable?.id) { mutableStateOf(variable?.key.orEmpty()) }
    var value by rememberSaveable(variable?.id, state.initialValue) { mutableStateOf(state.initialValue) }
    var isSecret by rememberSaveable(variable?.id) { mutableStateOf(variable?.isSecret ?: false) }
    var targets by remember(variable?.id) {
        mutableStateOf(variable?.targets?.takeIf { it.isNotEmpty() } ?: setOf(state.initialTarget))
    }
    var linkedProjectIds by remember(variable?.id) {
        mutableStateOf(variable?.linkedProjectIds.orEmpty())
    }
    var isLinkDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isSharedDialogOpen by rememberSaveable { mutableStateOf(false) }
    val shared = state.sharedVariable
    val isSharedSelected = shared != null
    val shownKey = shared?.key ?: key
    val shownSecret = shared?.isSecret ?: isSecret
    val shownValue = when {
        shared == null -> value
        shared.isSecret -> SecretPlaceholder
        else -> shared.value.orEmpty()
    }
    val shownTargets = shared?.targets ?: targets
    val canSave = isSharedSelected ||
        (key.isNotBlank() && targets.isNotEmpty() && (state.isEditing || value.isNotEmpty()))

    if (isSharedDialogOpen) {
        SharedVariableSelectDialog(
            variables = sharedVariables,
            onSelect = { selected ->
                onSharedVariableSelect(selected)
                isSharedDialogOpen = false
            },
            onDismissRequest = { isSharedDialogOpen = false },
        )
    }

    if (isLinkDialogOpen) {
        ProjectMultiSelectDialog(
            projects = projects,
            selectedIds = linkedProjectIds,
            onConfirm = { selected ->
                linkedProjectIds = selected
                isLinkDialogOpen = false
            },
            onDismissRequest = { isLinkDialogOpen = false },
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .unfocus()
                .padding(start = spacing.lg, end = spacing.lg, bottom = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                Text(
                    text = stringResource(
                        if (state.isEditing) R.string.edit_environment_variable else R.string.add_environment_variable
                    ),
                    style = NeuTheme.typography.title,
                )

                InputField(
                    value = shownKey,
                    onValueChange = { key = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.label_key),
                    placeholder = "EXAMPLE_KEY",
                    enabled = !state.isSaving && !isSharedSelected,
                )

                InputField(
                    value = shownValue,
                    onValueChange = { value = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.label_value),
                    placeholder = if (variable?.isSecret == true) {
                        stringResource(R.string.secret_value_unchanged)
                    } else {
                        null
                    },
                    enabled = !state.isSaving && !isSharedSelected,
                    singleLine = false,
                    keyboardOptions = if (shownSecret) {
                        KeyboardOptions(keyboardType = KeyboardType.Password)
                    } else {
                        KeyboardOptions.Default
                    },
                    visualTransformation = if (shownSecret) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                )

                if (isSharedLinkSupported && !state.isEditing) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        Text(
                            text = stringResource(R.string.label_shared_variable),
                            style = NeuTheme.typography.bodySmall,
                            color = NeuTheme.colors.onSurfaceMuted,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            DropdownButton(
                                label = shared?.key ?: stringResource(R.string.shared_variable_none),
                                onClick = { isSharedDialogOpen = true },
                                modifier = Modifier.weight(1f),
                                enabled = !state.isSaving,
                            )
                            if (isSharedSelected) {
                                ToolbarButton(onClick = { onSharedVariableSelect(null) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.clear_shared_variable),
                                    )
                                }
                            }
                        }
                    }
                }

                if (isLinkSupported) {
                    val linkedNames = projects.orEmpty()
                        .filter { it.project.id in linkedProjectIds }
                        .map { it.project.name }
                    val linkedLabel = when {
                        linkedNames.isEmpty() -> stringResource(R.string.linked_projects_none)
                        linkedNames.size == 1 -> linkedNames.first()
                        else -> stringResource(
                            R.string.linked_projects_more,
                            linkedNames.first(),
                            linkedNames.size - 1,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        Text(
                            text = stringResource(R.string.label_linked_projects),
                            style = NeuTheme.typography.bodySmall,
                            color = NeuTheme.colors.onSurfaceMuted,
                        )
                        DropdownButton(
                            label = linkedLabel,
                            onClick = { isLinkDialogOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isSaving,
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.label_secret))
                        Text(
                            text = stringResource(R.string.secret_description),
                            style = NeuTheme.typography.bodySmall,
                            color = NeuTheme.colors.onSurfaceMuted,
                        )
                    }
                    Switch(
                        checked = shownSecret,
                        onCheckedChange = { isSecret = it },
                        // Vercel does not allow a secret to be turned back into a readable value.
                        enabled = !state.isSaving && !isSharedSelected && variable?.isSecret != true,
                    )
                }

                ChipRow(modifier = Modifier.fillMaxWidth()) {
                    items(items = EnvironmentTarget.entries, key = { it.apiValue }) { target ->
                        ToggleChip(
                            label = stringResource(target.labelRes),
                            selected = target in shownTargets,
                            onToggle = {
                                if (!isSharedSelected) {
                                    targets = if (target in targets) targets - target else targets + target
                                }
                            },
                        )
                    }
                }
            }

            PrimaryButton(
                onClick = {
                    onSave(
                        EnvironmentVariableInput(
                            key = shownKey.trim(),
                            value = if (shared == null) value else shared.value.orEmpty(),
                            isSecret = shownSecret,
                            targets = shownTargets,
                            linkedProjectIds = linkedProjectIds,
                            sharedVariable = shared,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave,
                loading = state.isSaving,
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}

@get:StringRes
val EnvironmentTarget.labelRes: Int
    get() = when (this) {
        EnvironmentTarget.Production -> R.string.environment_production
        EnvironmentTarget.Preview -> R.string.environment_preview
        EnvironmentTarget.Development -> R.string.environment_development
    }
