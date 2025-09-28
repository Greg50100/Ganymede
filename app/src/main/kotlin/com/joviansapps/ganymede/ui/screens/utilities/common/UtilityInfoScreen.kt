package com.joviansapps.ganymede.ui.screens.utilities.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.navigation.Dest
import com.joviansapps.ganymede.ui.screens.utilities.electronics.info.*
import com.joviansapps.ganymede.ui.screens.utilities.health.info.BmiCalculatorInfo
import com.joviansapps.ganymede.ui.screens.utilities.health.info.BmrCalculatorInfo
import com.joviansapps.ganymede.ui.screens.utilities.math.info.QuadraticEquationSolverInfo

@Composable
fun UtilityInfoScreen(utilityId: String?) {
    Box(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        when (utilityId) {
            // Electronics
            Dest.OhmsLawCalculator.route -> OhmsLawCalculatorInfo()
            Dest.VoltageDividerCalculator.route -> VoltageDividerCalculatorInfo()
            Dest.TimeConstantCalculator.route -> TimeConstantCalculatorInfo()
            Dest.Timer555Calculator.route -> Timer555CalculatorInfo()
            Dest.OpAmpCalculator.route -> OpAmpCalculatorInfo()
            Dest.TransformerCalculator.route -> TransformerCalculatorInfo()
            Dest.BjtBiasingCalculator.route -> BjtBiasingCalculatorInfo()

            // Health
            Dest.BmiCalculator.route -> BmiCalculatorInfo()
            Dest.BmrCalculator.route -> BmrCalculatorInfo()

            // Math
            Dest.QuadraticEquationSolver.route -> QuadraticEquationSolverInfo()

            // Fallback for utilities without info yet
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.info_not_available))
                }
            }
        }
    }
}

fun getUtilityRoutesWithInfo(): Set<String> {
    return setOf(
        // Electronics
        Dest.OhmsLawCalculator.route,
        Dest.VoltageDividerCalculator.route,
        Dest.TimeConstantCalculator.route,
        Dest.Timer555Calculator.route,
        Dest.OpAmpCalculator.route,
        Dest.TransformerCalculator.route,
        Dest.BjtBiasingCalculator.route,

        // Health
        Dest.BmiCalculator.route,
        Dest.BmrCalculator.route,

        // Math
        Dest.QuadraticEquationSolver.route
        // Add other utility routes here as you create their info screens
    )
}