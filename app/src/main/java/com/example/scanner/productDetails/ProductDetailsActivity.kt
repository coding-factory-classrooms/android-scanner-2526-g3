package com.example.scanner.productDetails

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scanner.ScannedProduct
import com.example.scanner.ui.theme.ScannerTheme

class ProductDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product", ScannedProduct::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<ScannedProduct>("product")
        }

        enableEdgeToEdge()
        setContent {
            ScannerTheme {
                val productDetailsViewModel: ProductDetailsViewModel = viewModel()
                ProductDetailsScreen(productDetailsViewModel, product)
            }
        }
    }

}