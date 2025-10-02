// app/src/main/kotlin/com/joviansapps/ganymede/ui/components/Keypad.kt
package com.joviansapps.ganymede.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState       // si tu collectes un flow ici
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.data.CalculatorAction


private val portraitKeys = listOf(
    listOf("7","8","9","/"),
    listOf("4","5","6","*"),
    listOf("1","2","3","-"),
    listOf("0",".","⌫","+"),
    listOf("C","=")
)

@Composable
fun Keypad(onEvent: (CalculatorAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        portraitKeys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { label ->
                    KeypadButton(label) {
                        when (label) {
                            in "0".."9" -> onEvent(CalculatorAction.Number(label))
                            "."         -> onEvent(CalculatorAction.Decimal)
                            "+" , "-", "*", "/" ->
                                onEvent(CalculatorAction.Operator(label))
                            "⌫"         -> onEvent(CalculatorAction.Delete)
                            "C"         -> onEvent(CalculatorAction.DeleteAll)
                            "="         -> onEvent(CalculatorAction.Evaluate)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.KeypadButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .weight(if (label == "0") 2f else 1f)
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, style = MaterialTheme.typography.titleLarge)
        }
    }
}