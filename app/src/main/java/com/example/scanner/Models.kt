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
) : Parcelable

val testProducts = listOf(ScannedProduct(
    listOf("nutella"),
    "898883283834",
    "https://plus.unsplash.com/premium_photo-1664474619075-644dd191935f?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8aW1hZ2V8ZW58MHx8MHx8fDA%3D&fm=jpg&q=60&w=3000",
    "Pot de Nutella",
    Calendar.getInstance().time,
    "400g",
    listOf("Lait", "Fruits à coque", "Soja"),
    listOf( "Petit-déjeuners",
        "Produits à tartiner",
        "Produits à tartiner sucrés",
        "Pâtes à tartiner",
        "Pâtes à tartiner aux noisettes",
        "Pâtes à tartiner au chocolat",
        "Pâtes à tartiner aux noisettes et au cacao"), listOf(      "Sucre",
        "Sucre ajouté",
        "Disaccharide",
        "Huile de palme",
        "Huiles et graisses",
        "Huiles et graisses végétales",
        "Huile et matière grasse de palme",
        "Noisette",
        "Fruits à coque",
        "en:Tree nut",
        "Cacao maigre",
        "Plante",
        "Cacao",
        "Lait en poudre écrémé",
        "Produits laitiers et dérivées",
        "Lait en poudre",
        "Lactosérum en poudre",
        "Petit-lait",
        "Émulsifiant",
        "Vanilline",
        "E322",
        "Lécithine de soja",
        "E322i")))





