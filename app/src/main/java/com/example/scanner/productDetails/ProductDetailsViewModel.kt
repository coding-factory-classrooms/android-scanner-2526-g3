package com.example.scanner.productDetails

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.scanner.ScannedProduct

sealed class ProductDetailsUIState{
    data object Loading : ProductDetailsUIState()

    data class Success(val detailedProduct : ScannedProduct) : ProductDetailsUIState()

    data class Failure(val message: String) : ProductDetailsUIState()
}

class ProductDetailsViewModel(): ViewModel(){

    val uiStateFlow = MutableStateFlow<ProductDetailsUIState>(ProductDetailsUIState.Loading)

    fun loadDetailedProducts(detailedProduct: ScannedProduct?){
        uiStateFlow.value = ProductDetailsUIState.Loading

        if(detailedProduct != null ){
            uiStateFlow.value = ProductDetailsUIState.Success(detailedProduct)
        } else {
            uiStateFlow.value = ProductDetailsUIState.Failure("erreur dans le chargement des details du produit")
        }
    }


}
