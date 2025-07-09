package com.buggy.natura.utils

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

object CameraUtils {
    private var camera: Camera? = null

    fun setupCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        flashEnabled: Boolean,
        onCameraReady: () -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )

                // Set flash mode
                camera?.cameraControl?.enableTorch(flashEnabled)

                onCameraReady()

            } catch (exc: Exception) {
                // Handle camera setup error
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(previewView.context))
    }

    fun updateFlash(previewView: PreviewView, flashEnabled: Boolean) {
        camera?.cameraControl?.enableTorch(flashEnabled)
    }
}