package com.example.scanner

import java.util.Date

data class ScannedProduct (
    val brandsTags: List<String>,
    val categories: String,
    val code: String,
    val imageFrontURL: String,
    val productNameFr: String,
    val lastScanDate: Date
)
