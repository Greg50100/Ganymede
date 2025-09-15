package com.joviansapps.ganymede.navigation

/**
 * Defines all the possible navigation destinations in the app.
 * Using a sealed class ensures type safety and makes it easy to manage routes.
 */
sealed class Dest(val route: String) {
    data object Home             : Dest("home")
    data object Calculator       : Dest("calculator")
    data object Converter        : Dest("converter")
    data object Graph            : Dest("graph")
    data object Utilities        : Dest("utilities")
    data object Settings         : Dest("settings")

    // Electronics Utilities
    data object ElectronicsCategory : Dest("electronics_category")
    data object ResistorCalculator : Dest("resistor_calculator")
    data object InductanceCalculator : Dest("inductance_calculator")
    data object TimeConstantCalculator : Dest("time_constant_calculator")
    data object ParallelSeriesResistorCalculator : Dest("parallel_series_resistor_calculator")
    data object ParallelSeriesCapacitorCalculator : Dest("parallel_series_capacitor_calculator")
    data object OhmsLawCalculator : Dest("ohms_law_calculator")
    data object VoltageDividerCalculator : Dest("voltage_divider_calculator")
    data object LedResistorCalculator : Dest("led_resistor_calculator")
    data object Timer555Calculator : Dest("timer_555_calculator")

    // Health Utilities
    data object HealthCategory   : Dest("health_category")
    data object BmiCalculator    : Dest("bmi_calculator")

    // Math Utilities
    data object MathCategory     : Dest("math_category")
    data object QuadraticEquationSolver : Dest("quadratic_solver")
}
