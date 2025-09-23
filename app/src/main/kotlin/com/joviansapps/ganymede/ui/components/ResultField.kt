package com.joviansapps.ganymede.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A reusable Composable to display a labeled result area, implemented as a
 * read-only OutlinedTextField for a consistent UI with input fields.
 * It uses theme colors to distinguish itself as an output field.
 *
 * @param label The text label for the result, displayed above the field.
 * @param value The text value of the result.
 * @param modifier Optional modifier to apply to the OutlinedTextField.
 * @param unit Optional unit to append to the value.
 * @param maxLines Maximum number of lines for the value text.
 */
@Composable
fun ResultField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    maxLines: Int = 5
) {
    val displayValue = if (unit != null) "$value $unit" else value

    OutlinedTextField(
        value = displayValue,
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        label = { Text(label) },
        readOnly = true,
        maxLines = maxLines,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ResultFieldPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ResultField Preview", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            ResultField(
                label = "Output Voltage (V)",
                value = "4.95"
            )
            Spacer(modifier = Modifier.height(16.dp))
            ResultField(
                label = "Estimated Battery Life",
                value = "13",
                unit = "hours"
            )
            Spacer(modifier = Modifier.height(16.dp))
            ResultField(
                label = "Multi-line result",
                value = "This is an example of a result that spans multiple lines to show how the field adapts.",
                maxLines = 3
            )
        }
    }
}
