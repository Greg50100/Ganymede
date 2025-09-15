package com.joviansapps.ganymede.ui.screens.utilities.math

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun QuadraticEquationSolverScreen() {
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    var c by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(R.string.quadratic_equation_formula))
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = a,
            onValueChange = { a = it },
            label = { Text(stringResource(R.string.coefficient_a)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = b,
            onValueChange = { b = it },
            label = { Text(stringResource(R.string.coefficient_b)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = c,
            onValueChange = { c = it },
            label = { Text(stringResource(R.string.coefficient_c)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val valA = a.toDoubleOrNull()
                val valB = b.toDoubleOrNull()
                val valC = c.toDoubleOrNull()

                if (valA != null && valB != null && valC != null) {
                    if (valA == 0.0) {
                        result = "a cannot be zero."
                        return@Button
                    }

                    val discriminant = valB.pow(2) - 4 * valA * valC
                    result = "Discriminant (Δ) = $discriminant\n"

                    when {
                        discriminant > 0 -> {
                            val root1 = (-valB + sqrt(discriminant)) / (2 * valA)
                            val root2 = (-valB - sqrt(discriminant)) / (2 * valA)
                            result += "Two real roots:\nx1 = $root1\nx2 = $root2"
                        }
                        discriminant == 0.0 -> {
                            val root = -valB / (2 * valA)
                            result += "One real root:\nx = $root"
                        }
                        else -> {
                            val realPart = -valB / (2 * valA)
                            val imaginaryPart = sqrt(-discriminant) / (2 * valA)
                            result += "Two complex roots:\nx1 = $realPart + ${imaginaryPart}i\nx2 = $realPart - ${imaginaryPart}i"
                        }
                    }
                } else {
                    result = "Please enter valid numbers for a, b, and c."
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.calculate))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(result)
    }
}
