package com.karasu256.vcnative.ui.settings.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.local.AppDatabase
import com.karasu256.vcnative.data.local.CacheDatabase
import com.karasu256.vcnative.data.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class DataManagementUiState(
    val isConfirmingDeleteAppData: Boolean = false,
    val isRunning: Boolean = false,
    val appDataSizeBytes: Long? = null,
    val cacheSizeBytes: Long? = null,
)

sealed interface DataManagementEvent {
    data object CacheDeleted : DataManagementEvent
    data object AppDataDeleted : DataManagementEvent
}

@HiltViewModel
class DataManagementViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDatabase: AppDatabase,
    private val cacheDatabase: CacheDatabase,
    private val settingsRepository: AppSettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataManagementUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<DataManagementEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        refreshSizes()
    }

    private fun refreshSizes() {
        viewModelScope.launch {
            val (appData, cache) = withContext(Dispatchers.IO) {
                context.filesDir.sizeBytes() to context.cacheDir.sizeBytes()
            }
            _uiState.update { it.copy(appDataSizeBytes = appData, cacheSizeBytes = cache) }
        }
    }

    private fun File.sizeBytes(): Long = walkTopDown().filter { it.isFile }.sumOf { it.length() }

    fun requestDeleteAppData() {
        _uiState.update { it.copy(isConfirmingDeleteAppData = true) }
    }

    fun dismissDeleteAppData() {
        _uiState.update { it.copy(isConfirmingDeleteAppData = false) }
    }

    fun deleteCache() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cacheDatabase.clearAllTables()
                context.cacheDir.listFiles()
                    ?.filterNot { it.name.startsWith(CacheDatabase.NAME) }
                    ?.forEach { it.deleteRecursively() }
            }
            refreshSizes()
            _events.send(DataManagementEvent.CacheDeleted)
        }
    }

    fun deleteAppData() {
        if (_uiState.value.isRunning) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true) }
            withContext(Dispatchers.IO) {
                appDatabase.clearAllTables()
                cacheDatabase.clearAllTables()
            }
            settingsRepository.restore()
            _uiState.update { DataManagementUiState() }
            refreshSizes()
            _events.send(DataManagementEvent.AppDataDeleted)
        }
    }
}
