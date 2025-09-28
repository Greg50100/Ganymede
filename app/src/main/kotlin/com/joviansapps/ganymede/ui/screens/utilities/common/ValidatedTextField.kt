package com.joviansapps.ganymede.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.joviansapps.ganymede.ui.screens.utilities.common.FormFieldState

/**
 * A reusable OutlinedTextField that integrates with [FormFieldState] to display
 * validation errors and units.
 *
 * @param state The [FormFieldState] holding the value and validation status.
 * @param onValueChange The callback that is triggered when the value of the text field changes.
 * @param label The label to be displayed inside the text field.
 * @param modifier The [Modifier] to be applied to this text field.
 */
@Composable
fun ValidatedTextField(
    state: FormFieldState,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = state.value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        isError = state.isError,
        supportingText = {
            if (state.isError) {
                Text(
                    text = state.errorMessage ?: "Invalid input",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        trailingIcon = {
            state.unit?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}
