package com.karasu256.vcnative.ui.envvars

import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.EnvironmentVariable
import com.karasu256.vcnative.data.model.EnvironmentVariableInput
import com.karasu256.vcnative.data.repository.EnvironmentVariableRepository
import com.karasu256.vcnative.data.repository.ProjectRepository
import com.karasu256.vcnative.ui.deployments.create.ProjectItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedEnvironmentVariablesViewModel @Inject constructor(
    private val repository: EnvironmentVariableRepository,
    private val projectRepository: ProjectRepository,
) : EnvironmentVariablesViewModel() {

    override val linkableProjects: StateFlow<List<ProjectItem>?> = projectRepository.projects
        .map { list -> list?.map { ProjectItem(it.project, it.avatar?.asImageBitmap()) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        load()
        viewModelScope.launch { projectRepository.load() }
    }

    override suspend fun fetchVariables(refresh: Boolean): List<EnvironmentVariable> =
        repository.sharedVariables(refresh)

    override suspend fun createVariable(input: EnvironmentVariableInput) =
        repository.createShared(input)

    override suspend fun updateVariable(variable: EnvironmentVariable, input: EnvironmentVariableInput) =
        repository.updateShared(variable, input)

    override suspend fun deleteVariable(variable: EnvironmentVariable) =
        repository.deleteShared(variable.id)
}
