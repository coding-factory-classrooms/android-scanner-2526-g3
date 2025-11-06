package com.example.scanner.data.remote

data class ProductDto(
    val status: Int,
    val product: ProductData?
)

data class ProductData(
    val product_name: String?,
    val brands_tags: List<String>?,
    val quantity: String?,
    val image_front_url: String?,
    val allergens_tags_fr: List<String>?,
    val categories_tags_fr: List<String>?,
    val ingredients_tags_fr: List<String>?,
)


