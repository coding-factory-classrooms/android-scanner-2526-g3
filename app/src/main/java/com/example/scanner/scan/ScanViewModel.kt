package com.example.scanner.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.data.remote.ProductRepository
import com.example.scanner.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    fun fetchProduct(barcode: String, onFetched: (Product?) -> Unit) {
        viewModelScope.launch {
            val productData = repository.getProductByBarcode(barcode)
            val product = productData
            productData?.let {
                _products.value = _products.value + it
            }
            onFetched(product)
        }
    }
}


