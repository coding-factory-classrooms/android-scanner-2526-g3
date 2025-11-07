package com.example.scanner.scan

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.LocalStorage
import com.example.scanner.LocalStoragePaper
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

private val ls = LocalStoragePaper()
class ScanViewModel(
    private val repository: ProductRepository
) : ViewModel(), LocalStorage by ls {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    val scanStateFlow = MutableStateFlow<ScanState>(ScanState.Initial)

    fun isSimulated(isSimulated: Boolean) {
        scanStateFlow.value = when (isSimulated) {
            true -> ScanState.Simulated
            false -> ScanState.Normal
        }
    }

    fun fetchProduct(barcode: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val product = repository.getProductByBarcode(barcode)

            if (product != null) {
                _product.value = product

                // mettre le produit dans paper
                val message = saveProductToHistory(product, barcode)
                onSuccess(message)
            }
        }
    }
    fun fetchProductTest(barcode: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val product = repository.getProductByBarcode(barcode)

            if (product != null) {
                _product.value = product
                onSuccess()
            }
        }
    }

    private fun saveProductToHistory(product: Product, barcode: String): String {
        val scannedProduct = ScannedProduct(
            brandsTags = product.brand,
            code = barcode,
            imageFrontURL = product.imageUrl,
            productNameFr = product.name,
            quantity = product.quantity,
            lastScanDate = Calendar.getInstance().time,
            allergensTagsFr = product.allergensTagsFr,
            categoriesTagsFr = product.categoriesTagsFr,
            ingredientsTagsFr = product.ingredientsTagsFr
        )


        val existingList = ls.getProducts()


        val existingProduct = existingList.find { product ->
            product.code == barcode
        }

        val index = existingList.indexOfFirst { product ->
            product.code == barcode
        }

        // copie de la liste existante en une liste modifiable
        val updatedList = existingList.toMutableList()


        if (existingProduct != null) {
            ls.updateProduct(index, existingProduct)
            updatedList.remove(existingProduct)
        } else {
            ls.addProduct(scannedProduct)
        }

        Log.i("ScanViewModel", scannedProduct.toString())

        return if (existingProduct != null) {
            "Produit déjà ajouté"
        } else {
            "Produit ajouté"
        }
    }
}


