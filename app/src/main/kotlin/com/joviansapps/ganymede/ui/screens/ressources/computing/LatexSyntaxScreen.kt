package com.joviansapps.ganymede.ui.screens.ressources.computing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class LatexSyntax(val title: String, val syntax: String)

@Composable
fun LatexSyntaxScreen() {
    val syntaxes = listOf(
        LatexSyntax("Fraction", "\\frac{a}{b}"),
        LatexSyntax("Summation", "\\sum_{i=1}^{n} i"),
        LatexSyntax("Integral", "\\int_{a}^{b} x^2 dx"),
        LatexSyntax("Square Root", "\\sqrt{x}"),
        LatexSyntax("Greek Letters", "\\alpha, \\beta, \\gamma"),
        LatexSyntax("Subscript", "x_i"),
        LatexSyntax("Superscript", "x^2"),
        LatexSyntax("Matrix", "\\begin{pmatrix} a & b \\\\ c & d \\end{pmatrix}")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.latex_syntax)) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(syntaxes) { syntax ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(syntax.title, style = MaterialTheme.typography.h6)
                        Text(
                            text = syntax.syntax,
                            style = MaterialTheme.typography.body2,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
