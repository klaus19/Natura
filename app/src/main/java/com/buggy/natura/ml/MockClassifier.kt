package com.buggy.natura.ml

import com.buggy.natura.data.models.ClassificationResult
import com.buggy.natura.data.models.PlantCategory

/**
 * Mock classifier for testing the UI without a real AI model
 * This helps us develop and test the app interface
 */
object MockClassifier {

    private val mockPlants = listOf(
        "Rose", "Sunflower", "Tulip", "Daisy", "Orchid", "Lily", "Carnation",
        "Oak Tree", "Pine Tree", "Maple Tree", "Birch Tree", "Cherry Tree",
        "Cactus", "Aloe Vera", "Jade Plant", "Snake Plant", "Rubber Plant",
        "Basil", "Mint", "Rosemary", "Thyme", "Parsley", "Cilantro",
        "Tomato", "Pepper", "Lettuce", "Spinach", "Carrot", "Potato"
    )

    private val mockObjects = listOf(
        "Smartphone", "Laptop", "Coffee Cup", "Book", "Chair", "Table",
        "Pen", "Notebook", "Glasses", "Watch", "Key", "Coin"
    )

    /**
     * Generate random but realistic classification results
     */
    fun generateRandomResults(): List<ClassificationResult> {
        val isPlant = Math.random() > 0.3 // 70% chance of plant, 30% chance of object
        val sourceList = if (isPlant) mockPlants else mockObjects

        // Generate 3-5 random results
        val resultCount = (3..5).random()
        val selectedItems = sourceList.shuffled().take(resultCount)

        return selectedItems.mapIndexed { index, item ->
            val confidence = when (index) {
                0 -> 0.6f + (Math.random() * 0.35f).toFloat() // Primary: 60-95%
                1 -> 0.1f + (Math.random() * 0.3f).toFloat()  // Secondary: 10-40%
                else -> 0.01f + (Math.random() * 0.15f).toFloat() // Others: 1-16%
            }

            ClassificationResult(
                label = item,
                confidence = confidence,
                category = if (isPlant) getPlantCategory(item) else "Object",
                description = getDescription(item)
            )
        }.sortedByDescending { it.confidence }
    }

    private fun getPlantCategory(plantName: String): String {
        return when {
            plantName.contains("Tree", ignoreCase = true) -> PlantCategory.TREE.displayName
            plantName in listOf("Cactus", "Aloe Vera", "Jade Plant") -> PlantCategory.SUCCULENT.displayName
            plantName in listOf("Basil", "Mint", "Rosemary", "Thyme", "Parsley", "Cilantro") -> PlantCategory.HERB.displayName
            plantName in listOf("Tomato", "Pepper", "Lettuce", "Spinach", "Carrot", "Potato") -> PlantCategory.VEGETABLE.displayName
            else -> PlantCategory.FLOWER.displayName
        }
    }

    private fun getDescription(item: String): String {
        return when (item.lowercase()) {
            "rose" -> "A woody perennial flowering plant known for its beauty and fragrance"
            "sunflower" -> "A large flower head that follows the sun throughout the day"
            "oak tree" -> "A deciduous tree known for its strength and longevity"
            "cactus" -> "A drought-resistant succulent plant adapted to arid environments"
            "basil" -> "An aromatic herb commonly used in cooking"
            "smartphone" -> "A mobile communication device"
            "laptop" -> "A portable computer"
            else -> "Identified ${if (item in mockPlants) "plant" else "object"}"
        }
    }
}