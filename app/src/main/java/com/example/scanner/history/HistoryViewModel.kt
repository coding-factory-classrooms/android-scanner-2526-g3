import androidx.lifecycle.ViewModel
import com.example.scanner.data.remote.ProductData
import com.example.scanner.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.scanner.ScannedProduct
import com.example.scanner.testProducts
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow

sealed class HistoryUIState{
    data object Loading : HistoryUIState()

    data class Success(val scannedProducts : List<ScannedProduct>) : HistoryUIState()

    data class Failure(val message: String) : HistoryUIState()
}

class HistoryViewModel: ViewModel(){
  
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    val uiStateFlow = MutableStateFlow<HistoryUIState>(HistoryUIState.Loading)

    fun loadScannedProducts(){
        uiStateFlow.value = HistoryUIState.Loading

        addProductToDB(testProducts[0]) // un test d'ajout

        // recup les produits scannés dans le local storage

//        if(produitsScannes != null) {
//            uiStateFlow.value = HistoryUIState.Success(listOf(produitsScannes))
//        } else {
//            uiStateFlow.value = HistoryUIState.Failure("Erreur dans la recuperation des produits")
//        }
        val existingList = Paper.book().read<List<ScannedProduct>>("products", testProducts)
        Log.i("list",existingList.toString())
        uiStateFlow.value = HistoryUIState.Success(testProducts)
    }
    
    fun addProduct(product: Product?) {
        product?.let {
            _products.value = _products.value + it
        }
    }

    // ajout du produit en question dans la db
    fun addProductToDB(product: ScannedProduct) {
        // il y a des safe call car la liste products peut être vide
        val existingList = Paper.book().read<List<ScannedProduct>>("products", testProducts)
        val updatedList = existingList?.toMutableList()
        updatedList!!.add(product)
        Paper.book().write("products", updatedList)
    }


}
