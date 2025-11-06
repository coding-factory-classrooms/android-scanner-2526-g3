package com.example.scanner.productDetails

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.scanner.ScannedProduct
import com.example.scanner.domain.model.Product
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ProductDetailsUIState{
    data object Loading : ProductDetailsUIState()

    data class Success(val detailedProduct : ScannedProduct) : ProductDetailsUIState()

    data class Failure(val message: String) : ProductDetailsUIState()
}

class ProductDetailsViewModel(): ViewModel(){

    private val _product = MutableStateFlow<ScannedProduct?>(null)
    val product: StateFlow<ScannedProduct?> = _product.asStateFlow()

    val uiStateFlow = MutableStateFlow<ProductDetailsUIState>(ProductDetailsUIState.Loading)

    fun loadDetailedProducts(detailedProduct: ScannedProduct?){
        uiStateFlow.value = ProductDetailsUIState.Loading

        if(detailedProduct != null ){
            _product.value = detailedProduct
            uiStateFlow.value = ProductDetailsUIState.Success(detailedProduct)
        } else {
            uiStateFlow.value = ProductDetailsUIState.Failure("erreur dans le chargement des details du produit")
        }
    }
}
