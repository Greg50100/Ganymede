package com.joviansapps.ganymede.ui.screens.calculator

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.viewmodel.CalculatorViewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
fun CalculatorScreen() {
    val vm: CalculatorViewModel = viewModel()
    val expression by vm.expr.collectAsState()
    val result by vm.result.collectAsState()
    val history by vm.history.collectAsState()
    val isDegrees by vm.isDegrees.collectAsState()
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    var showHistory by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        // Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            val scroll = rememberScrollState()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val mem = if (vm.hasMemory()) stringResource(R.string.calc_memory_indicator) else ""
                Text(mem, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = result,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 52.sp,
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(scroll),
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )
            }
            Spacer(Modifier.height(6.dp))
            // Expression (colorisée + scroll horizontal si trop longue)
            val exprScroll = rememberScrollState()
            SelectionContainer {
                Box(Modifier.fillMaxWidth().horizontalScroll(exprScroll)) { ColorizedExpression(expression) }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.calc_swipe_hint),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(6.dp))


        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.4f),
            beyondViewportPageCount = 1,
            pageSpacing = 6.dp
        ) { page ->
            when (page) {
                0 -> FunctionPageLeft(vm)
                1 -> NumericPadPage(vm)
                2 -> FunctionPageRight(vm)
            }
        }
        // Indicateurs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            (0 until 3).forEach { i ->
                val active = i == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(if (active) 22.dp else 8.dp)
                        .background(
                            if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(50)
                        )
                )
            }
        }

        // Nouvelle rangée d'actions (style mémoire) : C, CE, RAD/DEG, H
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallTextButton("C", modifier = Modifier.weight(1f)) { vm.onEvent(com.joviansapps.ganymede.data.CalculatorEvent.DeleteAll) }
            SmallTextButton("CE", modifier = Modifier.weight(1f)) { vm.onClear() }
            SmallTextButton(if (isDegrees) "DEG" else "RAD", modifier = Modifier.weight(1f)) { vm.toggleDegrees() }
            SmallTextButton("H", modifier = Modifier.weight(1f)) { showHistory = true }
        }
        Spacer(Modifier.height(6.dp))
        // Rangée mémoire en bas (inchangée)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallTextButton(stringResource(R.string.calc_m_plus), modifier = Modifier.weight(1f)) { vm.onMemoryPlus() }
            SmallTextButton(stringResource(R.string.calc_m_minus), modifier = Modifier.weight(1f)) { vm.onMemoryMinus() }
            SmallTextButton(stringResource(R.string.calc_m_recall), modifier = Modifier.weight(1f)) { vm.onMemoryRecall() }
            SmallTextButton(stringResource(R.string.calc_m_clear), modifier = Modifier.weight(1f)) { vm.onMemoryClear() }
        }
    }

    if (showHistory) {
        // Historique (boîte de dialogue) — icônes Material déjà utilisées
        AlertDialog(
            onDismissRequest = { showHistory = false },
            title = { Text(stringResource(R.string.history_title)) },
            text = {
                if (history.isEmpty()) Text(stringResource(R.string.history_empty)) else {
                    LazyColumn { itemsIndexed(history) { i, item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item, modifier = Modifier.weight(1f).clickable {
                                // item format: "expr = result" -> récupérer la partie gauche
                                val left = item.split(" = ").firstOrNull() ?: item
                                vm.setExpression(left)
                                showHistory = false
                            })
                            IconButton(onClick = { vm.removeHistoryItem(i) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete)
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    } }
                }
            },
            confirmButton = { TextButton(onClick = { showHistory = false }) { Text(stringResource(R.string.history_close_button)) } },
            dismissButton = { TextButton(onClick = { vm.clearHistory() }) { Text(stringResource(R.string.history_clear_all_button)) } }
        )
    }
}

// Colorisation simple : nombres, opérateurs, fonctions, parenthèses (avec profondeur)
@Composable
private fun ColorizedExpression(text: String) {
    val numberRegex = Regex("\\d+\\.?\\d*")
    val tokenRegex = Regex("\\d+\\.?\\d*|[A-Za-z_]+|[()+\\-*/%^×÷]")
    // lire les couleurs du thème dans des variables locales (pour ne pas appeler MaterialTheme depuis le builder non-composable)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    val annotated = buildAnnotatedString {
        var depth = 0
        var last = 0
        for (m in tokenRegex.findAll(text)) {
            if (m.range.first > last) append(text.substring(last, m.range.first))
            val token = m.value
            when {
                token.matches(numberRegex) -> withStyle(SpanStyle(color = onSurfaceColor)) { append(token) }
                token == "(" -> {
                    depth++
                    val color = if (depth % 2 == 0) primaryColor else secondaryColor
                    withStyle(SpanStyle(color = color)) { append(token) }
                }
                token == ")" -> {
                    val color = if (depth % 2 == 0) primaryColor else secondaryColor
                    withStyle(SpanStyle(color = color)) { append(token) }
                    if (depth > 0) depth--
                }
                token.matches(Regex("[+\\-*/%^×÷]")) -> withStyle(SpanStyle(color = secondaryColor)) { append(token) }
                else -> withStyle(SpanStyle(color = tertiaryColor)) { append(token) }
            }
            last = m.range.last + 1
        }
        if (last < text.length) append(text.substring(last))
    }
    Text(annotated, style = MaterialTheme.typography.titleMedium, fontSize = 32.sp)
}

// DeleteKey : tap = delete, long press = clear, hold = repeat deletion
@Composable
private fun DeleteKey(modifier: Modifier = Modifier, onDelete: () -> Unit, onClear: () -> Unit) {
    val scope = rememberCoroutineScope()
    Button(
        onClick = { onDelete() }, // ensure single tap deletes
        modifier = modifier
            .height(56.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onClear() },
                    onPress = {
                        // repeat deletion while pressed (after short delay)
                        val job = scope.launch {
                            delay(400)
                            while (true) {
                                onDelete()
                                delay(80)
                            }
                        }
                        try { awaitRelease() } finally { job.cancel() }
                    }
                )
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
    ) { Text("⌫", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)) }
}

@Composable
private fun FunctionPageLeft(vm: CalculatorViewModel) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("sin", modifier = Modifier.weight(1f)) { vm.onFunction("sin") }
            SmallKey("cos", modifier = Modifier.weight(1f)) { vm.onFunction("cos") }
            SmallKey("tan", modifier = Modifier.weight(1f)) { vm.onFunction("tan") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("ln", modifier = Modifier.weight(1f)) { vm.onFunction("ln") }
            SmallKey("log", modifier = Modifier.weight(1f)) { vm.onFunction("log") }
            SmallKey("√",  modifier = Modifier.weight(1f)) { vm.onFunction("sqrt") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("eˣ", modifier = Modifier.weight(1f)) { vm.onFunction("e^(") }
            SmallKey("x²", modifier = Modifier.weight(1f)) { vm.onFunction("^2") }
            SmallKey("xʸ", modifier = Modifier.weight(1f)) { vm.onOperator("^(") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            SmallKey("|x|", modifier = Modifier.weight(1f)) { vm.onFunction("abs") }
            SmallKey("π",  modifier = Modifier.weight(1f)) { vm.onFunction("pi") }
            SmallKey("e",  modifier = Modifier.weight(1f)) { vm.onFunction("e") }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun NumericPadPage(vm: CalculatorViewModel) {
    val rows = listOf(
        listOf("(",")","%","÷"),
        listOf("7","8","9","×"),
        listOf("4","5","6","-"),
        listOf("1","2","3","+"),
        listOf("0",".","⌫","=")
    )
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    if (label == "⌫") {
                        DeleteKey(modifier = Modifier.weight(1f), onDelete = { vm.onDelete() }, onClear = { vm.onClear() })
                    } else {
                        CalculatorKey(label = label, modifier = Modifier.weight(1f)) {
                            when (label) {
                                in "0".."9" -> vm.onNumber(label)
                                "." -> vm.onDecimal()
                                "=" -> vm.onEvaluate()
                                "×", "*" -> vm.onOperator("*") // Both × and * trigger multiplication
                                "+" -> vm.onOperator("+")
                                "-" -> vm.onOperator("-")
                                "/" -> vm.onOperator("/")
                                "÷" -> vm.onOperator("/") // Both ÷ and / trigger division
                                "%" -> vm.onOperator("%")
                                "(" -> vm.onLeftParen()
                                ")" -> vm.onRightParen()
                                "C" -> vm.onClear()
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun FunctionPageRight(vm: CalculatorViewModel) {
    // Placeholder (peut accueillir futures fonctions : exp, mod, factorial, conversions, etc.)
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("sin⁻¹", modifier = Modifier.weight(1f)) { vm.onFunction("asin(") }
            SmallKey("cos⁻¹", modifier = Modifier.weight(1f)) { vm.onFunction("acos(") }
            SmallKey("tan⁻¹", modifier = Modifier.weight(1f)) { vm.onFunction("atan(") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("sinh", modifier = Modifier.weight(1f)) { vm.onFunction("sinh(") }
            SmallKey("cosh", modifier = Modifier.weight(1f)) { vm.onFunction("cosh(") }
            SmallKey("tanh", modifier = Modifier.weight(1f)) { vm.onFunction("tanh(") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("sinh⁻¹", modifier = Modifier.weight(1f)) { vm.onFunction("asinh(") }
            SmallKey("cosh⁻¹", modifier = Modifier.weight(1f)) { vm.onFunction("acosh(") }
            SmallKey("tanh⁻¹", modifier = Modifier.weight(1f)) { vm.onFunction("atanh(") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            SmallKey("2ˣ", modifier = Modifier.weight(1f)) { vm.onFunction("2^(") }
            SmallKey("x!", modifier = Modifier.weight(1f)) { vm.onFunction("fact(") }
            SmallKey("mod", modifier = Modifier.weight(1f)) { vm.onOperator("mod") }
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun CalculatorKey(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = colorsForCalculatorKey(label)
    ) { Text(label, style = textStyleForCalculatorKey(label)) }
}

@Composable
private fun textStyleForCalculatorKey(label: String): TextStyle {
    return if (label.matches(Regex("\\d")) || label == "0") {
        TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    } else {
        TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

// utilitaire pour choisir les couleurs selon le label
@Composable
private fun colorsForCalculatorKey(label: String): ButtonColors {
    return when {
        label == "=" -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
        label in listOf("+", "-", "×", "÷", "/", "*", "%", "mod", "^") -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
        label == "⌫" || label == "C" -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
        // Chiffres et point : utiliser surfaceVariant / onSurface pour meilleur contraste avec le fond
        label == "." || label.matches(Regex("\\d")) -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        else -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SmallKey(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = colorsForCalculatorKey(label)
    ) { Text(label) }
}

@Composable
private fun SmallTextButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = colorsForCalculatorKey(label)
    ) { Text(label, fontSize = 12.sp) }
}
