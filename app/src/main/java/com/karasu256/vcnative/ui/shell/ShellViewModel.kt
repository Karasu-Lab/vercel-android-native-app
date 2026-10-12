package com.karasu256.vcnative.ui.shell

import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.repository.ApiKeyRepository
import com.karasu256.vcnative.data.repository.ProjectFilterRepository
import com.karasu256.vcnative.data.repository.ProjectRepository
import com.karasu256.vcnative.data.repository.UserProfileRepository
import com.karasu256.vcnative.data.repository.UserRepository
import com.karasu256.vcnative.ui.deployments.create.ProjectItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShellViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val projectRepository: ProjectRepository,
    private val projectFilterRepository: ProjectFilterRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShellUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<ShellEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch { userProfileRepository.load() }
        viewModelScope.launch {
            userProfileRepository.profile.collect { profile ->
                _uiState.update { it.copy(user = profile.user, avatar = profile.avatar) }
            }
        }
        viewModelScope.launch {
            combine(
                projectRepository.projects,
                projectFilterRepository.selected
            ) { projects, selected ->
                val items = projects?.map { ProjectItem(it.project, it.avatar?.asImageBitmap()) }
                items to selected?.let { project ->
                    items?.firstOrNull { it.project.id == project.id } ?: ProjectItem(project)
                }
            }.collect { (items, filter) ->
                _uiState.update { it.copy(projects = items, filterProject = filter) }
            }
        }
    }

    fun openProjectDialog() {
        _uiState.update { it.copy(isProjectDialogOpen = true) }
        viewModelScope.launch { projectRepository.load() }
    }

    fun dismissProjectDialog() {
        _uiState.update { it.copy(isProjectDialogOpen = false) }
    }

    fun selectFilterProject(project: ProjectItem) {
        projectFilterRepository.select(project.project)
        _uiState.update { it.copy(isProjectDialogOpen = false) }
    }

    fun clearFilterProject() {
        projectFilterRepository.select(null)
    }

    fun showSheet(sheet: ShellSheet) {
        _uiState.update { it.copy(sheet = sheet) }
    }

    fun dismissSheet() {
        _uiState.update { it.copy(sheet = null) }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.write(null)
            apiKeyRepository.write(null)
            userProfileRepository.clear()
            _uiState.update { it.copy(sheet = null) }
            _events.send(ShellEvent.LoggedOut)
        }
    }
}
