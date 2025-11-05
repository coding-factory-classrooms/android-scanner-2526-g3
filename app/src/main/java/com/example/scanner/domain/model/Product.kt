package com.example.scanner.domain.model

import java.util.Date

data class Product(
    val name: String,
    val brand: String,
    val quantity: String,
    val imageUrl: String,
    val lastScanDate: Date
)


