package com.example.scanner

import java.util.Calendar
import java.util.Date
import android.os.Parcelable
import com.google.android.material.color.utilities.QuantizerWu
import kotlinx.parcelize.Parcelize
import java.util.Queue

@Parcelize
data class ScannedProduct (
    val brandsTags: List<String>,
    val code: String,
    val imageFrontURL: String,
    val productNameFr: String,
    val lastScanDate: Date,
    val quantity: String,
    val allergensTagsFr: List<String>,
    val categoriesTagsFr: List<String>,
    val ingredientsTagsFr: List<String>,
    val isFavorite: Boolean = false,
) : Parcelable





