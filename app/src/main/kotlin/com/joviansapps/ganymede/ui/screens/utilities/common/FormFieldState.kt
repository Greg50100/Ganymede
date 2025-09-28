package com.joviansapps.ganymede.ui.screens.utilities.common

/**
 * Represents the state of a single input field in a form.
 *
 * @property value The current text value of the field.
 * @property unit An optional unit to be displayed (e.g., "V", "A", "Ω").
 * @property isError Indicates if the current value is invalid.
 * @property errorMessage The error message to display when [isError] is true.
 */
data class FormFieldState(
    val value: String = "",
    val unit: String? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null
)
