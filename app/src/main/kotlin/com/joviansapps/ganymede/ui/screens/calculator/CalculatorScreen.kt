package com.joviansapps.ganymede.ui.screens.calculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.data.CalculatorAction
import com.joviansapps.ganymede.viewmodel.CalculatorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
fun CalculatorScreen(
    vm: CalculatorViewModel = viewModel(),
    hapticFeedbackEnabled: Boolean = true
) {
    val state by vm.uiState.collectAsState()
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    var showHistory by remember { mutableStateOf(false) }

    val haptics = LocalHapticFeedback.current
    val onActionWithHaptics: (CalculatorAction) -> Unit = { action ->
        if (hapticFeedbackEnabled) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        vm.onAction(action)
    }

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
                val mem = if (state.hasMemory) stringResource(R.string.calc_memory_indicator) else ""
                Text(mem, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = state.result,
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
            val exprScroll = rememberScrollState()
            LaunchedEffect(state.expression) {
                exprScroll.animateScrollTo(exprScroll.maxValue)
            }
            SelectionContainer {
                Box(Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScroll)) { ColorizedExpression(state.expression) }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.calc_swipe_hint),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(6.dp))

        // Pager for keypads
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.4f),
            beyondViewportPageCount = 1,
            pageSpacing = 6.dp
        ) { page ->
            when (page) {
                0 -> FunctionPageLeft(onAction = onActionWithHaptics)
                1 -> NumericPadPage(onAction = onActionWithHaptics)
                2 -> FunctionPageRight(onAction = onActionWithHaptics)
            }
        }
        // Pager indicators
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

        // Action row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallTextButton("C", modifier = Modifier.weight(1f)) { onActionWithHaptics(CalculatorAction.ClearAll) }
            SmallTextButton("CE", modifier = Modifier.weight(1f)) { onActionWithHaptics(CalculatorAction.Clear) }
            SmallTextButton(if (state.isDegrees) "DEG" else "RAD", modifier = Modifier.weight(1f)) { onActionWithHaptics(CalculatorAction.ToggleDegrees) }
            SmallTextButton("H", modifier = Modifier.weight(1f)) {
                if (hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                showHistory = true
            }
        }
        Spacer(Modifier.height(6.dp))
        // Memory row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallTextButton(stringResource(R.string.calc_m_plus), modifier = Modifier.weight(1f)) { onActionWithHaptics(CalculatorAction.MemoryPlus) }
            SmallTextButton(stringResource(R.string.calc_m_minus), modifier = Modifier.weight(1f)) { onActionWithHaptics(CalculatorAction.MemoryMinus) }
            SmallTextButton(stringResource(R.string.calc_m_recall), modifier = Modifier.weight(1f)) { onActionWithHaptics(CalculatorAction.MemoryRecall) }
            SmallTextButton(stringResource(R.string.calc_m_clear), modifier = Modifier.weight(1f)) { onActionWithHaptics(CalculatorAction.MemoryClear) }
        }
    }

    if (showHistory) {
        AlertDialog(
            onDismissRequest = { showHistory = false },
            title = { Text(stringResource(R.string.history_title)) },
            text = {
                if (state.history.isEmpty()) Text(stringResource(R.string.history_empty)) else {
                    LazyColumn { itemsIndexed(state.history) { i, item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.formatForDisplay(), modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val left = item.expression
                                    onActionWithHaptics(CalculatorAction.SetExpression(left))
                                    showHistory = false
                                })
                            IconButton(onClick = { onActionWithHaptics(CalculatorAction.RemoveHistoryItem(i)) }) {
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
            dismissButton = { TextButton(onClick = { onActionWithHaptics(CalculatorAction.ClearHistory) }) { Text(stringResource(R.string.history_clear_all_button)) } }
        )
    }
}

@Composable
private fun FunctionPageLeft(onAction: (CalculatorAction) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("sin", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("sin")) }
            SmallKey("cos", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("cos")) }
            SmallKey("tan", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("tan")) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("ln", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("ln")) }
            SmallKey("log", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("log")) }
            SmallKey("√",  modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("sqrt")) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("eˣ", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("e^(")) }
            SmallKey("x²", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("^2")) }
            SmallKey("xʸ", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operator("^(")) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            SmallKey("|x|", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("abs")) }
            SmallKey("π",  modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("pi")) }
            SmallKey("e",  modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("e")) }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun NumericPadPage(onAction: (CalculatorAction) -> Unit) {
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
                        DeleteKey(modifier = Modifier.weight(1f), onDelete = { onAction(CalculatorAction.Delete) }, onClear = { onAction(CalculatorAction.Clear) })
                    } else {
                        CalculatorKey(label = label, modifier = Modifier.weight(if (label == "0") 2.1f else 1f)) {
                            when (label) {
                                in "0".."9" -> onAction(CalculatorAction.Number(label))
                                "." -> onAction(CalculatorAction.Decimal)
                                "=" -> onAction(CalculatorAction.Evaluate)
                                "×", "*" -> onAction(CalculatorAction.Operator("*"))
                                "÷", "/" -> onAction(CalculatorAction.Operator("/"))
                                in "+-%" -> onAction(CalculatorAction.Operator(label))
                                "(" -> onAction(CalculatorAction.LeftParenthesis)
                                ")" -> onAction(CalculatorAction.RightParenthesis)
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
private fun FunctionPageRight(onAction: (CalculatorAction) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("sin⁻¹", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("asin")) }
            SmallKey("cos⁻¹", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("acos")) }
            SmallKey("tan⁻¹", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("atan")) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("sinh", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("sinh")) }
            SmallKey("cosh", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("cosh")) }
            SmallKey("tanh", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("tanh")) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallKey("sinh⁻¹", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("asinh")) }
            SmallKey("cosh⁻¹", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("acosh")) }
            SmallKey("tanh⁻¹", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("atanh")) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            SmallKey("2ˣ", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("2^(")) }
            SmallKey("x!", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Function("fact")) }
            SmallKey("mod", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operator("mod")) }
        }
        Spacer(Modifier.weight(1f))
    }
}

// --- Helper Composables for Keys and Styling ---

@Composable
private fun ColorizedExpression(text: String) {
    val numberRegex = Regex("\\d+\\.?\\d*")
    val tokenRegex = Regex("\\d+\\.?\\d*|[A-Za-z_]+|[()+\\-*/%^×÷]")
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

@Composable
private fun DeleteKey(modifier: Modifier = Modifier, onDelete: () -> Unit, onClear: () -> Unit) {
    val scope = rememberCoroutineScope()
    Button(
        onClick = { onDelete() },
        modifier = modifier
            .height(56.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onClear() },
                    onPress = {
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

@Composable
private fun colorsForCalculatorKey(label: String): ButtonColors {
    return when {
        label == "=" -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
        label in listOf("+", "-", "×", "÷", "/", "*", "%", "mod", "^") -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary)
        label == "⌫" || label == "C" -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
        label == "." || label.matches(Regex("\\d")) -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
        else -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
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
