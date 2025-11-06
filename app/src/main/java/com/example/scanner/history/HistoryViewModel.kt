package com.example.scanner.history

import androidx.lifecycle.ViewModel
import com.example.scanner.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log
import com.example.scanner.ScannedProduct
import io.paperdb.Paper

sealed class HistoryUIState{
    data object Loading : HistoryUIState()

    data class Success(val scannedProducts : List<ScannedProduct>) : HistoryUIState()

    data class Failure(val message: String) : HistoryUIState()
}

class HistoryViewModel: ViewModel(){
  
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    val uiStateFlow = MutableStateFlow<HistoryUIState>(HistoryUIState.Loading)
    
    init {
        loadProducts()
    }

    private fun loadProducts() {
        // Charge les produits depuis Paper DB au démarrage
        val savedProducts = Paper.book().read<List<ScannedProduct>>("products", emptyList()) ?: emptyList()
        if (savedProducts.isNotEmpty()) {
            // Convertir ScannedProduct en Product
            val productList = savedProducts.map { scannedProduct ->
                Product(
                    name = scannedProduct.productNameFr,
                    brand = scannedProduct.brandsTags,
                    quantity = "",
                    imageUrl = scannedProduct.imageFrontURL,
                    lastScanDate = scannedProduct.lastScanDate,
                    allergensTagsFr = scannedProduct.allergensTagsFr,
                    categoriesTagsFr = scannedProduct.categoriesTagsFr,
                    ingredientsTagsFr = scannedProduct.ingredientsTagsFr
                )
            }
            _products.value = productList
        }
    }

    fun loadScannedProducts(){
        uiStateFlow.value = HistoryUIState.Loading

        // Recup les produits scannés dans le local storage
        val existingList = Paper.book().read<List<ScannedProduct>>("products", emptyList()) ?: emptyList()
        Log.i("HistoryViewModel", "Produits chargés: ${existingList.size}")
        
        if (existingList.isNotEmpty()) {
            uiStateFlow.value = HistoryUIState.Success(existingList)
        } else {
            uiStateFlow.value = HistoryUIState.Failure("Aucun produit scanné")
        }
    }
}
