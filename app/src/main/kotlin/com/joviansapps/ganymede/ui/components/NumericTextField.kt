package com.joviansapps.ganymede.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * Champ texte spécialisé pour les entrées numériques avec options par défaut.
 * @param value Valeur actuelle sous forme de String
 * @param onValueChange Callback au changement
 * @param label Texte du label
 * @param modifier Modifier optionnel
 * @param unit Unité affichée en trailing icon (optionnel)
 * @param readOnly Indique si le champ est en lecture seule
 * @param enabled Indique si le champ est activé
 */
@Composable
fun NumericTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            unit?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
        },
        singleLine = singleLine,
        readOnly = readOnly,
        enabled = enabled
    )
}
