/**
 * File: LogicGatesScreen.kt
 * Project: Ganymede
 *
 * Author: Greg50100
 * Date: 28/09/2025
 *
 * Description:
 * Composable screen to display and simulate common logic gates (AND, OR, NOT, NAND, NOR, XOR, XNOR).
 * The component presents IEC/ANSI symbols, boolean expressions, and truth tables, and includes a
 * simple interactive simulator to toggle inputs and observe the resulting outputs.
 *
 * Repository: https://github.com/Greg50100/Ganymede
 */

package com.joviansapps.ganymede.ui.screens.ressources

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class LogicGate(
    val name: String,
    @param:DrawableRes val symbolIecRes: Int,
    @param:DrawableRes val symbolAnsiRes: Int,
    @param:DrawableRes val expression1: Int,
    @param:DrawableRes val expression2: Int,
    @param:DrawableRes val truthTableRes: Int,
    val calculate: (Boolean, Boolean) -> Boolean,
    val isUnary: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogicGatesScreen(modifier: Modifier = Modifier) {
    val gates = getLogicGates()
    val pagerState = rememberPagerState(pageCount = { gates.size })

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                GateInfoCard(gate = gates[page])
            }
        }

        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(gates.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun GateInfoCard(gate: LogicGate) {
    var inputA by remember { mutableStateOf(false) }
    var inputB by remember { mutableStateOf(false) }
    val outputQ = gate.calculate(inputA, inputB)

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = gate.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Symbol and Simulator
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LabeledOutlinedSection(label = "Symbole") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SymbolImage(label = "IEC", resourceId = gate.symbolIecRes)
                            SymbolImage(label = "ANSI", resourceId = gate.symbolAnsiRes)
                        }
                    }
                    LabeledOutlinedSection(label = "Simulateur") {
                        InputButton(label = "A", isChecked = inputA, onCheckedChange = { inputA = it })
                        if (!gate.isUnary) {
                            InputButton(label = "B", isChecked = inputB, onCheckedChange = { inputB = it })
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                        OutputDisplay(label = "Q", isActive = outputQ)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LabeledOutlinedSection(
                        label = "Exp Booléenne"
                    ) {
                        Image(
                            painter = painterResource(id = gate.expression1),
                            contentDescription = "Expression booléenne pour ${gate.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 60.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Image(
                            painter = painterResource(id = gate.expression2),
                            contentDescription = "Notations booléennes",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 60.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    LabeledOutlinedSection(
                        label = "Table de Vérité"
                    ) {
                        Image(
                            painter = painterResource(id = gate.truthTableRes),
                            contentDescription = "Table de vérité pour ${gate.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (gate.isUnary) 200.dp else 250.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledOutlinedSection(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.padding(top = 8.dp)) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
        Text(
            text = " $label ",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 12.dp, y = (-8).dp)
                .background(MaterialTheme.colorScheme.surface),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SymbolImage(label: String, @DrawableRes resourceId: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = "Symbole $label",
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}


@Composable
private fun InputButton(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
        Button(
            onClick = { onCheckedChange(!isChecked) },
            modifier = Modifier.size(30.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isChecked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = if (isChecked) "1" else "0",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun OutputDisplay(label: String, isActive: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isActive) "1" else "0",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getLogicGates(): List<LogicGate> {
    // Note: Replace R.drawable placeholders with your actual drawable resource IDs
    return listOf(
        LogicGate("AND", R.drawable.logic_gate_and_iec, R.drawable.logic_gate_and_ansi, R.drawable.and_equation_1,R.drawable.and_equation_2, R.drawable.logic_gate_and_truth_table, { a, b -> a && b }),
        LogicGate("OR", R.drawable.logic_gate_or_iec, R.drawable.logic_gate_or_ansi, R.drawable.or_equation_1, R.drawable.or_equation_2,R.drawable.logic_gate_or_truth_table, { a, b -> a || b }),
        LogicGate("NOT", R.drawable.logic_gate_not_iec, R.drawable.logic_gate_not_ansi, R.drawable.not_equation_1, R.drawable.not_equation_2, R.drawable.logic_gate_not_truth_table, { a, _ -> !a }, isUnary = true),
        LogicGate("NAND", R.drawable.logic_gate_nand_iec, R.drawable.logic_gate_nand_ansi, R.drawable.nand_equation_1, R.drawable.nand_equation_2, R.drawable.logic_gate_nand_truth_table, { a, b -> !(a && b) }),
        LogicGate("NOR", R.drawable.logic_gate_nor_iec, R.drawable.logic_gate_nor_ansi, R.drawable.nor_equation_1, R.drawable.nor_equation_2, R.drawable.logic_gate_nor_truth_table, { a, b -> !(a || b) }),
        LogicGate("XOR", R.drawable.logic_gate_xor_iec, R.drawable.logic_gate_xor_ansi, R.drawable.xor_equation_1, R.drawable.xor_equation_2, R.drawable.logic_gate_xor_truth_table, { a, b -> a xor b }),
        LogicGate("XNOR", R.drawable.logic_gate_xnor_iec, R.drawable.logic_gate_xnor_ansi, R.drawable.xnor_equation_1, R.drawable.xnor_equation_2, R.drawable.logic_gate_xnor_truth_table, { a, b -> !(a xor b) })
    )
}
