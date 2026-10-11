package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectFilterRepository @Inject constructor() {
    private val _selected = MutableStateFlow<Project?>(null)
    val selected = _selected.asStateFlow()

    fun select(project: Project?) {
        _selected.value = project
    }
}
