package com.example.scanner.history

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.scanner.ScannedProduct
import com.example.scanner.productDetails.ProductDetailsActivity
import com.example.scanner.scan.ScanActivity
import com.example.scanner.ui.theme.ScannerTheme
import com.example.scanner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(vm: HistoryViewModel = viewModel()) {
    val uiState by vm.uiStateFlow.collectAsState()

    // Effectuer une seule fois le chargement des produits
    LaunchedEffect (Unit) {
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
    val context = LocalContext.current
    when (state) {
        is HistoryUIState.Failure -> Text(state.message)
        HistoryUIState.Loading -> CircularProgressIndicator()
        is HistoryUIState.Success ->
            ProductsList(state)
    }
}

@Composable
private fun ProductsList(state: HistoryUIState.Success) {
    // on crée la variable pour le champ de recherche
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current


    // on filtre les produits par le nom du produit, en ne regardant pas les majuscules
    var filteredProducts = state.scannedProducts.filter { productMap ->
        (productMap.productNameFr as? String)
            ?.contains(searchQuery, ignoreCase = true)
            ?: false
    }

    // les favoris en premiers
    filteredProducts = filteredProducts.sortedBy { !it.isFavorite }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Rechercher un produit") },
        shape = RoundedCornerShape(12.dp),
        value = searchQuery,
        onValueChange = {searchQuery = it}
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(filteredProducts) { product ->
            ProductCard(product, context)
        }
    }
}

@Composable
fun ProductCard(product: ScannedProduct, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            Modifier.fillMaxWidth(),
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
                Log.i("product", product.toString())
                Text(product.productNameFr)
                Text(product.brandsTags.toString())
                Text(product.lastScanDate.toString())
                DetailsButton(onButtonClick = {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra("product", product)
                    context.startActivity(intent)
                })
            }

            Column(verticalArrangement = Arrangement.Top) {
                FavoriteButton(product)
                ShareButton(product)
                DeleteButton(product)
            }

        }
    }
}

@Composable
private fun FavoriteButton(product : ScannedProduct, vm: HistoryViewModel = viewModel()) {
    var isToggled by remember { mutableStateOf(product.isFavorite) }
    val context = LocalContext.current

    IconButton(
        onClick = {
            isToggled = !isToggled
            vm.changeFavoriteProduct(product, context)
        }
    ) {
        Icon(
            modifier = Modifier.height(20.dp),
            painter = if (isToggled) painterResource(R.drawable.favorite_filled) else painterResource(R.drawable.favorite),
            contentDescription = "Bouton favori"
        )
    }
}

@Composable
private fun ShareButton(product : ScannedProduct, vm: HistoryViewModel = viewModel()) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            vm.shareProduct(product, context)
        }
    ) {
        Icon(
            modifier = Modifier.height(20.dp),
            painter = painterResource(R.drawable.share),
            contentDescription = "Bouton de partage"
        )
    }
}

@Composable
private fun DeleteButton(product : ScannedProduct, vm: HistoryViewModel = viewModel()) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            vm.deleteProduct(product, context)
            vm.loadScannedProducts()
        }
    ) {
        Icon(
            modifier = Modifier.height(20.dp),
            painter = painterResource(R.drawable.delete),
            contentDescription = "Bouton de supression"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    ScannerTheme {
        HistoryScreen()
    }
}

@Composable
fun DetailsButton(onButtonClick: () -> Unit) {
    Button(
        onClick = onButtonClick
    ) {
        Text("Details")
    }
}
