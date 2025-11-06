package com.example.scanner.scan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.ScannedProduct
import com.example.scanner.data.remote.ProductRepository
import com.example.scanner.domain.model.Product
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class ScanState {
    data object Initial : ScanState()
    data object Normal : ScanState()
    data object Simulated : ScanState()
}

class ScanViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    val scanStateFlow = MutableStateFlow<ScanState>(ScanState.Initial)

    fun isSimulated(isSimulated: Boolean) {
        scanStateFlow.value = when(isSimulated) {
            true -> ScanState.Simulated
            false -> ScanState.Normal
        }
    }

    fun fetchProduct(barcode: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val product = repository.getProductByBarcode(barcode)
            
            if (product != null) {
                _products.value = _products.value + product
                
                // mettre le produit dans paper
                saveProductToHistory(product, barcode)
                onSuccess()
            }
        }
    }
    
    private fun saveProductToHistory(product: Product, barcode: String) {
        try {
            val scannedProduct = ScannedProduct(
                brandsTags = listOf(product.brand),
                code = barcode,
                imageFrontURL = product.imageUrl,
                productNameFr = product.name,
                lastScanDate = Calendar.getInstance().time
            )
            
            val existingList = Paper.book().read<List<ScannedProduct>>("products", emptyList()) ?: emptyList()
            val updatedList = existingList.toMutableList()
            updatedList.add(scannedProduct)
            Paper.book().write("products", updatedList)
            
            Log.i("ScanViewModel", scannedProduct.toString())
        } catch (e: Exception) {
            // Could not save to Paper DB (possibly in test environment)
            // Silent fail is OK here as we don't want to block the product addition
        }
    }
}


