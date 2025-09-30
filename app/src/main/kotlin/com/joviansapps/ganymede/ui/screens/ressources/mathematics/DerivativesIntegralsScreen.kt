package com.joviansapps.ganymede.ui.screens.ressources.mathematics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import kotlinx.coroutines.launch

private data class Formula(val function: String, val result: String)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DerivativesIntegralsScreen() {
    val pages = listOf("Derivatives", "Integrals")
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    val formulas = mapOf(
        "Derivatives" to listOf(
            Formula("f(x) = c", "f'(x) = 0"),
            Formula("f(x) = xⁿ", "f'(x) = nxⁿ⁻¹"),
            Formula("f(x) = eˣ", "f'(x) = eˣ"),
            Formula("f(x) = ln(x)", "f'(x) = 1/x"),
            Formula("f(x) = sin(x)", "f'(x) = cos(x)"),
            Formula("f(x) = cos(x)", "f'(x) = -sin(x)")
        ),
        "Integrals" to listOf(
            Formula("∫ c dx", "cx + C"),
            Formula("∫ xⁿ dx", "xⁿ⁺¹/(n+1) + C"),
            Formula("∫ eˣ dx", "eˣ + C"),
            Formula("∫ 1/x dx", "ln|x| + C"),
            Formula("∫ sin(x) dx", "-cos(x) + C"),
            Formula("∫ cos(x) dx", "sin(x) + C")
        )
    )

    Scaffold { padding ->
        Column(Modifier.padding(padding)) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                pages.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                    )
                }
            }
            HorizontalPager(state = pagerState) { page ->
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(formulas[pages[page]] ?: emptyList()) { f ->
                        Text("${f.function}  =>  ${f.result}", fontFamily = FontFamily.Monospace, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}
