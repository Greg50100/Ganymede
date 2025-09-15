package com.joviansapps.ganymede.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.graphing.GraphViewModel
import com.joviansapps.ganymede.ui.screens.calculator.CalculatorScreen
import com.joviansapps.ganymede.ui.screens.converter.ConverterScreen
import com.joviansapps.ganymede.ui.screens.graph.GraphScreen
import com.joviansapps.ganymede.ui.screens.home.HomeScreen
import com.joviansapps.ganymede.ui.screens.settings.SettingsScreen
import com.joviansapps.ganymede.ui.screens.utilities.UtilitiesScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.ElectronicCategoryScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.LedResistorCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.OhmsLawCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.ParallelSeriesCapacitorCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.ParallelSeriesResistorCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.TimeConstantCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.Timer555CalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.VoltageDividerCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.inductancecalculator.InductanceCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.resistorcalculator.ResistorCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.FilterCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.WireGaugeCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.EnergyCostCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.VoltageDropCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.health.BmiCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.health.HealthCategoryScreen
import com.joviansapps.ganymede.ui.screens.utilities.math.MathCategoryScreen
import com.joviansapps.ganymede.ui.screens.utilities.math.QuadraticEquationSolverScreen
import com.joviansapps.ganymede.ui.screens.utilities.physics.PhysicsCategoryScreen
import com.joviansapps.ganymede.ui.screens.utilities.physics.FreeFallCalculatorScreen
import com.joviansapps.ganymede.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(settingsVm: SettingsViewModel) {
    val nav = rememberNavController()
    val bottomItems = listOf(Dest.Home, Dest.Settings)

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            val current by nav.currentBackStackEntryAsState()
            val route = current?.destination?.route
            val isMainScreen = bottomItems.any { it.route == route }

            val titleText: String = when (route) {
                Dest.Home.route -> stringResource(R.string.home_label)
                Dest.Calculator.route -> stringResource(R.string.calculator_title)
                Dest.Converter.route -> stringResource(R.string.converter_title)
                Dest.Graph.route -> stringResource(R.string.graph_title)
                Dest.Utilities.route -> stringResource(R.string.utilities_title)
                Dest.Settings.route -> stringResource(R.string.settings_title)
                Dest.ElectronicsCategory.route -> stringResource(R.string.electronics_category_title)
                Dest.ResistorCalculator.route -> stringResource(R.string.resistor_calculator_title)
                Dest.InductanceCalculator.route -> stringResource(R.string.inductance_calculator_title)
                Dest.TimeConstantCalculator.route -> stringResource(R.string.time_constant_calculator_title)
                Dest.ParallelSeriesResistorCalculator.route -> stringResource(R.string.parallel_series_resistor_calculator_title)
                Dest.ParallelSeriesCapacitorCalculator.route -> stringResource(R.string.parallel_series_capacitor_calculator_title)
                Dest.OhmsLawCalculator.route -> stringResource(R.string.ohms_law_calculator_title)
                Dest.VoltageDividerCalculator.route -> stringResource(R.string.voltage_divider_calculator_title)
                Dest.LedResistorCalculator.route -> stringResource(R.string.led_resistor_calculator_title)
                Dest.Timer555Calculator.route -> stringResource(R.string.timer_555_astable_title)
                Dest.FilterCalculator.route -> stringResource(R.string.filter_calculator_title)
                Dest.HealthCategory.route -> stringResource(R.string.health_category_title)
                Dest.BmiCalculator.route -> stringResource(R.string.bmi_calculator_title)
                Dest.MathCategory.route -> stringResource(R.string.math_category_title)
                Dest.QuadraticEquationSolver.route -> stringResource(R.string.quadratic_equation_solver_title)
                Dest.PhysicsCategory.route -> stringResource(R.string.physics_category_title)
                Dest.FreeFallCalculator.route -> stringResource(R.string.free_fall_calculator_title)
                Dest.WireGaugeCalculator.route -> stringResource(R.string.wire_gauge_calculator_title)
                Dest.EnergyCostCalculator.route -> stringResource(R.string.energy_cost_calculator_title)
                Dest.VoltageDropCalculator.route -> stringResource(R.string.voltage_drop_calculator_title)

                else -> stringResource(R.string.app_name)
            }
            TopAppBar(
                title = { Text(text = titleText) },
                navigationIcon = {
                    if (!isMainScreen) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button_description)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                val current by nav.currentBackStackEntryAsState()
                bottomItems.forEach { dest ->
                    val labelRes = when (dest) {
                        Dest.Settings -> R.string.settings_title
                        else -> R.string.home_label
                    }
                    NavigationBarItem(
                        selected = current?.destination?.route == dest.route,
                        onClick = {
                            nav.navigate(dest.route) {
                                popUpTo(nav.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (dest) {
                                    Dest.Settings -> Icons.Default.Settings
                                    else -> Icons.Default.Home
                                },
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(labelRes)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Dest.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.Home.route) {
                HomeScreen(
                    onOpenCalculator = { nav.navigate(Dest.Calculator.route) },
                    onOpenConverter  = { nav.navigate(Dest.Converter.route) },
                    onOpenGraph      = { nav.navigate(Dest.Graph.route) },
                    onOpenUtilities  = { nav.navigate(Dest.Utilities.route) }
                )
            }
            composable(Dest.Calculator.route) {
                CalculatorScreen()
            }
            composable(Dest.Converter.route) {
                ConverterScreen()
            }
            composable(Dest.Settings.route) {
                SettingsScreen(vm = settingsVm)
            }
            composable(Dest.Graph.route) {
                val graphVm: GraphViewModel = viewModel()
                GraphScreen(graphViewModel = graphVm)
            }
            composable(Dest.Utilities.route) {
                UtilitiesScreen(
                    onOpenElectronics = { nav.navigate(Dest.ElectronicsCategory.route) },
                    onOpenHealth = { nav.navigate(Dest.HealthCategory.route) },
                    onOpenMath = { nav.navigate(Dest.MathCategory.route) },
                    onPhysics = { nav.navigate(Dest.PhysicsCategory.route) }
                )
            }
            composable(Dest.ElectronicsCategory.route) {
                ElectronicCategoryScreen(
                    onOpenResistorCalculator = { nav.navigate(Dest.ResistorCalculator.route) },
                    onOpenInductanceCalculator = { nav.navigate(Dest.InductanceCalculator.route) },
                    onOpenTimeConstantCalculator = { nav.navigate(Dest.TimeConstantCalculator.route) },
                    onOpenParallelSeriesResistorCalculator = { nav.navigate(Dest.ParallelSeriesResistorCalculator.route) },
                    onOpenParallelSeriesCapacitorCalculator = { nav.navigate(Dest.ParallelSeriesCapacitorCalculator.route) },
                    onOpenOhmsLawCalculator = { nav.navigate(Dest.OhmsLawCalculator.route) },
                    onOpenVoltageDividerCalculator = { nav.navigate(Dest.VoltageDividerCalculator.route) },
                    onOpenLedResistorCalculator = { nav.navigate(Dest.LedResistorCalculator.route) },
                    onOpenTimer555Calculator = { nav.navigate(Dest.Timer555Calculator.route) },
                    onOpenFilterCalculator = { nav.navigate(Dest.FilterCalculator.route) },
                    onOpenWireGaugeCalculator = { nav.navigate(Dest.WireGaugeCalculator.route) },
                    onOpenVoltageDropCalculator = { nav.navigate(Dest.VoltageDropCalculator.route) },
                    onOpenEnergyCostCalculator = { nav.navigate(Dest.EnergyCostCalculator.route) }
                )
            }
            composable(Dest.ResistorCalculator.route) { ResistorCalculatorScreen() }
            composable(Dest.InductanceCalculator.route) { InductanceCalculatorScreen() }
            composable(Dest.TimeConstantCalculator.route) { TimeConstantCalculatorScreen( ) }
            composable(Dest.ParallelSeriesResistorCalculator.route) { ParallelSeriesResistorCalculatorScreen() }
            composable(Dest.ParallelSeriesCapacitorCalculator.route) { ParallelSeriesCapacitorCalculatorScreen() }
            composable(Dest.OhmsLawCalculator.route) { OhmsLawCalculatorScreen() }
            composable(Dest.VoltageDividerCalculator.route) { VoltageDividerCalculatorScreen() }
            composable(Dest.LedResistorCalculator.route) { LedResistorCalculatorScreen() }
            composable(Dest.Timer555Calculator.route) { Timer555CalculatorScreen() }
            composable(Dest.FilterCalculator.route) { FilterCalculatorScreen() }
            composable(Dest.WireGaugeCalculator.route) { WireGaugeCalculatorScreen() }
            composable(Dest.VoltageDropCalculator.route) { VoltageDropCalculatorScreen() }
            composable(Dest.EnergyCostCalculator.route) { EnergyCostCalculatorScreen() }

            composable(Dest.HealthCategory.route) {
                HealthCategoryScreen(
                    onOpenBmiCalculator = { nav.navigate(Dest.BmiCalculator.route) }
                )
            }
            composable(Dest.BmiCalculator.route) {
                BmiCalculatorScreen()
            }
            composable(Dest.MathCategory.route) {
                MathCategoryScreen(
                    onOpenQuadraticEquationSolver = { nav.navigate(Dest.QuadraticEquationSolver.route) }
                )
            }
            composable(Dest.QuadraticEquationSolver.route) {
                QuadraticEquationSolverScreen()
            }
            composable(Dest.PhysicsCategory.route) {
                PhysicsCategoryScreen(
                    onOpenFreeFallCalculator = { nav.navigate(Dest.FreeFallCalculator.route) }
                )
            }
            composable(Dest.FreeFallCalculator.route) {
                FreeFallCalculatorScreen()
            }
        }
    }
}

