package com.joviansapps.ganymede.ui.screens.graph

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GraphScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Valider l'expression (parser/erreurs) et afficher un message i18n en cas d'échec.
    val vm = viewModel<com.joviansapps.ganymede.graphing.GraphViewModel>()

    var expr by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }

    // Palette cyclique (distinct pour éviter doublons)
    val palette = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.inversePrimary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    ).distinct().ifEmpty { listOf(Color.Red, Color.Green, Color.Blue) }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val tabTitles = listOf(stringResource(R.string.tab_functions), stringResource(R.string.tab_graph))

    Column(modifier.fillMaxSize()) {
        // Tab navigation sans TopAppBar
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            when (page) {
                0 -> FunctionInputPage(
                    expr = expr,
                    name = name,
                    onExprChange = { expr = it },
                    onNameChange = { name = it },
                    onAdd = {
                        if (expr.isNotBlank()) {
                            val fnName = if (name.isBlank()) "f${'$'}{vm.functions.size + 1}" else name
                            val color = palette[vm.functions.size % palette.size]
                            vm.functions.add(com.joviansapps.ganymede.graphing.GraphFunction(expr, color, fnName))
                            expr = ""
                            name = ""
                        }
                    },
                    onClearAll = { vm.functions.clear() },
                    onDelete = { idx -> if (idx in vm.functions.indices) vm.functions.removeAt(idx) },
                    functions = vm.functions
                )
                1 -> GraphPage(vm)
            }
        }
    }
}

@Composable
private fun FunctionInputPage(
    expr: String,
    name: String,
    onExprChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onAdd: () -> Unit,
    onClearAll: () -> Unit,
    onDelete: (Int) -> Unit,
    functions: MutableList<com.joviansapps.ganymede.graphing.GraphFunction>,
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = expr,
            onValueChange = onExprChange,
            label = { Text(stringResource(R.string.f_x)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.name_optionnal)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onAdd, enabled = expr.isNotBlank()) { Text(stringResource(R.string.add)) }
            OutlinedButton(onClick = onClearAll, enabled = functions.isNotEmpty()) { Text(stringResource(R.string.clear_all)) }
        }
        if (functions.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.functions_count, functions.size), style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            LazyColumn(Modifier.weight(1f)) {
                itemsIndexed(functions, key = { index, f -> f.name + index }) { index, f ->
                    FunctionRow(index = index, f = f, onDelete = onDelete)
                }
            }
        } else {
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.no_functions), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FunctionRow(index: Int, f: com.joviansapps.ganymede.graphing.GraphFunction, onDelete: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(f.color)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(f.name, style = MaterialTheme.typography.bodyMedium, color = f.color)
            Text("= ${'$'}{f.expression}", style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = { onDelete(index) }) {
            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
        }
    }
    // remplace Divider déprécié
    HorizontalDivider(thickness = 0.5.dp)
}

@Composable
private fun GraphPage(vm: com.joviansapps.ganymede.graphing.GraphViewModel) {
    // Retirer pointerInput du Box — CanvasView gère maintenant les gestures (pinch/double-tap)
    Box(Modifier.fillMaxSize()) {
        com.joviansapps.ganymede.graphing.CanvasView(vm = vm)

        // Overlay boutons zoom
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallFloatingActionButton(onClick = {
                    val w = vm.window.value
                    val cx = (w.xMin + w.xMax) / 2f
                    val cy = (w.yMin + w.yMax) / 2f
                    val zoom = 1.25f
                    w.xMin = cx + (w.xMin - cx) / zoom
                    w.xMax = cx + (w.xMax - cx) / zoom
                    w.yMin = cy + (w.yMin - cy) / zoom
                    w.yMax = cy + (w.yMax - cy) / zoom
                    vm.window.value = vm.window.value
                }) { Icon(Icons.Filled.ZoomIn, contentDescription = stringResource(R.string.zoom_in)) }

                SmallFloatingActionButton(onClick = {
                    val w = vm.window.value
                    val cx = (w.xMin + w.xMax) / 2f
                    val cy = (w.yMin + w.yMax) / 2f
                    val zoom = 1.25f
                    w.xMin = cx + (w.xMin - cx) * zoom
                    w.xMax = cx + (w.xMax - cx) * zoom
                    w.yMin = cy + (w.yMin - cy) * zoom
                    w.yMax = cy + (w.yMax - cy) * zoom
                    vm.window.value = vm.window.value
                }) { Icon(Icons.Filled.ZoomOut, contentDescription = stringResource(R.string.zoom_out)) }

                SmallFloatingActionButton(onClick = {
                    vm.window.value = com.joviansapps.ganymede.graphing.Window()
                }) { Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.reset)) }
            }
        }
    }
}
