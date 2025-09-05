package com.joviansapps.ganymede.ui.screens.calculator

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
fun CalculatorScreen() {
    val vm: CalculatorViewModel = viewModel()
    val expression by vm.expr.collectAsState()
    val result by vm.result.collectAsState()
    val isDegrees by vm.isDegrees.collectAsState()
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    // Supprime le Scaffold interne pour éviter un double padding/insets
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        // Header : toggle Degrés/Radians
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { vm.toggleDegrees() }) {
                Text(if (isDegrees) "DEG" else "RAD")
            }
        }

        // Display (hauteur minimale réduite + scroll horizontal pour textes longs)
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
            // Expression (scroll horizontal si trop longue)
            val exprScroll = rememberScrollState()
            Text(
                text = expression,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 32.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScroll),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.calc_swipe_hint),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(4.dp))

        // Pager (fonctions gauche / pavé / fonctions droite)
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

        // Bandeau mémoire & actions globales
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallTextButton(stringResource(R.string.calc_m_plus), modifier = Modifier.weight(1f)) { vm.onMemoryPlus() }
            SmallTextButton(stringResource(R.string.calc_m_minus), modifier = Modifier.weight(1f)) { vm.onMemoryMinus() }
            SmallTextButton(stringResource(R.string.calc_m_recall), modifier = Modifier.weight(1f)) { vm.onMemoryRecall() }
            SmallTextButton(stringResource(R.string.calc_m_clear), modifier = Modifier.weight(1f)) { vm.onMemoryClear() }
        }
    }
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
                            "⌫" -> vm.onDelete()
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
            SmallKey("sinh��¹", modifier = Modifier.weight(1f)) { vm.onFunction("asinh(") }
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
        shape = RoundedCornerShape(8.dp)
    ) { Text(label) }
}

@Composable
private fun SmallKey(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick, modifier = modifier.height(44.dp), shape = RoundedCornerShape(6.dp)) { Text(label) }
}

@Composable
private fun SmallTextButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier.height(44.dp), shape = RoundedCornerShape(6.dp)) { Text(label, fontSize = 12.sp) }
}
