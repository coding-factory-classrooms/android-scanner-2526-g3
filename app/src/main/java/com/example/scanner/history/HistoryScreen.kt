package com.example.scanner.history

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.scanner.ScannedProduct
import com.example.scanner.ui.theme.ScannerTheme

@Composable
fun HistoryScreen(vm: HistoryViewModel = viewModel()) {
    val uiState by vm.uiStateFlow.collectAsState()

    vm.loadScannedProducts()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Historique des produits scannés",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            HistoryBody(uiState)
        }
    }
}

@Composable
fun HistoryBody(state: HistoryUIState) {
    when (state) {
        is HistoryUIState.Failure -> Text(state.message)
        HistoryUIState.Loading -> CircularProgressIndicator()
        is HistoryUIState.Success -> LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.scannedProducts) { product ->
                ProductCard(product)
            }
        }
    }
}

@Composable
fun ProductCard(product: ScannedProduct) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.imageFrontURL),
                contentDescription = product.productNameFr,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )

            Column {
                Log.i("product",product.toString())
                Text(product.productNameFr)
                Text(product.brandsTags[0])
                Text(product.lastScanDate.toString())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    ScannerTheme {
        // ViewModel non nécessaire pour preview → état vide
        HistoryScreen()
    }
}
