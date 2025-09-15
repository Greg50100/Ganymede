package com.joviansapps.ganymede.ui.screens.graph

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.graphing.CanvasView
import com.joviansapps.ganymede.graphing.GraphFunction
import com.joviansapps.ganymede.graphing.GraphViewModel

@Composable
fun GraphScreen(graphViewModel: GraphViewModel = viewModel()) {
    // functions is a SnapshotStateList<GraphFunction> exposed by the ViewModel
    val functions = graphViewModel.functions
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // The CanvasView now takes up the majority of the screen
        Box(modifier = Modifier.weight(1f)) {
            CanvasView(vm = graphViewModel)
        }
        // Controls are simplified
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val functionsCount = functions.size
            Text(
                text = context.resources.getQuantityString(R.plurals.functions_count, functionsCount, functionsCount),
                style = MaterialTheme.typography.bodyLarge
            )

            // Reset and clear
            Row {
                IconButton(onClick = { graphViewModel.resetZoom() }) {
                    Icon(Icons.Default.RestartAlt, contentDescription = stringResource(R.string.reset))
                }
                IconButton(onClick = { graphViewModel.clearFunctions() }, enabled = functions.isNotEmpty()) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.clear_all))
                }
            }
        }
        FunctionInputPage(
            functions = functions,
            onAddFunction = { name, expression -> graphViewModel.addFunction(name, expression) },
            onRemoveFunction = { id -> graphViewModel.removeFunction(id) },
            onFunctionExpressionChange = { id, expression ->
                val index = functions.indexOfFirst { it.id == id }
                if (index != -1) {
                    val old = functions[index]
                    // create a new GraphFunction preserving color and id and name
                    functions[index] = GraphFunction(expression, old.color, old.name, old.id)
                }
            },
            onFunctionNameChange = { id, name ->
                val index = functions.indexOfFirst { it.id == id }
                if (index != -1) {
                    val old = functions[index]
                    functions[index] = GraphFunction(old.expression, old.color, name, old.id)
                }
            }
        )
    }
}

@Composable
private fun FunctionInputPage(
    functions: List<GraphFunction>,
    onAddFunction: (String, String) -> Unit,
    onRemoveFunction: (String) -> Unit,
    onFunctionExpressionChange: (String, String) -> Unit,
    onFunctionNameChange: (String, String) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
            items(functions) { func ->
                FunctionInputField(
                    function = func,
                    onNameChange = { newName -> onFunctionNameChange(func.id, newName) },
                    onExpressionChange = { newExpression -> onFunctionExpressionChange(func.id, newExpression) },
                    onRemove = { onRemoveFunction(func.id) }
                )
            }
        }
        Button(
            onClick = {
                // show a simple default example when adding without a name
                onAddFunction("", "x")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.add))
        }
    }
}

@Composable
private fun FunctionInputField(
    function: GraphFunction,
    onNameChange: (String) -> Unit,
    onExpressionChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = function.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.name_optionnal)) },
            modifier = Modifier.width(120.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = function.expression,
            onValueChange = onExpressionChange,
            label = { Text(stringResource(R.string.f_x)) },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
        }
    }
}
