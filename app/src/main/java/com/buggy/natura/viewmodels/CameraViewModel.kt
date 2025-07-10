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
import kotlinx.coroutines.launch

/**
 * ViewModel for Camera Screen
 * Manages AI classification state and camera interactions
 */
class CameraViewModel : ViewModel() {

    private val _uiState = mutableStateOf(CameraUiState())
    val uiState: State<CameraUiState> = _uiState

    private var imageClassifier: ImageClassifier? = null
    private var isProcessing = false

    /**
     * Initialize the AI classifier
     */
    fun initializeClassifier(context: Context) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isInitializing = true)

                imageClassifier = ImageClassifier(context)
                val initialized = imageClassifier?.initialize() ?: false

                _uiState.value = _uiState.value.copy(
                    isInitializing = false,
                    isClassifierReady = initialized
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
     * Process an image for classification
     */
    fun classifyImage(bitmap: Bitmap) {
        if (isProcessing || imageClassifier == null || !_uiState.value.isClassifierReady) {
            return
        }

        viewModelScope.launch {
            try {
                isProcessing = true
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
            } finally {
                isProcessing = false
            }
        }
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
                // Create a test bitmap
                val testBitmap = createTestBitmap()
                classifyImage(testBitmap)
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
        imageClassifier?.close()
        Log.d("CameraViewModel", "ViewModel cleared")
    }
}

/**
 * UI State for Camera Screen
 */
data class CameraUiState(
    val isInitializing: Boolean = false,
    val isClassifierReady: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isFlashEnabled: Boolean = false,
    val classificationResults: List<ClassificationResult> = emptyList(),
    val lastAnalysisTime: Long = 0L,
    val errorMessage: String? = null
)