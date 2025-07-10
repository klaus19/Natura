package com.buggy.natura.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.buggy.natura.data.models.ClassificationResult


/**
 * Individual result item showing plant/object name and confidence
 */
@Composable
fun ResultItem(
    result: ClassificationResult,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Name and category
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = result.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (result.category.isNotEmpty()) {
                Text(
                    text = result.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right side: Confidence with visual indicator
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConfidenceBar(
                confidence = result.confidence,
                modifier = Modifier.width(40.dp).height(4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${result.getConfidencePercentage()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = getConfidenceColor(result.confidence)
            )
        }
    }
}

/**
 * Get color based on confidence level
 */
@Composable
private fun getConfidenceColor(confidence: Float): Color {
    return when {
        confidence >= 0.7f -> Color(0xFF4CAF50) // Green for high confidence
        confidence >= 0.4f -> Color(0xFFFF9800) // Orange for medium confidence
        else -> Color(0xFFF44336) // Red for low confidence
    }
}
