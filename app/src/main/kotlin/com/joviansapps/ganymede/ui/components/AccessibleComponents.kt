package com.joviansapps.ganymede.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Bouton de calculatrice accessible avec retour haptique
 */
@Composable
fun AccessibleCalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String = text,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    val haptic = LocalHapticFeedback.current

    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier.semantics {
            contentDescription = "Bouton $description"
        },
        enabled = enabled,
        colors = colors
    ) {
        Text(text)
    }
}

/**
 * Snackbar accessible pour les erreurs
 */
@Composable
fun AccessibleErrorSnackbar(
    errorMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Snackbar(
        action = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.semantics {
                    contentDescription = "Fermer le message d'erreur"
                }
            ) {
                Text("OK")
            }
        },
        modifier = modifier.semantics {
            contentDescription = "Erreur: $errorMessage"
        }
    ) {
        Text(errorMessage)
    }
}

/**
 * Slider accessible pour les paramètres de graphique
 */
@Composable
fun AccessibleSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    label: String,
    modifier: Modifier = Modifier,
    steps: Int = 0
) {
    Column(modifier = modifier) {
        Text(
            text = "$label: ${String.format("%.2f", value)}",
            style = MaterialTheme.typography.labelMedium
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "$label, valeur actuelle: ${String.format("%.2f", value)}"
                }
        )
    }
}
