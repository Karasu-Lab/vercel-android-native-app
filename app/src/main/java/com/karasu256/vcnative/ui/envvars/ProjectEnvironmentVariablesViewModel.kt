package com.karasu256.vcnative.ui.envvars

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.EnvironmentVariable
import com.karasu256.vcnative.data.model.EnvironmentVariableInput
import com.karasu256.vcnative.data.repository.EnvironmentVariableRepository
import com.ramcosta.composedestinations.generated.destinations.ProjectEnvironmentVariablesScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectEnvironmentVariablesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: EnvironmentVariableRepository,
) : EnvironmentVariablesViewModel() {

    private val projectId: String =
        ProjectEnvironmentVariablesScreenDestination.argsFrom(savedStateHandle).projectId

    private val _sharedVariables = MutableStateFlow<List<EnvironmentVariable>?>(null)
    override val linkableSharedVariables: StateFlow<List<EnvironmentVariable>?> =
        _sharedVariables.asStateFlow()

    init {
        load()
        viewModelScope.launch { _sharedVariables.value = repository.sharedVariables() }
    }

    override suspend fun fetchVariables(refresh: Boolean): List<EnvironmentVariable> =
        repository.projectVariables(projectId, refresh)

    override suspend fun createVariable(input: EnvironmentVariableInput) {
        val shared = input.sharedVariable
        if (shared != null) {
            repository.linkSharedToProject(shared, projectId)
        } else {
            repository.createProject(projectId, input)
        }
    }

    override suspend fun updateVariable(variable: EnvironmentVariable, input: EnvironmentVariableInput) =
        repository.updateProject(projectId, variable.id, input)

    override suspend fun deleteVariable(variable: EnvironmentVariable) {
        if (variable.isShared) {
            repository.unlinkSharedFromProject(variable.id, projectId)
        } else {
            repository.deleteProject(projectId, variable.id)
        }
    }
}
