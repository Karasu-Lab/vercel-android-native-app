package com.karasu256.vcnative.ui.settings.licenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.License
import com.karasu256.vcnative.data.repository.LicenseRepository
import com.ramcosta.composedestinations.generated.destinations.LicenseDetailScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LicenseDetailUiState(
    val license: License? = null,
    val content: String? = null,
)

@HiltViewModel
class LicenseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LicenseRepository,
) : ViewModel() {

    private val licenseId = LicenseDetailScreenDestination.argsFrom(savedStateHandle).licenseId

    private val _uiState = MutableStateFlow(LicenseDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val license = repository.find(licenseId)
            _uiState.update { it.copy(license = license) }
            val content = repository.read(licenseId)
            _uiState.update { it.copy(content = content) }
        }
    }
}
