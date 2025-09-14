package com.joviansapps.ganymede.ui.screens.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.graphing.CanvasView
import com.joviansapps.ganymede.graphing.GraphFunction
import com.joviansapps.ganymede.graphing.GraphViewModel
import com.joviansapps.ganymede.graphing.Window

@Composable
fun GraphScreen(graphViewModel: GraphViewModel) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(stringResource(R.string.tab_functions), stringResource(R.string.tab_graph))

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> FunctionsTab(graphViewModel)
            1 -> GraphTab(graphViewModel)
        }
    }
}

@Composable
fun FunctionsTab(graphViewModel: GraphViewModel) {
    // functions is a SnapshotStateList<GraphFunction> exposed by the ViewModel
    val functions = graphViewModel.functions
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (functions.isNotEmpty()) {
                    context.resources.getQuantityString(R.plurals.functions_count, functions.size, functions.size)
                } else {
                    stringResource(R.string.no_functions)
                }
            )
            Button(onClick = { functions.clear() }) {
                Text(stringResource(R.string.clear_all))
            }
        }
        FunctionList(functions = functions, onRemove = { functionId ->
            val idx = functions.indexOfFirst { it.id == functionId }
            if (idx >= 0) functions.removeAt(idx)
        })
        AddFunction(onAdd = { name, expression ->
            val displayName = if (name.isBlank()) expression else name
            val color = Color.Red
            functions.add(GraphFunction(expression, color, displayName))
        })
    }
}

@Composable
fun FunctionList(functions: List<GraphFunction>, onRemove: (String) -> Unit) {
    LazyColumn {
        items(functions) { function ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${function.name}: ${function.expression}", modifier = Modifier.weight(1f))
                Button(onClick = { onRemove(function.id) }) {
                    Text(stringResource(R.string.delete))
                }
            }
        }
    }
}

@Composable
fun AddFunction(onAdd: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var expression by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name_optionnal)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = expression,
            onValueChange = { expression = it },
            label = { Text(stringResource(R.string.f_x)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (expression.isNotBlank()) {
                    onAdd(name, expression)
                    name = ""
                    expression = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.add))
        }
    }
}

@Composable
fun GraphTab(graphViewModel: GraphViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CanvasView(graphViewModel)
        Row {
            Button(onClick = {
                // simple zoomIn centered
                val w = graphViewModel.window.value
                val cx = (w.xMin + w.xMax) / 2f
                val factor = 0.8f
                val newW = Window()
                val halfWidth = (w.xMax - w.xMin) * factor / 2f
                val halfHeight = (w.yMax - w.yMin) * factor / 2f
                newW.xMin = cx - halfWidth
                newW.xMax = cx + halfWidth
                val cy = (w.yMin + w.yMax) / 2f
                newW.yMin = cy - halfHeight
                newW.yMax = cy + halfHeight
                newW.findAutoScale()
                graphViewModel.window.value = newW
            }) {
                Text(stringResource(R.string.zoom_in))
            }
            Button(onClick = {
                val w = graphViewModel.window.value
                val cx = (w.xMin + w.xMax) / 2f
                val factor = 1.25f
                val newW = Window()
                val halfWidth = (w.xMax - w.xMin) * factor / 2f
                val halfHeight = (w.yMax - w.yMin) * factor / 2f
                newW.xMin = cx - halfWidth
                newW.xMax = cx + halfWidth
                val cy = (w.yMin + w.yMax) / 2f
                newW.yMin = cy - halfHeight
                newW.yMax = cy + halfHeight
                newW.findAutoScale()
                graphViewModel.window.value = newW
            }) {
                Text(stringResource(R.string.zoom_out))
            }
            Button(onClick = { graphViewModel.window.value = Window() }) {
                Text(stringResource(R.string.reset))
            }
        }
    }
}
