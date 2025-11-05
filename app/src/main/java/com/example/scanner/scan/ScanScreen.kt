package com.example.scanner.scan

import HistoryViewModel
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
import coil.request.ImageRequest
import coil.size.Scale
import com.example.scanner.ui.theme.ScannerTheme
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.camera.core.Preview as CameraPreview
import com.example.scanner.history.HistoryActivity

@ExperimentalGetImage
@Composable
fun ScanScreen(
    scanViewModel: ScanViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel()
) {
    val products by scanViewModel.products.collectAsState()
    var scannedCode by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val context = LocalContext.current


    val scanState by vm.scanStateFlow.collectAsState()

    // récupère la variable Simulated depuis l'activity
    val simulated = (context as? ComponentActivity)
        ?.intent
        ?.getBooleanExtra("simulated", false) ?: false

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Scaffold { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // Camera Preview
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraExecutor = Executors.newSingleThreadExecutor()

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = CameraPreview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val analyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { code ->
                                    if (!isProcessing) { // ne fait quelque chose que si pas déjà en traitement
                                        isProcessing = true
                                        scannedCode = code
                                        scanViewModel.fetchProduct(code) { productData ->
                                            val product = productData
                                            // Ajouter dans l'historique
                                            historyViewModel.addProduct(product)
                                            // Lancer HistoryActivity
                                            context.startActivity(
                                                Intent(context, HistoryActivity::class.java)
                                            )
                                        }
                                    }
                                })
                            }

                            val analyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build().apply {
                                    setAnalyzer(executor) { imageProxy ->
                                        processImage(imageProxy) { code ->
                                            scannedCode = code
                                            Log.d("ScanScreen", "Code: $code")
                                        }
                                    }
                                }

                            // Fin du scan, donc on retire la gestion de la caméra
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

                    previewView
                },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = scannedCode ?: "Aucun code détecté",
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn {
                items(products) { product ->
                    Row(modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.size(64.dp)
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text(product.brand)
                            Text(product.quantity)
                        }
                    }
                }
            }
        }
    }
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
