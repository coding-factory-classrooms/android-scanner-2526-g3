package com.example.scanner.scan

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scanner.history.GoBackButton
import com.example.scanner.ui.theme.ScannerTheme
import com.example.scanner.history.HistoryActivity
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.camera.core.Preview as CameraPreview

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalGetImage
@Composable
fun ScanScreen(
    scanViewModel: ScanViewModel = viewModel()
) {
    val product by scanViewModel.product.collectAsState()
    val scanState by scanViewModel.scanStateFlow.collectAsState()
    var scannedCode by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var productFetched by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // récupère la variable Simulated depuis l'activity
    val simulated = (context as? ComponentActivity)
        ?.intent
        ?.getBooleanExtra("simulated", false) ?: false

    // on update l'état de simulation
    scanViewModel.isSimulated(simulated)
    Log.i("simulated",simulated.toString())

    Scaffold(
        topBar = {
        TopAppBar(
            title = {
                Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp) ,verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Scanner")
                    GoBackButton()
                }

            },
        )
    }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            when(val p = scanState) {
                ScanState.Initial -> CircularProgressIndicator()
                ScanState.Normal -> AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraExecutor = Executors.newSingleThreadExecutor()

                        // la gestion de la caméra
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = CameraPreview.Builder().build().apply {
                                setSurfaceProvider(previewView.surfaceProvider)
                            }

                            // on détecte le barcode via l'image de la caméra
                            val analyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    // on détecte le code du barcode
                                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { code ->
                                        if (!isProcessing && !productFetched) {
                                            isProcessing = true
                                            scannedCode = code

                                            // on récupère le produit
                                            scanViewModel.fetchProduct(code) {
                                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                                productFetched = true
                                                isProcessing = false
                                            }
                                        }
                                    })
                                }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    analyzer
                                )
                            } catch (e: Exception) {
                                Log.e("CameraX", "Use case binding failed", e)
                            }

                        }, ContextCompat.getMainExecutor(ctx))

                        // on affiche la caméra via cette variable
                        previewView
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                ScanState.Simulated -> {
                    val code = "3274080005003"
                    // on récupère le produit et on navigue si réussi
                    scanViewModel.fetchProduct(code) {
                        context.startActivity(Intent(context, HistoryActivity::class.java))
                        isProcessing = false
                    }
                    Log.d("ScanScreen", "Code: $scannedCode")
                    Text("Simulation de scan")
                }
            }

            if (product != null && productFetched){
                Row(modifier = Modifier.padding(8.dp)) {
                    AsyncImage(
                        model = product?.imageUrl,
                        contentDescription = product?.name,
                        modifier = Modifier.size(64.dp)
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(product?.name ?: "Nom inconnu", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (scannedCode == null){
                Text(
                    text = "Aucun code détecté",
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (productFetched && product != null) {
                // bouton de reinitalisation
                Button(
                    onClick = {
                        scannedCode = null
                        productFetched = false
                        isProcessing = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Reset")
                }
                // bouton de validation
                Button(
                    onClick = {
                        context.startActivity(Intent(context, HistoryActivity::class.java))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Validate")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun Builder() {
    TODO("Not yet implemented")
}

private class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { onBarcodeDetected(it) }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("BarcodeAnalyzer", "Erreur de scan", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

@ExperimentalGetImage
@Composable
@Preview(showBackground = true)
fun ScanScreenPreview() {
    ScannerTheme { ScanScreen() }
}
