package com.buggy.natura.data.models

/**
 * Represents a single classification result from our AI model
 * @param label The name of the identified plant/object
 * @param confidence Score between 0.0 and 1.0 (how sure the AI is)
 * @param category Type of classification (Plant, Flower, Tree, Object, etc.)
 * @param description Brief description or care tips
 */
data class ClassificationResult(
    val label: String,
    val confidence: Float,
    val category: String = "Unknown",
    val description: String = ""
) {
    /**
     * Helper function to get confidence as percentage
     */
    fun getConfidencePercentage(): Int = (confidence * 100).toInt()

    /**
     * Helper function to determine if this is a high-confidence result
     */
    fun isHighConfidence(): Boolean = confidence >= 0.7f

    /**
     * Helper function to determine if this is a medium-confidence result
     */
    fun isMediumConfidence(): Boolean = confidence >= 0.4f && confidence < 0.7f

    /**
     * Helper function to determine if this is a low-confidence result
     */
    fun isLowConfidence(): Boolean = confidence < 0.4f
}
