package com.example.scanner.history


import HistoryViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.example.scanner.data.remote.ProductData
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.scanner.ScannedProduct
import com.example.scanner.ui.theme.ScannerTheme
import io.paperdb.Paper

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val products = viewModel.products.collectAsState()

    Scaffold { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            Text(
                text = "Historique des produits scannés",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn {
                items(products.value) { product ->
                    Row(modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(product.imageUrl)
                                .crossfade(true)
                                .scale(Scale.FILL)
                                .build(),
                            contentDescription = product.name,
                            modifier = Modifier.size(64.dp)
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text(product.brand)
                            Text(product.quantity)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryBody(state: HistoryUIState) {
    when (state) {
        is HistoryUIState.Failure -> Text(state.message)
        HistoryUIState.Loading -> CircularProgressIndicator()
        is HistoryUIState.Success -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(state.scannedProducts) { product ->
                ProductCard(product)
            }
        }
    }
}

@Composable
fun ProductCard(product: ScannedProduct) {
    Card {
        Row(Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            println(product.imageFrontURL)
            Image(
                painter = rememberAsyncImagePainter(product.imageFrontURL),
                contentDescription = product.productNameFr,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(product.productNameFr)
                Text(product.brandsTags[0])
                Text(product.lastScanDate.toString())
            }
        }
    }
}

@Preview
@Composable
fun MovieListScreenPreview() {
    ScannerTheme {
        HistoryScreen()
    }
}
