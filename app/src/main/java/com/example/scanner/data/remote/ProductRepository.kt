package com.example.scanner.data.remote

import android.util.Log
import com.example.scanner.domain.model.Product

class ProductRepository(private val api: ProductApi) {

    suspend fun getProductByBarcode(barcode: String): Product? {
        return try {
            val dto = api.getProductByBarcode(barcode)
            println("DTO reçu : $dto") // debug
            if (dto.status == 1 && dto.product != null) {
                Product(
                    name = dto.product.product_name ?: "Inconnu",
                    brand = dto.product.brands ?: "N/A",
                    quantity = dto.product.quantity ?: "-",
                    imageUrl = dto.product.image_url ?: ""
                )
            } else {
                Log.e("ProductRepository", "Produit non trouvé pour le code: $barcode")
                null
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Erreur API pour le code: $barcode", e)
            null
        }
    }

}

