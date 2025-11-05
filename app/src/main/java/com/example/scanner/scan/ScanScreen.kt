package com.example.scanner.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scanner.ui.theme.ScannerTheme


@Composable
fun ScanScreen() {

    Scaffold() { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)
        {

            // la cam
            

            ScanButton(onButtonClick = {
                //
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


@Preview
@Composable
fun ScanScreenPreview() {
    ScannerTheme() {
        ScanScreen()
    }
}