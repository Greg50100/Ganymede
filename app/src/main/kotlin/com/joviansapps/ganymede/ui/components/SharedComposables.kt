package com.joviansapps.ganymede.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import androidx.compose.foundation.layout.width

/**
 * A reusable, styled dropdown menu for selecting an item from a list,
 * with an optional colored leading icon.
 * This component is designed to replace duplicated dropdown logic in various calculator screens.
 *
 * @param items The list of strings to display in the dropdown.
 * @param selected The currently selected item.
 * @param onSelected The callback function to be invoked when an item is selected.
 * @param label The text to display as the label for the dropdown's text field.
 * @param getColorForItem A lambda function that returns a Color for a given item, used for the leading icon. If null, no icon is shown.
 */
@Composable
fun ColorSelectionDropdown(
    items: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    label: String,
    getColorForItem: (item: String) -> Color?
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val listBg = MaterialTheme.colorScheme.surfaceVariant
    val selectedBg = MaterialTheme.colorScheme.primaryContainer
    val selectedText = MaterialTheme.colorScheme.onPrimaryContainer
    val itemTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(Modifier.fillMaxWidth()) {
        Box {
            OutlinedTextField(
                value = selected,
                onValueChange = { /* Read-only */ },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label) },
                readOnly = true,
                trailingIcon = {
                    Text(if (expanded) stringResource(id = R.string.arrow_up) else stringResource(id = R.string.arrow_down))
                },
                leadingIcon = {
                    val color = getColorForItem(selected)
                    if (color != null) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color,
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            )
            // Transparent box to capture clicks over the whole field
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = !expanded }
            )
        }

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(listBg, RoundedCornerShape(4.dp))
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = 240.dp)
                        .verticalScroll(scrollState)
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items.forEach { item ->
                        val isSelected = item == selected
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) selectedBg else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    onSelected(item)
                                    expanded = false
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val color = getColorForItem(item)
                            if (color != null) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(
                                            color,
                                            RoundedCornerShape(4.dp)
                                        )
                                )
                            }
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) selectedText else itemTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
    }
}

@Composable
fun TableHeader(headers: List<String>) {
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        headers.forEach { header ->
            Text(
                text = header,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .width(150.dp)
                    .padding(horizontal = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TableRow(cells: List<String>) {
    Row(
        Modifier
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        cells.forEach { cell ->
            Text(
                text = cell,
                modifier = Modifier
                    .width(150.dp)
                    .padding(horizontal = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
