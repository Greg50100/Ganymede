package com.joviansapps.ganymede.ui.screens.utilities.math

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixCalculatorScreen() {
    var matrixSize by remember { mutableStateOf(2) }
    var matrixA by remember { mutableStateOf(Array(3) { Array(3) { 0.0 } }) }
    var matrixB by remember { mutableStateOf(Array(3) { Array(3) { 0.0 } }) }
    var result by remember { mutableStateOf<Array<Array<Double>>?>(null) }
    var operation by remember { mutableStateOf("Addition") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Calculatrice de Matrices",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Taille:", modifier = Modifier.align(Alignment.CenterVertically))
                OutlinedTextField(
                    value = matrixSize.toString(),
                    onValueChange = {
                        val size = it.toIntOrNull()?.coerceIn(2, 3) ?: 2
                        matrixSize = size
                        // Réinitialiser les matrices
                        matrixA = Array(3) { Array(3) { 0.0 } }
                        matrixB = Array(3) { Array(3) { 0.0 } }
                        result = null
                    },
                    modifier = Modifier.width(80.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Text("×$matrixSize", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Addition", "Soustraction", "Multiplication", "Déterminant").forEach { op ->
                    FilterChip(
                        onClick = { operation = op },
                        label = { Text(op, fontSize = 12.sp) },
                        selected = operation == op
                    )
                }
            }
        }

        item {
            Text("Matrice A:", fontWeight = FontWeight.Medium)
            MatrixInput(
                matrix = matrixA,
                size = matrixSize,
                onValueChange = { row, col, value ->
                    matrixA[row][col] = value
                }
            )
        }

        if (operation != "Déterminant") {
            item {
                Text("Matrice B:", fontWeight = FontWeight.Medium)
                MatrixInput(
                    matrix = matrixB,
                    size = matrixSize,
                    onValueChange = { row, col, value ->
                        matrixB[row][col] = value
                    }
                )
            }
        }

        item {
            Button(
                onClick = {
                    result = when (operation) {
                        "Addition" -> addMatrices(matrixA, matrixB, matrixSize)
                        "Soustraction" -> subtractMatrices(matrixA, matrixB, matrixSize)
                        "Multiplication" -> multiplyMatrices(matrixA, matrixB, matrixSize)
                        "Déterminant" -> arrayOf(arrayOf(calculateDeterminant(matrixA, matrixSize)))
                        else -> null
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculer")
            }
        }

        result?.let { res ->
            item {
                Text("Résultat:", fontWeight = FontWeight.Medium)
                if (operation == "Déterminant") {
                    Text(
                        text = "det(A) = ${res[0][0]}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    MatrixDisplay(matrix = res, size = matrixSize)
                }
            }
        }
    }
}

@Composable
private fun MatrixInput(
    matrix: Array<Array<Double>>,
    size: Int,
    onValueChange: (Int, Int, Double) -> Unit
) {
    Column {
        repeat(size) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(size) { col ->
                    OutlinedTextField(
                        value = if (matrix[row][col] == 0.0) "" else matrix[row][col].toString(),
                        onValueChange = { value ->
                            val doubleValue = value.toDoubleOrNull() ?: 0.0
                            onValueChange(row, col, doubleValue)
                        },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
    }
}

@Composable
private fun MatrixDisplay(matrix: Array<Array<Double>>, size: Int) {
    Column {
        repeat(size) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(size) { col ->
                    Text(
                        text = String.format("%.2f", matrix[row][col]),
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
        }
    }
}

// Fonctions de calcul matriciel
private fun addMatrices(a: Array<Array<Double>>, b: Array<Array<Double>>, size: Int): Array<Array<Double>> {
    return Array(size) { row ->
        Array(size) { col ->
            a[row][col] + b[row][col]
        }
    }
}

private fun subtractMatrices(a: Array<Array<Double>>, b: Array<Array<Double>>, size: Int): Array<Array<Double>> {
    return Array(size) { row ->
        Array(size) { col ->
            a[row][col] - b[row][col]
        }
    }
}

private fun multiplyMatrices(a: Array<Array<Double>>, b: Array<Array<Double>>, size: Int): Array<Array<Double>> {
    return Array(size) { row ->
        Array(size) { col ->
            (0 until size).sumOf { k -> a[row][k] * b[k][col] }
        }
    }
}

private fun calculateDeterminant(matrix: Array<Array<Double>>, size: Int): Double {
    return when (size) {
        2 -> matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]
        3 -> {
            matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1]) -
            matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0]) +
            matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0])
        }
        else -> 0.0
    }
}
