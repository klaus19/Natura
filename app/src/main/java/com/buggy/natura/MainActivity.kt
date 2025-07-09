package com.buggy.natura

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.buggy.natura.ml.ImageClassifier
import com.buggy.natura.ui.screens.CameraScreen
import com.buggy.natura.ui.theme.NatureLensTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

     private val requestPermissionLauncher = registerForActivityResult(
         ActivityResultContracts.RequestPermission()
     ){ isGranted ->
         if (isGranted){
             Toast.makeText(this,"Camera Prmission granted", Toast.LENGTH_SHORT).show()
         }else{
             Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
             finish()
         }
     }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check camera permission
        checkCameraPermission()

        // 🧪 TEST THE AI CLASSIFIER HERE
        testImageClassifier()
        setContent {
            NatureLensTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraScreen()
                }
            }
        }
    }

    private fun testImageClassifier() {
        Log.d("NatureLens", "🧪 Testing ImageClassifier...")

        lifecycleScope.launch {
            try {
                // Create classifier
                val classifier = ImageClassifier(this@MainActivity)

                // Initialize it
                Log.d("NatureLens", "Initializing classifier...")
                val initialized = classifier.initialize()

                if (initialized) {
                    Log.d("NatureLens", "✅ Classifier initialized successfully!")

                    // Create a simple mock bitmap for testing
                    val mockBitmap = createMockBitmap()

                    // Run classification
                    Log.d("NatureLens", "Running classification...")
                    val results = classifier.classify(mockBitmap)

                    // Print results
                    Log.d("NatureLens", "🎯 Classification Results:")
                    results.forEachIndexed { index, result ->
                        Log.d("NatureLens", "${index + 1}. ${result.label} - ${result.getConfidencePercentage()}% (${result.category})")
                        Log.d("NatureLens", "   Description: ${result.description}")
                    }

                    // Clean up
                    classifier.close()
                    Log.d("NatureLens", "✅ Test completed successfully!")

                } else {
                    Log.e("NatureLens", "❌ Failed to initialize classifier")
                }

            } catch (e: Exception) {
                Log.e("NatureLens", "❌ Error testing classifier: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Create a simple colored bitmap for testing
     * In real use, this will be camera frames
     */
    private fun createMockBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.GREEN) // Fill with green color (like a plant!)
        return bitmap
    }

    private fun checkCameraPermission() {
        when{
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED ->{

            }
            else ->{
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NatureLensTheme {
         CameraScreen()
    }
}