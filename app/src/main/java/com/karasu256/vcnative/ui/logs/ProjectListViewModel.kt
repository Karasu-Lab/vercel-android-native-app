package com.karasu256.vcnative.ui.logs

import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.repository.ProjectRepository
import com.karasu256.vcnative.ui.components.Refreshable
import com.karasu256.vcnative.ui.deployments.create.ProjectItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectListViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
) : ViewModel(), Refreshable {

    val projects: StateFlow<List<ProjectItem>?> = projectRepository.projects
        .map { list -> list?.map { ProjectItem(it.project, it.avatar?.asImageBitmap()) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch { projectRepository.load() }
    }

    override fun refresh() {
        if (_isRefreshing.value) return
        _isRefreshing.value = true
        viewModelScope.launch {
            try {
                projectRepository.load(force = true)
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
