package com.joviansapps.ganymede.ui.screens.ressources.computing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

data class RegexInfo(val title: String, val pattern: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegexCheatSheetScreen() {
    val regexList = listOf(
        RegexInfo(
            title = "Email Validation",
            pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            description = "Checks for a standard email format."
        ),
        RegexInfo(
            title = "URL Validation",
            pattern = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$",
            description = "Validates a web URL, including http/https."
        ),
        RegexInfo(
            title = "IPv4 Address",
            pattern = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b",
            description = "Finds an IPv4 address in the format X.X.X.X."
        ),
        RegexInfo(
            title = "International Phone Number",
            pattern = "^\\+?[1-9]\\d{1,14}$",
            description = "A simple regex for international phone numbers (E.164 format)."
        ),
        RegexInfo(
            title = "Date (YYYY-MM-DD)",
            pattern = "^\\d{4}-\\d{2}-\\d{2}$",
            description = "Validates a date in YYYY-MM-DD format."
        ),
        RegexInfo(
            title = "Username",
            pattern = "^[a-zA-Z0-9_]{3,16}$",
            description = "Allows alphanumeric characters and underscores, 3 to 16 characters long."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.regex_cheat_sheet)) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(regexList) { regex ->
                RegexCard(regexInfo = regex)
            }
        }
    }
}

@Composable
fun RegexCard(regexInfo: RegexInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(regexInfo.title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = regexInfo.pattern,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(regexInfo.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
