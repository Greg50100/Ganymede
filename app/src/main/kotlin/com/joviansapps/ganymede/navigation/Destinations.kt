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
    data object FilterCalculator : Dest("filter_calculator")
    data object WireGaugeCalculator : Dest("wire_gauge_calculator")
    data object VoltageDropCalculator : Dest("voltage_drop_calculator")
    data object EnergyCostCalculator : Dest("energy_cost_calculator")
    data object BatteryLifeCalculator : Dest("battery_life_calculator")
    data object WavelengthFrequencyCalculator : Dest("wavelength_frequency_calculator")
    data object ZenerDiodeCalculator : Dest("zener_diode_calculator")
    data object ReactanceCalculator : Dest("reactance_calculator")
    data object PowerCalculator : Dest("power_calculator")
    data object WheatstoneBridgeCalculator : Dest("wheatstone_bridge_calculator")
    data object OpAmpCalculator : Dest("op_amp_calculator")
    // AJOUTÉ
    data object CapacitorCodeCalculator : Dest("capacitor_code_calculator")

    // Newly added electronics destinations
    data object SmdResistorCalculator : Dest("smd_resistor_calculator")
    data object PowerAcCalculator : Dest("power_ac_calculator")
    data object DeltaStarConverter : Dest("delta_star_converter")
    data object ComponentToleranceCalculator : Dest("component_tolerance_calculator")
    data object StandardValueCalculator : Dest("standard_value_calculator")
    data object RlcImpedanceCalculator : Dest("rlc_impedance_calculator")
    data object RlcResonantCircuitCalculator : Dest("rlc_resonant_circuit_calculator")
    data object PassiveFilterCalculator : Dest("passive_filter_calculator")
    data object RmsCalculator : Dest("rms_calculator")
    data object BjtBiasingCalculator : Dest("bjt_biasing_calculator")
    data object TransformerCalculator : Dest("transformer_calculator")


    // Health Utilities
    data object HealthCategory   : Dest("health_category")
    data object BmiCalculator    : Dest("bmi_calculator")
    data object BmrCalculator    : Dest("bmr_calculator")
    data object BodyFatCalculator : Dest("body_fat_calculator")

    // Math Utilities
    data object MathCategory     : Dest("math_category")
    data object QuadraticEquationSolver : Dest("quadratic_solver")
    data object PercentageCalculator : Dest("percentage_calculator")
    data object GCDandLCMCalculator : Dest("gcd_lcm_calculator")

    // Physics Utilities
    data object PhysicsCategory  : Dest("physics_category")
    data object FreeFallCalculator : Dest("free_fall_calculator")
    data object NewtonsSecondLawCalculator : Dest("newtons_second_law_calculator")
    data object ProjectileMotionCalculator : Dest("projectile_motion_calculator")
    data object IdealGasLawCalculator : Dest("ideal_gas_law_calculator")
    data object BernoulliCalculator : Dest("bernoulli_calculator")


    // Date & Time Utilities
    data object DateCategory     : Dest("date_category")
    data object DateCalculator   : Dest("date_calculator")

    data object ChemistryCategory : Dest("chemistry_category")
    data object MolarMassCalculator : Dest("molar_mass_calculator")

}
