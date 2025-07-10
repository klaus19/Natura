package com.buggy.natura.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buggy.natura.data.models.ClassificationResult
import com.buggy.natura.ml.ImageClassifier
import com.buggy.natura.utils.AnalysisThrottler
import kotlinx.coroutines.launch


/**
 * ViewModel for Camera Screen - Updated with real-time analysis
 */
class CameraViewModel : ViewModel() {

    private val _uiState = mutableStateOf(CameraUiState())
    val uiState: State<CameraUiState> = _uiState

    private var imageClassifier: ImageClassifier? = null
    private var analysisThrottler: AnalysisThrottler? = null

    /**
     * Initialize the AI classifier and analysis throttler
     */
    fun initializeClassifier(context: Context) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isInitializing = true)

                imageClassifier = ImageClassifier(context)
                val initialized = imageClassifier?.initialize() ?: false

                if (initialized) {
                    // Setup throttler for real-time analysis
                    analysisThrottler = AnalysisThrottler(
                        intervalMs = 3000L // Analyze every 3 seconds
                    ) { bitmap ->
                        classifyImageInternal(bitmap)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isInitializing = false,
                    isClassifierReady = initialized,
                    isRealTimeEnabled = initialized
                )

                Log.d("CameraViewModel", "Classifier initialized: $initialized")

            } catch (e: Exception) {
                Log.e("CameraViewModel", "Failed to initialize classifier", e)
                _uiState.value = _uiState.value.copy(
                    isInitializing = false,
                    isClassifierReady = false,
                    errorMessage = "Failed to initialize AI: ${e.message}"
                )
            }
        }
    }

    /**
     * Process camera frame (called automatically from camera)
     */
    fun processCameraFrame(bitmap: Bitmap) {
        if (!_uiState.value.isRealTimeEnabled || !_uiState.value.isClassifierReady) {
            return
        }

        // Submit to throttler for processing
        analysisThrottler?.submitForAnalysis(bitmap)
    }

    /**
     * Manual classification (for test button or capture button)
     */
    fun classifyImage(bitmap: Bitmap) {
        viewModelScope.launch {
            classifyImageInternal(bitmap)
        }
    }

    /**
     * Internal classification method
     */
    private suspend fun classifyImageInternal(bitmap: Bitmap) {
        try {
            _uiState.value = _uiState.value.copy(isAnalyzing = true)

            val results = imageClassifier?.classify(bitmap) ?: emptyList()

            _uiState.value = _uiState.value.copy(
                isAnalyzing = false,
                classificationResults = results,
                lastAnalysisTime = System.currentTimeMillis()
            )

            Log.d("CameraViewModel", "Classification completed: ${results.size} results")

        } catch (e: Exception) {
            Log.e("CameraViewModel", "Classification failed", e)
            _uiState.value = _uiState.value.copy(
                isAnalyzing = false,
                errorMessage = "Classification failed: ${e.message}"
            )
        }
    }

    /**
     * Toggle real-time analysis on/off
     */
    fun toggleRealTimeAnalysis() {
        val newState = !_uiState.value.isRealTimeEnabled
        _uiState.value = _uiState.value.copy(isRealTimeEnabled = newState)

        if (!newState) {
            analysisThrottler?.pause()
        }

        Log.d("CameraViewModel", "Real-time analysis: $newState")
    }

    /**
     * Force immediate analysis (for capture button)
     */
    fun captureAndAnalyze() {
        analysisThrottler?.forceAnalysis()
    }

    /**
     * Toggle flash on/off
     */
    fun toggleFlash() {
        _uiState.value = _uiState.value.copy(
            isFlashEnabled = !_uiState.value.isFlashEnabled
        )
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Manual test of classifier (for the test button)
     */
    fun runManualTest(context: Context) {
        viewModelScope.launch {
            try {
                val testBitmap = createTestBitmap()
                classifyImageInternal(testBitmap)
            } catch (e: Exception) {
                Log.e("CameraViewModel", "Manual test failed", e)
            }
        }
    }

    private fun createTestBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.GREEN)
        return bitmap
    }

    override fun onCleared() {
        super.onCleared()
        analysisThrottler?.cleanup()
        imageClassifier?.close()
        Log.d("CameraViewModel", "ViewModel cleared")
    }
}

/**
 * UI State for Camera Screen - Updated with real-time features
 */
data class CameraUiState(
    val isInitializing: Boolean = false,
    val isClassifierReady: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isFlashEnabled: Boolean = false,
    val isRealTimeEnabled: Boolean = true, // NEW: Real-time toggle
    val classificationResults: List<ClassificationResult> = emptyList(),
    val lastAnalysisTime: Long = 0L,
    val errorMessage: String? = null
)