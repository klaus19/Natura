package com.buggy.natura.utils

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.*

/**
 * Throttles image analysis to prevent overwhelming the AI
 * Ensures smooth performance and battery optimization
 */
class AnalysisThrottler(
    private val intervalMs: Long = 2000L, // Analyze every 2 seconds
    private val onAnalyze: suspend (Bitmap) -> Unit
) {
    private var isProcessing = false
    private var lastAnalysisTime = 0L
    private var pendingBitmap: Bitmap? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Submit a bitmap for analysis (will be throttled)
     */
    fun submitForAnalysis(bitmap: Bitmap) {
        val currentTime = System.currentTimeMillis()

        // Always keep the latest bitmap
        pendingBitmap?.recycle() // Clean up previous bitmap
        pendingBitmap = bitmap.copy(bitmap.config?: Bitmap.Config.ARGB_8888, false)

        // Check if we should process now
        if (!isProcessing && (currentTime - lastAnalysisTime) >= intervalMs) {
            processNow()
        }
    }

    /**
     * Process the pending bitmap immediately
     */
    private fun processNow() {
        val bitmapToProcess = pendingBitmap ?: return
        pendingBitmap = null

        if (isProcessing) return

        scope.launch {
            try {
                isProcessing = true
                lastAnalysisTime = System.currentTimeMillis()

                Log.d("AnalysisThrottler", "Starting analysis...")
                onAnalyze(bitmapToProcess)
                Log.d("AnalysisThrottler", "Analysis completed")

            } catch (e: Exception) {
                Log.e("AnalysisThrottler", "Analysis failed", e)
            } finally {
                isProcessing = false
                bitmapToProcess.recycle() // Clean up

                // If there's a pending bitmap and enough time has passed, process it
                delay(100) // Small delay to prevent rapid firing
                if (pendingBitmap != null &&
                    (System.currentTimeMillis() - lastAnalysisTime) >= intervalMs) {
                    processNow()
                }
            }
        }
    }

    /**
     * Force process the current pending bitmap (for manual capture)
     */
    fun forceAnalysis() {
        if (!isProcessing && pendingBitmap != null) {
            processNow()
        }
    }

    /**
     * Pause analysis
     */
    fun pause() {
        pendingBitmap?.recycle()
        pendingBitmap = null
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        scope.cancel()
        pendingBitmap?.recycle()
        pendingBitmap = null
    }
}