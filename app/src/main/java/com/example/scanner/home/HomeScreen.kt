package com.example.scanner.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scanner.ui.theme.ScannerTheme


@Composable
fun HomeScreen(
    onNavigateToScan: (isSimulated: Boolean) -> Unit = { _ -> },
    onNavigateToHistory: () -> Unit = {}
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Scan")

            Spacer(Modifier.height(32.dp))

            ScanButton(onButtonClick = {
                onNavigateToScan(false)
            })

            Spacer(Modifier.height(12.dp))

            SimulatedScanButton(onButtonClick = {
                onNavigateToScan(true)
            })

            Spacer(Modifier.height(12.dp))

            HistoryButton(onButtonClick = {
                onNavigateToHistory()
            })
        }
    }
}

@Composable
private fun ScanButton(onButtonClick: () -> Unit) {
    Button(
        onClick = onButtonClick
    ) {
        Text("Scan")
    }
}

@Composable
private fun SimulatedScanButton(onButtonClick: () -> Unit) {
    Button(
        onClick = onButtonClick
    ) {
        Text("Scan simulé")
    }
}

@Composable
private fun HistoryButton(onButtonClick: () -> Unit) {
    Button(
        onClick = onButtonClick
    ) {
        Text("History")
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    ScannerTheme() {
        HomeScreen()
    }
}