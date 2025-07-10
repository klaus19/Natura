package com.buggy.natura.ui.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buggy.natura.ui.components.ResultsPanel
import com.buggy.natura.utils.CameraUtils
import com.buggy.natura.viewmodels.CameraViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState

    // Initialize classifier when screen starts
    LaunchedEffect(Unit) {
        viewModel.initializeClassifier(context)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("NatureLens")
                    if (uiState.isAnalyzing) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    if (uiState.isRealTimeEnabled && uiState.isClassifierReady) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔴 LIVE",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
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
            // Camera Preview with Real-time Analysis
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        CameraUtils.setupCameraWithAnalysis(
                            previewView = this,
                            lifecycleOwner = lifecycleOwner,
                            flashEnabled = uiState.isFlashEnabled,
                            onCameraReady = {
                                // Camera is ready
                            },
                            onImageAnalyzed = { bitmap ->
                                // This is called automatically for each frame!
                                viewModel.processCameraFrame(bitmap)
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    CameraUtils.updateFlash(previewView, uiState.isFlashEnabled)
                }
            )

            // Error Message
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
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
                    onClick = { viewModel.toggleFlash() },
                    containerColor = if (uiState.isFlashEnabled) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isFlashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = if (uiState.isFlashEnabled) "Turn off flash" else "Turn on flash"
                    )
                }

                // Capture & Analyze Button
                FloatingActionButton(
                    onClick = {
                        viewModel.captureAndAnalyze()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Capture and analyze now"
                    )
                }
            }
        }

        // Results Panel with Real-time Controls
        ResultsPanel(
            results = uiState.classificationResults,
            isAnalyzing = uiState.isAnalyzing,
            isClassifierReady = uiState.isClassifierReady,
            isRealTimeEnabled = uiState.isRealTimeEnabled,
            onTestClick = { viewModel.runManualTest(context) },
            onToggleRealTime = { viewModel.toggleRealTimeAnalysis() },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}