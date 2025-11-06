package com.example.scanner

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scanner.productDetails.ProductDetailsUIState
import com.example.scanner.productDetails.ProductDetailsViewModel
import com.example.scanner.scan.ScanScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun produit_charge_avec_succes() = runTest {
        val produit = ScannedProduct(
            brandsTags = listOf("suntat"),
            code = "8690804360732",
            imageFrontURL = "https://images.openfoodfacts.net/images/products/869/080/436/0732/front_fr.22.400.jpg",
            productNameFr = "Latte cappuccino",
            lastScanDate = Calendar.getInstance().time,
            quantity = "250 ml",
            allergensTagsFr = emptyList(),
            categoriesTagsFr = listOf("Boissons", "Produits laitiers", "Boissons lactées", "Boissons au café", "Boissons lactées au café"),
            ingredientsTagsFr = listOf("Cafe", "Sucre"),
            isFavorite = false
        )

        val viewModel = ProductDetailsViewModel()
        viewModel.loadDetailedProducts(produit)

        // verifie que UiState est success
        assertTrue(viewModel.uiStateFlow.value is ProductDetailsUIState.Success)

        // verifie produit
        assertEquals(listOf("suntat"), viewModel.product.value?.brandsTags)
        assertEquals("8690804360732", viewModel.product.value?.code)
        assertEquals("https://images.openfoodfacts.net/images/products/869/080/436/0732/front_fr.22.400.jpg", viewModel.product.value?.imageFrontURL)
        assertEquals("Latte cappuccino", viewModel.product.value?.productNameFr)
        assertEquals("250 ml", viewModel.product.value?.quantity)
        assertEquals(emptyList<String>(), viewModel.product.value?.allergensTagsFr)
        assertEquals(listOf("Boissons", "Produits laitiers", "Boissons lactées", "Boissons au café", "Boissons lactées au café"), viewModel.product.value?.categoriesTagsFr)
    }

    @Test
    fun produit_non_charge() = runTest {
        val produit = null

        val viewModel = ProductDetailsViewModel()
        viewModel.loadDetailedProducts(produit)

        // verifie que UiState est failure
        assertTrue(viewModel.uiStateFlow.value is ProductDetailsUIState.Failure)

        // verifie message d'erreur
        val failureState = viewModel.uiStateFlow.value as ProductDetailsUIState.Failure
        assertEquals("erreur dans le chargement des details du produit", failureState.message)
    }
}