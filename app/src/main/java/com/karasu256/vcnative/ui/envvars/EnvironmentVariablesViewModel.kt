package com.karasu256.vcnative.ui.envvars

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.R
import com.karasu256.vcnative.data.model.EnvironmentTarget
import com.karasu256.vcnative.data.model.EnvironmentVariable
import com.karasu256.vcnative.data.model.EnvironmentVariableInput
import com.karasu256.vcnative.ui.components.Refreshable
import com.karasu256.vcnative.ui.deployments.create.ProjectItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EnvironmentVariableSheetState(
    val variable: EnvironmentVariable?,
    val initialTarget: EnvironmentTarget,
    val initialValue: String = "",
    val isSaving: Boolean = false,
    val sharedVariable: EnvironmentVariable? = null,
) {
    val isEditing: Boolean
        get() = variable != null
}

data class EnvironmentVariablesUiState(
    val variables: List<EnvironmentVariable>? = null,
    val isRefreshing: Boolean = false,
    val sheet: EnvironmentVariableSheetState? = null,
    val pendingDelete: EnvironmentVariable? = null,
    val isDeleting: Boolean = false,
)

sealed interface EnvironmentVariablesEvent {
    data class Message(@StringRes val messageRes: Int) : EnvironmentVariablesEvent
}

abstract class EnvironmentVariablesViewModel : ViewModel(), Refreshable {

    private val _uiState = MutableStateFlow(EnvironmentVariablesUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EnvironmentVariablesEvent>()
    val events = _events.asSharedFlow()

    open val linkableProjects: StateFlow<List<ProjectItem>?>? = null

    open val linkableSharedVariables: StateFlow<List<EnvironmentVariable>?>? = null

    protected abstract suspend fun fetchVariables(refresh: Boolean): List<EnvironmentVariable>

    protected abstract suspend fun createVariable(input: EnvironmentVariableInput)

    protected abstract suspend fun updateVariable(
        variable: EnvironmentVariable,
        input: EnvironmentVariableInput,
    )

    protected abstract suspend fun deleteVariable(variable: EnvironmentVariable)

    protected fun load() {
        viewModelScope.launch { reload(refresh = false) }
    }

    override fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            try {
                reload(refresh = true)
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private suspend fun reload(refresh: Boolean) {
        val variables = fetchVariables(refresh)
        _uiState.update { it.copy(variables = variables) }
    }

    fun openCreate(target: EnvironmentTarget) {
        _uiState.update {
            it.copy(sheet = EnvironmentVariableSheetState(variable = null, initialTarget = target))
        }
    }

    fun openEdit(variable: EnvironmentVariable) {
        _uiState.update {
            it.copy(
                sheet = EnvironmentVariableSheetState(
                    variable = variable,
                    initialTarget = variable.targets.firstOrNull() ?: EnvironmentTarget.Production,
                    initialValue = variable.value.orEmpty(),
                ),
            )
        }
    }

    fun selectSharedVariable(variable: EnvironmentVariable?) {
        _uiState.update { state ->
            state.copy(sheet = state.sheet?.copy(sharedVariable = variable))
        }
    }

    fun dismissSheet() {
        if (_uiState.value.sheet?.isSaving == true) return
        _uiState.update { it.copy(sheet = null) }
    }

    fun save(input: EnvironmentVariableInput) {
        val sheet = _uiState.value.sheet ?: return
        if (sheet.isSaving) return
        _uiState.update { it.copy(sheet = sheet.copy(isSaving = true)) }
        viewModelScope.launch {
            val variable = sheet.variable
            if (variable == null) {
                createVariable(input)
            } else {
                updateVariable(variable, input)
            }
            _uiState.update { it.copy(sheet = null) }
            _events.emit(
                EnvironmentVariablesEvent.Message(
                    if (variable == null) R.string.environment_variable_created else R.string.environment_variable_updated
                )
            )
            reload(refresh = false)
        }
    }

    fun requestDelete(variable: EnvironmentVariable) {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(pendingDelete = variable) }
    }

    fun dismissDelete() {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(pendingDelete = null) }
    }

    fun confirmDelete() {
        val state = _uiState.value
        val variable = state.pendingDelete ?: return
        if (state.isDeleting) return
        _uiState.update { it.copy(isDeleting = true) }
        viewModelScope.launch {
            deleteVariable(variable)
            _uiState.update { current ->
                current.copy(
                    pendingDelete = null,
                    isDeleting = false,
                    variables = current.variables?.filter { it.id != variable.id },
                )
            }
            _events.emit(
                EnvironmentVariablesEvent.Message(
                    if (variable.isShared) R.string.environment_variable_unlinked else R.string.environment_variable_deleted
                )
            )
        }
    }
}
