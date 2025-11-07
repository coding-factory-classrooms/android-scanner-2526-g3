package com.example.scanner

import android.util.Log
import android.widget.Toast
import io.paperdb.Paper

interface LocalStorage {
    fun getProducts(): List<ScannedProduct>

    fun deleteProduct(product : ScannedProduct): Boolean

    fun addProduct(product: ScannedProduct): Boolean

    fun updateProduct(index : Int, product: ScannedProduct): Boolean
}

class LocalStoragePaper : LocalStorage{
    override fun getProducts(): List<ScannedProduct> {
        val savedProducts = Paper.book().read<List<ScannedProduct>>("products", emptyList()) ?: emptyList()
        return savedProducts
    }

    override fun deleteProduct(product: ScannedProduct): Boolean {
        val existingList = getProducts()
        val productList = existingList.toMutableList()

        // on récupère le produit via sa date de scan (car on a pas d'id)
        val indexToUpdate = productList.indexOfFirst {
            it.lastScanDate == product.lastScanDate
        }

        if (indexToUpdate != -1) {
            productList.removeAt(indexToUpdate)
            Paper.book().write("products", productList)
            return true
        } else {
           return false
        }
    }

    override fun addProduct(product: ScannedProduct): Boolean {
        val existingList = Paper.book().read<List<ScannedProduct>>("products", emptyList()) ?: emptyList()
        // copie de la liste existante en une liste modifiable
        val updatedList = existingList.toMutableList()

        updatedList.add(product)
        Paper.book().write("products", updatedList)

        return true
    }

    override fun updateProduct(index: Int, product: ScannedProduct): Boolean {
        val existingList = getProducts()
        val updatedList = existingList.toMutableList()

        // on récupère le produit via sa date de scan (car on a pas d'id)
        val indexToUpdate = updatedList.indexOfFirst {
            it.code == product.code
        }

        Log.i("interface",indexToUpdate.toString())

        if (indexToUpdate != -1) {
            updatedList[indexToUpdate] = product
            Log.i("interface fav",updatedList[indexToUpdate].toString())
            Paper.book().write("products", updatedList)
            return true
        } else {
            return false
        }
    }

}