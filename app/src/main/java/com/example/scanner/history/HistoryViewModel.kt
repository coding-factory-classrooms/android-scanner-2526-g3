package com.example.scanner.history

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.scanner.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log
import android.widget.Toast
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
                    brand = scannedProduct.brandsTags.firstOrNull() ?: "",
                    quantity = "",
                    imageUrl = scannedProduct.imageFrontURL,
                    lastScanDate = scannedProduct.lastScanDate
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
        Log.i("HistoryViewModel", "Produits: ${existingList}")
        
        if (existingList.isNotEmpty()) {
            uiStateFlow.value = HistoryUIState.Success(existingList)
        } else {
            uiStateFlow.value = HistoryUIState.Failure("Aucun produit scanné")
        }
    }


    fun changeFavoriteProduct(product: ScannedProduct, context: Context) {
        val favoriteProductValue = !product.isFavorite

        when (favoriteProductValue) {
            true -> {
                Toast.makeText(context, product.productNameFr + " mis en favori", Toast.LENGTH_SHORT).show()
            }
            false -> {
                Toast.makeText(context, product.productNameFr + " retiré des favoris", Toast.LENGTH_SHORT).show()
            }
        }

        // modification du produit
        val existingList = Paper.book().read<List<ScannedProduct>>("products", emptyList()) ?: emptyList()
        val updatedList = existingList.toMutableList()

        // on récupère le produit via sa date de scan (car on a pas d'id)
        val indexToUpdate = updatedList.indexOfFirst {
            it.lastScanDate == product.lastScanDate
        }

        if (indexToUpdate != -1) {
            val updatedProduct = updatedList[indexToUpdate].copy(isFavorite = favoriteProductValue)
            updatedList[indexToUpdate] = updatedProduct
        } else {
            Toast.makeText(context, "Produit non trouvé :(", Toast.LENGTH_SHORT).show()
        }

        Paper.book().write("products", updatedList)
    }

    fun shareProduct(product: ScannedProduct, context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            // on envoit le texte à envoyer dans l'intent spécial de "partage"
            putExtra(Intent.EXTRA_TEXT,
                "${product.productNameFr}\n" +
                        "Scanné le ${product.lastScanDate}\n" +
                        "Marque ${product.brandsTags}\n" +
                        "Code barre ${product.code}\n"
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
}
