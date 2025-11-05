package com.example.scanner

import android.os.Build
import androidx.annotation.RequiresApi
import io.paperdb.Paper
import java.util.Calendar
import java.util.Date

data class ScannedProduct (
    val brandsTags: List<String>,
    val code: String,
    val imageFrontURL: String,
    val productNameFr: String,
    val lastScanDate: Date
)

val testProducts = listOf(ScannedProduct(listOf("nutella"), "898883283834", "https://plus.unsplash.com/premium_photo-1664474619075-644dd191935f?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8aW1hZ2V8ZW58MHx8MHx8fDA%3D&fm=jpg&q=60&w=3000", "Pot de Nutella",
    Calendar.getInstance().time))





