package com.joviansapps.ganymede.data

/**
 * Represents the entire state of the calculator screen in a single, immutable object.
 * This makes state management more predictable and easier to debug.
 */
data class CalculatorState(
    val expression: String = "",
    val result: String = "0",
    val history: List<String> = emptyList(),
    val isDegrees: Boolean = false,
    val hasMemory: Boolean = false,
    val justEvaluated: Boolean = false
)
