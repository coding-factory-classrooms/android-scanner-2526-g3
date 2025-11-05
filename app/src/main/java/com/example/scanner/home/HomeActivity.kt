package com.example.scanner.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.scanner.history.HistoryActivity
import com.example.scanner.scan.ScanActivity
import com.example.scanner.ui.theme.ScannerTheme
import io.paperdb.Paper

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Paper.init(this)

        enableEdgeToEdge()
        setContent {
            ScannerTheme {
                HomeScreen(
                    onNavigateToScan = { isSimulated ->
                        val intent = Intent(this, ScanActivity::class.java)
                        intent.putExtra("simulated", isSimulated)
                        if (isSimulated) {
                            intent.putExtra("numbersBarCode", "3274080005003")
                        }
                        startActivity(intent)
                    },
                    onNavigateToHistory = {
                        val intent = Intent(this, HistoryActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}