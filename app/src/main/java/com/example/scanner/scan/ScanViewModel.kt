package com.example.scanner.scan

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
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
        scanStateFlow.value = when (isSimulated) {
            true -> ScanState.Simulated
            false -> ScanState.Normal
        }
    }

    fun fetchProduct(barcode: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val product = repository.getProductByBarcode(barcode)

            if (product != null) {
                _products.value = _products.value + product

                // mettre le produit dans paper
                val message = saveProductToHistory(product, barcode)
                onSuccess(message)
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

        val existingList = Paper.book().read<List<ScannedProduct>>("products", emptyList()) ?: emptyList()

        val existingProduct = existingList.find { product ->
            product.code == barcode
        }

        // copie de la liste existante en une liste modifiable
        val updatedList = existingList.toMutableList()

        if (existingProduct != null) {
            updatedList.remove(existingProduct)
        }
        val message = if (existingProduct !=null) {
            "Produit déjà ajouté"
        } else {
            "Produit ajouté"
        }

        updatedList.add(scannedProduct)

        Paper.book().write("products", updatedList)

        Log.i("ScanViewModel", scannedProduct.toString())

        return message
    }
}


