package com.example.scanner.home

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.scanner.R
import com.example.scanner.ui.theme.ScannerTheme
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import io.paperdb.Paper

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Paper.init(this)

        enableEdgeToEdge()
        setContent {
            ScannerTheme() {

                HomeScreen()
            }
        }
    }
}