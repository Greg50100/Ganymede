package com.joviansapps.ganymede.ui.screens.ressources.computing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

data class CommandInfo(val command: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsefulCommandsScreen() {
    val pages = listOf("Linux", "Windows", "macOS")
    var selectedTab by remember { mutableStateOf(0) }

    val commands = mapOf(
        "Linux" to listOf(
            CommandInfo("ls -la", "List files and directories with detailed information."),
            CommandInfo("cd <directory>", "Change current directory."),
            CommandInfo("mkdir <name>", "Create a new directory."),
            CommandInfo("rm <file>", "Remove a file."),
            CommandInfo("sudo apt update", "Update package lists (Debian/Ubuntu)."),
            CommandInfo("grep 'pattern' <file>", "Search for a pattern in a file."),
            CommandInfo("pwd", "Print working directory.")
        ),
        "Windows" to listOf(
            CommandInfo("dir", "List files and directories."),
            CommandInfo("cd <directory>", "Change current directory."),
            CommandInfo("mkdir <name>", "Create a new directory."),
            CommandInfo("del <file>", "Delete a file."),
            CommandInfo("ipconfig", "Display network configuration."),
            CommandInfo("sfc /scannow", "Scan and repair system files."),
            CommandInfo("tasklist", "List running processes.")
        ),
        "macOS" to listOf(
            CommandInfo("ls -la", "List files and directories with detailed information."),
            CommandInfo("cd <directory>", "Change current directory."),
            CommandInfo("mkdir <name>", "Create a new directory."),
            CommandInfo("rm <file>", "Remove a file."),
            CommandInfo("brew install <package>", "Install a package using Homebrew."),
            CommandInfo("top", "Display running processes."),
            CommandInfo("pwd", "Print working directory.")
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.useful_commands)) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                pages.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            when (selectedTab) {
                0 -> CommandList(commands = commands["Linux"] ?: emptyList())
                1 -> CommandList(commands = commands["Windows"] ?: emptyList())
                2 -> CommandList(commands = commands["macOS"] ?: emptyList())
            }
        }
    }
}

@Composable
fun CommandList(commands: List<CommandInfo>) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(commands) { command ->
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text(
                    text = command.command,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = command.description,
                    style = MaterialTheme.typography.bodyMedium
                )
             }
         }
     }
 }
