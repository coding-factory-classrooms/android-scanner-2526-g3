package com.example.scanner.data.remote

data class ProductDto(
    val status: Int,
    val product: ProductData?
)

data class ProductData(
    val product_name: String?,
    val brands: String?,
    val quantity: String?,
    val image_url: String?
)


