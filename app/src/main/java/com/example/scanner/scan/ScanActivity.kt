package com.example.scanner.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.ExperimentalGetImage
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scanner.data.remote.ProductApi
import com.example.scanner.data.remote.ProductRepository
import com.example.scanner.ui.theme.ScannerTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : ComponentActivity() {

    private val cameraExecutor = Executors.newSingleThreadExecutor()



    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val simulated = intent.getBooleanExtra("simulated", false)

        enableEdgeToEdge()

        // Crée Retrofit et ProductApi
        val retrofit = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/") // Remplace par ton endpoint si nécessaire
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val productApi = retrofit.create(ProductApi::class.java)

        // Crée le repository
        val repository = ProductRepository(productApi)

        // Crée le ViewModel via ViewModelProvider pour respecter le cycle de vie
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ScanViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            ScannerTheme {
                val scanViewModel: ScanViewModel = viewModel(factory = viewModelFactory)
                ScanScreen(scanViewModel = scanViewModel)
            }
        }

        // Vérifie les permissions
        if (!hasAllPermissions() && !simulated) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (requestCode == REQUEST_CODE_PERMISSIONS && !hasAllPermissions()) {
            Toast.makeText(this, "Permission pour la camera necessaire", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun hasAllPermissions() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }.toTypedArray()
    }
}
