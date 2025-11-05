package com.example.scanner.history

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.scanner.ScannedProduct
import com.example.scanner.ui.theme.ScannerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(vm: HistoryViewModel = viewModel()) {

    val uiState by vm.uiStateFlow.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadScannedProducts()
    }

    Scaffold(topBar = {
        TopAppBar(
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text("Scanned Products")
            }
        )
    }) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
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
    Card(
        modifier = Modifier
        .fillMaxWidth()
        .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )) {
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