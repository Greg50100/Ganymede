package com.joviansapps.ganymede.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.navigation.Dest

data class Searchable(
    val title: String,
    val route: String,
    val keywords: List<String> = emptyList()
)

@Composable
fun getSearchableList(): List<Searchable> {
    return listOf(
        // Main screens
        Searchable(stringResource(id = R.string.calculator_title), Dest.Calculator.route, listOf("calculator", "standard")),
        Searchable(stringResource(id = R.string.converter_title), Dest.Converter.route, listOf("conversion", "units")),
        Searchable(stringResource(id = R.string.graph_title), Dest.Graph.route, listOf("graphing", "functions", "maths")),
        Searchable(stringResource(id = R.string.utilities_title), Dest.Utilities.route, listOf("utilities", "tools")),

        // Electronics
        Searchable(stringResource(id = R.string.resistor_calculator_title), Dest.ResistorCalculator.route, listOf("resistance", "ohm", "color code")),
        Searchable(stringResource(id = R.string.inductance_calculator_title), Dest.InductanceCalculator.route, listOf("inductor", "henry", "coil")),
        Searchable(stringResource(id = R.string.ohms_law_calculator_title), Dest.OhmsLawCalculator.route, listOf("voltage", "current", "resistance", "power", "V=IR", "ohm's law")),
        Searchable(stringResource(id = R.string.battery_life_calculator_title), Dest.BatteryLifeCalculator.route, listOf("autonomy", "battery", "capacity")),

        // Health
        Searchable(stringResource(id = R.string.bmi_calculator_title), Dest.BmiCalculator.route, listOf("body mass index", "weight", "height", "bmi")),
        Searchable(stringResource(id = R.string.bmr_calculator_title), Dest.BmrCalculator.route, listOf("basal metabolic rate", "calories", "bmr")),
        Searchable(stringResource(id = R.string.body_fat_calculator_title), Dest.BodyFatCalculator.route, listOf("body fat", "fat mass", "bfp")),

        // Math
        Searchable(stringResource(id = R.string.quadratic_equation_solver_title), Dest.QuadraticEquationSolver.route, listOf("quadratic", "delta", "root", "equation")),
        Searchable(stringResource(id = R.string.gcd_lcm_calculator_title), Dest.GCDandLCMCalculator.route, listOf("gcd", "lcm", "greatest common divisor", "least common multiple")),
        Searchable(stringResource(id = R.string.percentage_calculator_title), Dest.PercentageCalculator.route, listOf("percentage", "discount", "increase")),

        // Physics
        Searchable(stringResource(id = R.string.free_fall_calculator_title), Dest.FreeFallCalculator.route, listOf("free fall", "gravity", "speed")),
        Searchable(stringResource(id = R.string.newtons_second_law_title), Dest.NewtonsSecondLawCalculator.route, listOf("force", "mass", "acceleration", "newton's second law")),

        // Chemistry
        Searchable(stringResource(id = R.string.molar_mass_calculator_title), Dest.MolarMassCalculator.route, listOf("molar mass", "chemistry", "mol")),

        // Date
        Searchable(stringResource(id = R.string.date_calculator_title), Dest.DateCalculator.route, listOf("duration", "day", "month", "year", "date calculation"))
    )
}
