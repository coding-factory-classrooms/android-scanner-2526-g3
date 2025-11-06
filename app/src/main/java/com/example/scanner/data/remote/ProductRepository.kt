package com.example.scanner.data.remote

import android.util.Log
import com.example.scanner.domain.model.Product
import java.util.Calendar

class ProductRepository(private val api: ProductApi) {

    suspend fun getProductByBarcode(barcode: String): Product? {
        return try {
            val dto = api.getProductByBarcode(barcode)
            println("DTO reçu : $dto") // debug
            if (dto.status == 1 && dto.product != null) {
                Product(
                    name = dto.product.product_name ?: "Inconnu",
                    brand = dto.product.brands_tags ?: emptyList(),
                    quantity = dto.product.quantity ?: "-",
                    imageUrl = dto.product.image_front_url ?: "",
                    lastScanDate = Calendar.getInstance().time,
                    allergensTagsFr = dto.product.allergens_tags_fr ?: emptyList(),
                    categoriesTagsFr = dto.product.categories_tags_fr ?: emptyList(),
                    ingredientsTagsFr = dto.product.ingredients_tags_fr ?: emptyList(),
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

