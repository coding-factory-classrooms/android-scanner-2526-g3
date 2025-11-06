package com.example.scanner.domain.model

import java.util.Date

data class Product(
    val name: String,
    val brand: List<String>,
    val quantity: String,
    val imageUrl: String,
    val lastScanDate: Date,
    val allergensTagsFr: List<String>,
    val categoriesTagsFr: List<String>,
    val ingredientsTagsFr: List<String>,
)


