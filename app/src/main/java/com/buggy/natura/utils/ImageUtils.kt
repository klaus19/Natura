package com.buggy.natura.utils

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Utility functions for image processing
 * These will be used by our AI classifier
 */
object ImageUtils {

    /**
     * Resize bitmap to specified dimensions
     * @param bitmap Original bitmap
     * @param width Target width
     * @param height Target height
     * @return Resized bitmap
     */
    fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    /**
     * Rotate bitmap by specified degrees
     * @param bitmap Original bitmap
     * @param degrees Rotation angle
     * @return Rotated bitmap
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(degrees)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Crop bitmap to center square
     * Useful for preparing images for square AI models
     * @param bitmap Original bitmap
     * @return Center-cropped square bitmap
     */
    fun cropToSquare(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    /**
     * Prepare bitmap for AI model input
     * Standard preprocessing: resize to 224x224, center crop
     * @param bitmap Original bitmap
     * @param targetSize Target size (default 224 for most models)
     * @return Preprocessed bitmap ready for AI
     */
    fun preprocessForAI(bitmap: Bitmap, targetSize: Int = 224): Bitmap {
        // First crop to square to maintain aspect ratio
        val square = cropToSquare(bitmap)
        // Then resize to model input size
        return resizeBitmap(square, targetSize, targetSize)
    }
}