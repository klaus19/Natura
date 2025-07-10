package com.buggy.natura.utils

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object CameraUtils {
    private var camera: Camera? = null
    private var imageAnalysis: ImageAnalysis? = null

    /**
     * Setup camera with image analysis for AI processing
     */
    fun setupCameraWithAnalysis(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        flashEnabled: Boolean,
        onCameraReady: () -> Unit,
        onImageAnalyzed: (Bitmap) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                // Preview use case
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                // Image analysis use case for AI
                imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(
                            ContextCompat.getMainExecutor(previewView.context)
                        ) { imageProxy ->
                            processImageProxy(imageProxy, onImageAnalyzed)
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

                // Set flash mode
                camera?.cameraControl?.enableTorch(flashEnabled)

                onCameraReady()

            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(previewView.context))
    }

    /**
     * Legacy setup method (for compatibility)
     */
    fun setupCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        flashEnabled: Boolean,
        onCameraReady: () -> Unit
    ) {
        setupCameraWithAnalysis(
            previewView = previewView,
            lifecycleOwner = lifecycleOwner,
            flashEnabled = flashEnabled,
            onCameraReady = onCameraReady,
            onImageAnalyzed = { /* No analysis */ }
        )
    }

    /**
     * Process ImageProxy and convert to Bitmap for AI analysis
     */
    private fun processImageProxy(
        imageProxy: ImageProxy,
        onImageAnalyzed: (Bitmap) -> Unit
    ) {
        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            if (bitmap != null) {
                onImageAnalyzed(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            imageProxy.close()
        }
    }

    /**
     * Convert ImageProxy to Bitmap
     * Handles YUV to RGB conversion properly
     */
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        return try {
            val yBuffer = imageProxy.planes[0].buffer // Y
            val vuBuffer = imageProxy.planes[2].buffer // VU

            val ySize = yBuffer.remaining()
            val vuSize = vuBuffer.remaining()

            val nv21 = ByteArray(ySize + vuSize)

            yBuffer.get(nv21, 0, ySize)
            vuBuffer.get(nv21, ySize, vuSize)

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
            val imageBytes = out.toByteArray()

            android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Update flash setting
     */
    fun updateFlash(previewView: PreviewView, flashEnabled: Boolean) {
        camera?.cameraControl?.enableTorch(flashEnabled)
    }
}
