package com.joviansapps.ganymede.ui.components

import java.text.DecimalFormat

/**
 * Format a nullable Double with a DecimalFormat pattern and return an empty string for null.
 * Keep this pure and safe for use in Composables and tests.
 */
fun formatDouble(value: Double?, pattern: String = "#.##"): String {
    if (value == null) return ""
    val formatter = DecimalFormat(pattern)
    return formatter.format(value)
}

