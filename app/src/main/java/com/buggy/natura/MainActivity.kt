package com.buggy.natura

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.buggy.natura.ui.screens.CameraScreen
import com.buggy.natura.ui.theme.NatureLensTheme

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