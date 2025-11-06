package com.example.scanner.productDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.scanner.ScannedProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    vm: ProductDetailsViewModel = viewModel(),
    product: ScannedProduct?
) {
    val uiState by vm.uiStateFlow.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadDetailedProducts(product)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Détails du produit")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            DetailsBody(uiState)
        }
    }
}

@Composable
fun DetailsBody(state: ProductDetailsUIState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ProductDetailsUIState.Failure -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            ProductDetailsUIState.Loading -> {
                CircularProgressIndicator()
            }
            is ProductDetailsUIState.Success -> {
                GeneralInfo(state.detailedProduct)
            }
        }
    }
}

@Composable
fun GeneralInfo(product: ScannedProduct) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.imageFrontURL),
            contentDescription = product.productNameFr,
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )
        // Nom du produit
        Text(
            text = product.productNameFr,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Marques
        if (product.brandsTags.isNotEmpty()) {
            InfoSection(
                title = "Marque",
                content = product.brandsTags.joinToString(", ")
            )
        }

        // Quantité
        if (product.quantity.isNotBlank()) {
            InfoSection(
                title = "Quantité",
                content = product.quantity
            )
        }

        // Catégories
        if (product.categoriesTagsFr.isNotEmpty()) {
            InfoSection(
                title = "Catégories",
                content = product.categoriesTagsFr.joinToString(", ") {
                    it.removePrefix("fr:").replace("-", " ").capitalize()
                }
            )
        }

        // Allergènes
        if (product.allergensTagsFr.isNotEmpty()) {
            InfoSection(
                title = "Allergènes",
                content = product.allergensTagsFr.joinToString(", ") {
                    it.removePrefix("fr:").replace("-", " ").capitalize()
                },
                isWarning = true
            )
        }

        // Ingrédients
        if (product.ingredientsTagsFr.isNotEmpty()) {
            InfoSection(
                title = "Ingrédients",
                content = product.ingredientsTagsFr.joinToString(", ") {
                    it.removePrefix("fr:").replace("-", " ").capitalize()
                }
            )
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    content: String,
    isWarning: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isWarning) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isWarning) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isWarning) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}