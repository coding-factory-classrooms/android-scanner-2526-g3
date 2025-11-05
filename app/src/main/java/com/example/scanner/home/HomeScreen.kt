package com.example.scanner.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scanner.history.HistoryActivity
import com.example.scanner.scan.ScanActivity
import com.example.scanner.ui.theme.ScannerTheme


@Composable
fun HomeScreen() {
    val context = LocalContext.current

    Scaffold() { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)
        {

            Text("Scan")

            Spacer(Modifier.height(32.dp))


            ScanButton(onButtonClick = {

                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra("simulated",false)
                context.startActivity(intent)
            })

            Spacer(Modifier.height(12.dp))

            SimulatedScanButton(onButtonClick = {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra("simulated",true)
                intent.putExtra("numbersBarCode","3274080005003")
                context.startActivity(intent)
            })

            Spacer(Modifier.height(12.dp))

            HistoryButton(onButtonClick = {
                val intent = Intent(context, HistoryActivity::class.java)
                context.startActivity(intent)
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