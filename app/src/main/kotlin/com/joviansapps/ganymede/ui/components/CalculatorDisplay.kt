// app/src/main/kotlin/com/joviansapps/ganymede/ui/components/CalculatorDisplay.kt
package com.joviansapps.ganymede.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.data.CalculatorAction
import com.joviansapps.ganymede.viewmodel.CalculatorViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.CalculatorDisplay(
    vm: CalculatorViewModel,
    primaryTextStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    secondaryTextStyle: TextStyle = MaterialTheme.typography.headlineSmall
) {
    // Collecte explicite (évite problème delegate)
    val historyState = vm.history.collectAsState()
    val history = historyState.value
    val displayState = vm.displayText.collectAsState()
    val display = displayState.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
    ) {
        val listState = rememberLazyListState()

        LaunchedEffect(history.size) {
            if (history.isNotEmpty()) {
                listState.scrollToItem(history.size - 1)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            state = listState,
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Bottom)
        ) {
            items(history) { item ->
                Text(
                    text = item.formatForDisplay(),
                    style = secondaryTextStyle,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.onAction(CalculatorAction.DeleteAll) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)),
            horizontalArrangement = Arrangement.End
        ) {
            BasicTextField(
                value = display,
                onValueChange = {},
                singleLine = true,
                readOnly = true,
                textStyle = primaryTextStyle.copy(textAlign = TextAlign.End),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}