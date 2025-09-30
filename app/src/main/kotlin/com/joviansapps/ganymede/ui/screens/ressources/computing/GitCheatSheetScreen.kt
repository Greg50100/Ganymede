package com.joviansapps.ganymede.ui.screens.ressources.computing

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class GitCommand(val command: String, val description: String)

@Composable
fun GitCheatSheetScreen() {
    val commands = listOf(
        GitCommand("git clone [url]", "Get a copy of a remote repository."),
        GitCommand("git add [file]", "Stage a file for the next commit."),
        GitCommand("git commit -m \"msg\"", "Commit staged files with a message."),
        GitCommand("git push", "Send local commits to the remote repository."),
        GitCommand("git pull", "Fetch and merge changes from the remote repository."),
        GitCommand("git branch", "List, create, or delete branches."),
        GitCommand("git merge [branch]", "Merge a branch into the current branch."),
        GitCommand("git status", "Show the working tree status.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.git_cheatsheet)) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(commands) { command ->
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Text(command.command, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
                    Text(command.description, style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}
