package com.example.scanner.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.scanner.ScannedProduct
import com.example.scanner.testProducts
import kotlinx.coroutines.flow.MutableStateFlow

sealed class HistoryUIState{
    data object Loading : HistoryUIState()

    data class Success(val scannedProducts : List<ScannedProduct>) : HistoryUIState()

    data class Failure(val message: String) : HistoryUIState()
}

class HistoryViewModel: ViewModel(){

    val uiStateFlow = MutableStateFlow<HistoryUIState>(HistoryUIState.Loading)

    fun loadScannedProducts(){
        uiStateFlow.value = HistoryUIState.Loading

        // recup les produits scannés dans le local storage

//        if(produitsScannes != null) {
//            uiStateFlow.value = HistoryUIState.Success(listOf(produitsScannes))
//        } else {
//            uiStateFlow.value = HistoryUIState.Failure("Erreur dans la recuperation des produits")
//        }
        uiStateFlow.value = HistoryUIState.Success(testProducts)

    }


}