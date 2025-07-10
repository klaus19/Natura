package com.buggy.natura.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Visual confidence indicator bar
 */
@Composable
fun ConfidenceBar(
    confidence: Float,
    modifier: Modifier = Modifier
) {
    val animatedConfidence by animateFloatAsState(
        targetValue = confidence,
        animationSpec = tween(durationMillis = 500),
        label = "confidence_animation"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedConfidence)
                .clip(RoundedCornerShape(2.dp))
                .background(getConfidenceBarColor(confidence))
        )
    }
}

/**
 * Get confidence bar color based on confidence level
 */
@Composable
private fun getConfidenceBarColor(confidence: Float): Color {
    return when {
        confidence >= 0.7f -> Color(0xFF4CAF50) // Green
        confidence >= 0.4f -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
}
