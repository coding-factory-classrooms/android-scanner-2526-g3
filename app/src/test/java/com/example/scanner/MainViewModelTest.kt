package com.example.retrofitapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.scanner.data.remote.ProductApi
import com.example.scanner.data.remote.ProductData
import com.example.scanner.data.remote.ProductDto
import com.example.scanner.data.remote.ProductRepository
import com.example.scanner.scan.ScanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FakeApi : ProductApi {
    override suspend fun getProductByBarcode(barcode: String): ProductDto {
        return ProductDto(
            status = 1,
            product = ProductData(
                product_name = "Coca Cola",
                brands = "Coca-Cola",
                quantity = "330ml",
                image_url = "https://m.media-amazon.com/images/I/61lMtYzJENL.jpg"
            )
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

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
    fun `valid product is scanned`() = runTest {
        // Arrange
        val api = FakeApi()
        val repository = ProductRepository(api)
        val viewModel = ScanViewModel(repository)

        // Act
        var callbackCalled = false
        viewModel.fetchProduct("1234") { 
            callbackCalled = true
        }

        // Assert
        assertEquals(1, viewModel.products.value.size)
        assertEquals("Coca Cola", viewModel.products.value[0].name)
        assertEquals("Coca-Cola", viewModel.products.value[0].brand)
        assertTrue("Callback should be called on success", callbackCalled)
    }


}
