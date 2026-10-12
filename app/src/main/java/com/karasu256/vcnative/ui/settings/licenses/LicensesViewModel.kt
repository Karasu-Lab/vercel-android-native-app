package com.karasu256.vcnative.ui.settings.licenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karasu256.vcnative.data.model.License
import com.karasu256.vcnative.data.repository.LicenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicensesViewModel @Inject constructor(
    private val repository: LicenseRepository,
) : ViewModel() {

    private val _licenses = MutableStateFlow<List<License>>(emptyList())
    val licenses = _licenses.asStateFlow()

    init {
        viewModelScope.launch { _licenses.value = repository.list() }
    }
}
