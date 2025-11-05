package com.example.scanner.history

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.enableEdgeToEdge
import com.example.scanner.ui.theme.ScannerTheme

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ScannerTheme {
                val historyViewModel: HistoryViewModel = viewModel()
                HistoryScreen(historyViewModel)
            }
        }
    }
}
