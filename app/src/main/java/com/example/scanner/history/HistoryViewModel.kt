package com.example.scanner.history

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.scanner.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log
import android.widget.Toast
import com.example.scanner.LocalStorage
import com.example.scanner.LocalStoragePaper
import com.example.scanner.ScannedProduct
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class HistoryUIState{
    data object Loading : HistoryUIState()

    data class Success(val scannedProducts : List<ScannedProduct>) : HistoryUIState()

    data class Failure(val message: String) : HistoryUIState()
}

private val ls = LocalStoragePaper()

class HistoryViewModel : ViewModel(), LocalStorage by ls {
  
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    val uiStateFlow = MutableStateFlow<HistoryUIState>(HistoryUIState.Loading)
    
    init {
        loadProducts()
    }

    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
        return formatter.format(date)
    }

    private fun loadProducts() {
        // Charge les produits depuis Paper DB au démarrage

        val savedProducts = ls.getProducts()
        if (savedProducts.isNotEmpty()) {
            // Convertir ScannedProduct en Product
            val productList = savedProducts.map { scannedProduct ->
                Product(
                    name = scannedProduct.productNameFr,
                    brand = scannedProduct.brandsTags,
                    quantity = scannedProduct.quantity,
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
        val productList = ls.getProducts()
        Log.i("interface",productList.toString())
        
        if (productList.isNotEmpty()) {
            uiStateFlow.value = HistoryUIState.Success(productList)
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
        val existingList = ls.getProducts()
        val updatedList = existingList.toMutableList()

        // on récupère le produit via sa date de scan (car on a pas d'id)
        val indexToUpdate = updatedList.indexOfFirst {
            it.lastScanDate == product.lastScanDate
        }

        if (indexToUpdate != -1) {
            Log.i("interface before",updatedList[indexToUpdate].toString())
            val updatedProduct = updatedList[indexToUpdate].copy(isFavorite = favoriteProductValue)
            Log.i("interface after",updatedProduct.toString())
            val result = ls.updateProduct(indexToUpdate, updatedProduct)

            if (!result) {
                Toast.makeText(context, "Problème lors de la modification du produit", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Produit non trouvé :(", Toast.LENGTH_SHORT).show()
        }
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

    fun deleteProduct(product: ScannedProduct, context: Context) {

        val result = ls.deleteProduct(product)

        if (result) {
            Toast.makeText(context, "Produit supprimé de la liste", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Produit non trouvé :(", Toast.LENGTH_SHORT).show()
        }

    }
}
