package com.example.scanner.scan

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scanner.ui.theme.ScannerTheme
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.camera.core.Preview as CameraPreview


@ExperimentalGetImage
@Composable
fun ScanScreen(vm: ScanViewModel = viewModel()) {
    val context = LocalContext.current


    val scanState by vm.scanStateFlow.collectAsState()

    // récupère la variable Simulated depuis l'activity
    val simulated = (context as? ComponentActivity)
        ?.intent
        ?.getBooleanExtra("simulated", false) ?: false

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var scannedCode by remember { mutableStateOf<String?>(null) }

    // on update l'état de simulation
    vm.isSimulated(simulated)
    Log.i("simulated",simulated.toString())

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when(val s = scanState) {
                // Etat initial, donc on mets un chargement
                ScanState.Initial -> CircularProgressIndicator()
                // Etat "normal" donc on affiche la caméra
                ScanState.Normal -> AndroidView(
                    factory = { ctx ->
                        // on affiche la caméra dans une Preview View
                        val previewView = PreviewView(ctx)
                        val executor = Executors.newSingleThreadExecutor()

                        // on gère le scan de bar-code via la caméra
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = CameraPreview.Builder().build().apply {
                                setSurfaceProvider(previewView.surfaceProvider)
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
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                // Etat "simulé" donc on mets en dur le code "scanné en mode simulation"
                ScanState.Simulated -> {
                    scannedCode = "3274080005003"
                    Log.d("ScanScreen", "Code: $scannedCode")
                    Text("Simulation de scan")
                }
            }



            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = scannedCode ?: "Aucun code détecté",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@ExperimentalGetImage
private fun processImage(imageProxy: ImageProxy, onResult: (String) -> Unit) {
    val mediaImage = imageProxy.image ?: return imageProxy.close()
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    // On utilise un module BarcodeScanning, qui regarde via l'image de la caméra les numéros du barcode
    BarcodeScanning.getClient()
        .process(image)
        .addOnSuccessListener { barcodes ->
            barcodes.firstOrNull()?.rawValue?.let(onResult)
        }
        .addOnFailureListener { e ->
            Log.e("BarcodeScan", "Erreur : ${e.message}")
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

@ExperimentalGetImage
@Composable
@Preview(showBackground = true)
fun ScanScreenPreview() {
    ScannerTheme { ScanScreen() }
}
