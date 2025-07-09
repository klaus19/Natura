package com.buggy.natura.data.models

/**
 * Detailed information about a plant
 * This will be expanded in later phases with database integration
 */
data class PlantInfo(
    val name: String,
    val scientificName: String = "",
    val category: PlantCategory,
    val careInstructions: CareInstructions,
    val description: String = "",
    val imageUrl: String = "",
    val tags: List<String> = emptyList()
)

enum class PlantCategory(val displayName: String) {
    FLOWER("Flower"),
    TREE("Tree"),
    SHRUB("Shrub"),
    SUCCULENT("Succulent"),
    HERB("Herb"),
    VEGETABLE("Vegetable"),
    FRUIT("Fruit"),
    FERN("Fern"),
    GRASS("Grass"),
    UNKNOWN("Unknown")
}
/**
 * Care instructions for plants
 */
data class CareInstructions(
    val wateringFrequency: String = "Unknown",
    val sunlightRequirement: String = "Unknown",
    val soilType: String = "Unknown",
    val temperature: String = "Unknown",
    val humidity: String = "Unknown",
    val fertilizer: String = "Unknown"
)