package com.buggy.natura.ui.screens

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.buggy.natura.utils.CameraUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen() {
    var flashEnabled by remember { mutableStateOf(false) }
    var isCameraReady by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("NatureLens") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        // Camera Preview Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Camera Preview
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        CameraUtils.setupCamera(
                            previewView = this,
                            lifecycleOwner = lifecycleOwner,
                            flashEnabled = flashEnabled
                        ) {
                            isCameraReady = true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    // Update camera when flash setting changes
                    if (isCameraReady) {
                        CameraUtils.updateFlash(previewView, flashEnabled)
                    }
                }
            )

            // Camera Status Overlay
            if (!isCameraReady) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Starting camera...")
                    }
                }
            }

            // Camera Controls
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Flash Toggle
                FloatingActionButton(
                    onClick = { flashEnabled = !flashEnabled },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = if (flashEnabled) "Turn off flash" else "Turn on flash"
                    )
                }

                // Capture Button (for future use)
                FloatingActionButton(
                    onClick = {
                        // TODO: Implement capture functionality in next phase
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Capture"
                    )
                }
            }
        }

        // Placeholder for Results Panel (Phase 2)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🌱",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Plant identification coming in Phase 2!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}