package com.example.scanner.scan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

sealed class ScanState {
    data object Initial : ScanState()
    data object Normal : ScanState()
    data object Simulated : ScanState()
}

class ScanViewModel : ViewModel() {

    val scanStateFlow = MutableStateFlow<ScanState>(ScanState.Initial)

    fun isSimulated(isSimulated: Boolean) {
        scanStateFlow.value = when(isSimulated) {
            true -> ScanState.Simulated
            false -> ScanState.Normal
        }
    }
}