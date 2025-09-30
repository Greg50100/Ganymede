package com.joviansapps.ganymede.ui.screens.ressources.computing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class MarkdownSyntax(val title: String, val syntax: String, val example: String)

@Composable
fun MarkdownSyntaxScreen() {
    val syntaxes = listOf(
        MarkdownSyntax("Headers", "# H1\n## H2", "<h1>Header 1</h1>"),
        MarkdownSyntax("Bold", "**bold text**", "<strong>bold text</strong>"),
        MarkdownSyntax("Italic", "*italicized text*", "<em>italicized text</em>"),
        MarkdownSyntax("Blockquote", "> blockquote", "<blockquote>blockquote</blockquote>"),
        MarkdownSyntax("Ordered List", "1. First item\n2. Second item", "<ol><li>...</li></ol>"),
        MarkdownSyntax("Unordered List", "- First item\n- Second item", "<ul><li>...</li></ul>"),
        MarkdownSyntax("Link", "[title](https://www.example.com)", "<a href=...>title</a>"),
        MarkdownSyntax("Image", "![alt text](image.jpg)", "<img src=... alt=...>"),
        MarkdownSyntax("Inline Code", "`code`", "<code>code</code>")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.markdown_syntax)) },
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
                SyntaxCard(syntax)
            }
        }
    }
}

@Composable
private fun SyntaxCard(info: MarkdownSyntax) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(info.title, style = MaterialTheme.typography.h6)
            Text(
                text = info.syntax,
                style = MaterialTheme.typography.body2,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
