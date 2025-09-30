package com.joviansapps.ganymede.ui.screens.ressources.computing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.navigation.Dest
import com.joviansapps.ganymede.ui.screens.ressources.common.ResourcesCategoryGridScreen
import com.joviansapps.ganymede.ui.screens.ressources.common.CategoryItem

@Composable
fun ComputingCategoryScreen(
    onOpenASCIITables: () -> Unit,
    onOpenLogicGates: () -> Unit,
    onOpenGitCheatSheet: () -> Unit,
    onOpenHttpCodes: () -> Unit,
    onOpenLatexSyntax: () -> Unit,
    onOpenMarkdownSyntax: () -> Unit,
    onOpenRegexCheatSheet: () -> Unit,
    onOpenTcpUdpPorts: () -> Unit,
    onOpenUsefulCommands: () -> Unit,
    modifier: Modifier = Modifier
) {
    val computingItems = listOf(
        CategoryItem(
            title = "ASCII Tables", //TODO À AJOUTER DANS strings.xml
            description = "Display ASCII character tables.", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenASCIITables
        ),
        CategoryItem(
            title = "Logic Gates", //TODO À AJOUTER DANS strings.xml
            description = "Information about common logic gates.", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenLogicGates
        ),
        CategoryItem(
            title = "Git Cheat Sheet", //TODO À AJOUTER DANS strings.xml
            description = "Common Git commands and workflows.", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenGitCheatSheet
        ),
        CategoryItem(
            title = "HTTP Status Codes", //TODO À AJOUTER DANS strings.xml
            description = "List of HTTP status codes and meanings.", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenHttpCodes
        ),
        CategoryItem(
            title = "LaTeX Syntax", //TODO À AJOUTER DANS strings.xml
            description = "Common LaTeX commands and syntax.", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenLatexSyntax
        ),
        CategoryItem(
            title = "Markdown Syntax", //TODO À AJOUTER DANS strings.xml
            description = "Markdown formatting guide.", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenMarkdownSyntax
        ),
        CategoryItem(
            title = "Regex Cheat Sheet", //TODO À AJOUTER DANS strings.xml
            description = "Common regular expression patterns.", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenRegexCheatSheet
        ),
        CategoryItem(
            title = "TCP/UDP Ports", //TODO À AJOUTER DANS strings.xml
            description = "List of common TCP and UDP ports.", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenTcpUdpPorts
        ),
        CategoryItem(
            title = stringResource(id = R.string.useful_commands),
            description = stringResource(id = R.string.computing_category_description),
            icon = Icons.Default.Science,
            onClick = onOpenUsefulCommands
        )
        // D'autres ressources informatiques peuvent être ajoutées ici
    )

    ResourcesCategoryGridScreen(items = computingItems, modifier = modifier)
}