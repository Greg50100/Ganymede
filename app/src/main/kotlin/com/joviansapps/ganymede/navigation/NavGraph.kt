package com.joviansapps.ganymede.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.data.getSearchableList
import com.joviansapps.ganymede.graphing.GraphViewModel
import com.joviansapps.ganymede.ui.screens.calculator.CalculatorScreen
import com.joviansapps.ganymede.ui.screens.converter.ConverterScreen
import com.joviansapps.ganymede.ui.screens.graph.GraphScreen
import com.joviansapps.ganymede.ui.screens.home.HomeScreen
import com.joviansapps.ganymede.ui.screens.search.SearchScreen
import com.joviansapps.ganymede.ui.screens.settings.SettingsScreen
import com.joviansapps.ganymede.ui.screens.utilities.UtilitiesScreen
import com.joviansapps.ganymede.ui.screens.utilities.chemistry.ChemistryCategoryScreen
import com.joviansapps.ganymede.ui.screens.utilities.chemistry.MolarMassCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.common.UtilityInfoScreen
import com.joviansapps.ganymede.ui.screens.utilities.common.getUtilityRoutesWithInfo
import com.joviansapps.ganymede.ui.screens.utilities.date.DateCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.date.DateCategoryScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.*
import com.joviansapps.ganymede.ui.screens.utilities.electronics.inductancecalculator.InductanceCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.resistorcalculator.ResistorCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.health.*
import com.joviansapps.ganymede.ui.screens.utilities.math.GcdLcmCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.math.MathCategoryScreen
import com.joviansapps.ganymede.ui.screens.utilities.math.PercentageCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.math.QuadraticEquationSolverScreen
import com.joviansapps.ganymede.ui.screens.utilities.physics.*
import com.joviansapps.ganymede.viewmodel.SearchViewModel
import com.joviansapps.ganymede.viewmodel.SettingsViewModel

import com.joviansapps.ganymede.ui.screens.ressources.RessourcesScreen
import com.joviansapps.ganymede.ui.screens.ressources.SIUnitsSystem.SIConstantsScreen
import com.joviansapps.ganymede.ui.screens.ressources.SIUnitsSystem.SIUnitsScreen
import com.joviansapps.ganymede.ui.screens.ressources.SIUnitsSystem.DerivedSIUnitsScreen
import com.joviansapps.ganymede.ui.screens.ressources.SIUnitsSystem.SIPrefixesScreen
import com.joviansapps.ganymede.ui.screens.ressources.SIUnitsSystem.SIUnitsSystemCategoryScreen
import com.joviansapps.ganymede.ui.screens.ressources.generalreferences.GeneralReferencesCategoryScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.ComputingCategoryScreen
import com.joviansapps.ganymede.ui.screens.ressources.chemistryphysics.ChemistryPhysicsCategoryScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.ASCIITablesScreen
import com.joviansapps.ganymede.ui.screens.ressources.generalreferences.GreekAlphabetScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.LogicGatesScreen
import com.joviansapps.ganymede.ui.screens.ressources.chemistryphysics.PeriodicTableScreen
import com.joviansapps.ganymede.ui.screens.ressources.electronics.ElectronicSymbolsScreen
import com.joviansapps.ganymede.ui.screens.ressources.electronics.ComponentPinoutsScreen
import com.joviansapps.ganymede.ui.screens.ressources.electronics.WireGaugeScreen
import com.joviansapps.ganymede.ui.screens.ressources.electronics.BatteryTechScreen
import com.joviansapps.ganymede.ui.screens.ressources.electronics.ComponentPackagesScreen
import com.joviansapps.ganymede.ui.screens.ressources.electronics.ConnectorsPinoutsScreen
import com.joviansapps.ganymede.ui.screens.ressources.generalreferences.MorseCodeScreen
import com.joviansapps.ganymede.ui.screens.ressources.generalreferences.NatoAlphabetScreen
import com.joviansapps.ganymede.ui.screens.ressources.generalreferences.RomanNumeralsScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.GitCheatSheetScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.HttpCodesScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.LatexSyntaxScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.MarkdownSyntaxScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.RegexCheatSheetScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.TcpUdpPortsScreen
import com.joviansapps.ganymede.ui.screens.ressources.computing.UsefulCommandsScreen
import com.joviansapps.ganymede.ui.screens.ressources.chemistryphysics.ElectromagneticSpectrumScreen
import com.joviansapps.ganymede.ui.screens.ressources.chemistryphysics.MaterialPropertiesScreen
import com.joviansapps.ganymede.ui.screens.ressources.chemistryphysics.RedoxPotentialScreen
import com.joviansapps.ganymede.ui.screens.ressources.mathematics.DerivativesIntegralsScreen
import com.joviansapps.ganymede.ui.screens.ressources.mathematics.LaplaceTransformsScreen
import com.joviansapps.ganymede.ui.screens.ressources.mathematics.TrigIdentitiesScreen
import com.joviansapps.ganymede.ui.screens.ressources.mathematics.MathematicsCategoryScreen
import com.joviansapps.ganymede.ui.screens.ressources.mechanics.BearingDesignationScreen
import com.joviansapps.ganymede.ui.screens.ressources.mechanics.TappingDrillScreen
import com.joviansapps.ganymede.ui.screens.ressources.mechanics.MechanicsCategoryScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(settingsVm: SettingsViewModel) {
    val settingsState by settingsVm.uiState.collectAsState()
    val nav = rememberNavController()
    val bottomItems = listOf(Dest.Home, Dest.Search, Dest.Settings)
    // CORRECTED: Using the new function from UtilityInfoProvider.kt
    val utilityRoutesWithInfo = getUtilityRoutesWithInfo()

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            val current by nav.currentBackStackEntryAsState()
            val route = current?.destination?.route
            val isMainScreen = bottomItems.any { it.route == route }

            val titleText: String = when (route) {
                Dest.UtilityInfo.route -> stringResource(R.string.formulas_title)
                Dest.Home.route -> stringResource(R.string.home_label)
                Dest.Calculator.route -> stringResource(R.string.calculator_title)
                Dest.Converter.route -> stringResource(R.string.converter_title)
                Dest.Graph.route -> stringResource(R.string.graph_title)
                Dest.Utilities.route -> stringResource(R.string.utilities_title)
                Dest.Search.route -> stringResource(R.string.search_label)
                Dest.Settings.route -> stringResource(R.string.settings_title)
                Dest.Ressources.route -> stringResource(R.string.ressources_title)
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
                Dest.WireGaugeCalculator.route -> stringResource(R.string.wire_gauge_calculator_title)
                Dest.EnergyCostCalculator.route -> stringResource(R.string.energy_cost_calculator_title)
                Dest.VoltageDropCalculator.route -> stringResource(R.string.voltage_drop_calculator_title)
                Dest.BatteryLifeCalculator.route -> stringResource(R.string.battery_life_calculator_title)
                Dest.WavelengthFrequencyCalculator.route -> stringResource(R.string.wavelength_calculator_title)
                Dest.ZenerDiodeCalculator.route -> stringResource(R.string.zener_diode_calculator_title)
                Dest.ReactanceCalculator.route -> stringResource(R.string.reactance_calculator_title)
                Dest.PowerCalculator.route -> stringResource(R.string.power_calculator_title)
                Dest.WheatstoneBridgeCalculator.route -> stringResource(R.string.wheatstone_bridge_calculator_title)
                Dest.OpAmpCalculator.route -> stringResource(R.string.op_amp_calculator_title)
                Dest.CapacitorCodeCalculator.route -> stringResource(R.string.capacitor_code_calculator_title)
                Dest.SmdResistorCalculator.route -> stringResource(R.string.smd_resistor_calculator_title)
                Dest.PowerAcCalculator.route -> stringResource(R.string.ac_power_calculator_title)
                Dest.DeltaStarConverter.route -> stringResource(R.string.delta_star_converter_title)
                Dest.ComponentToleranceCalculator.route -> stringResource(R.string.component_tolerance_calculator_title)
                Dest.StandardValueCalculator.route -> stringResource(R.string.standard_value_calculator_title)
                Dest.RlcImpedanceCalculator.route -> stringResource(R.string.rlc_impedance_calculator_title)
                Dest.RlcResonantCircuitCalculator.route -> stringResource(R.string.rlc_resonant_circuit_calculator_title)
                Dest.PassiveFilterCalculator.route -> stringResource(R.string.passive_filter_calculator_title)
                Dest.RmsCalculator.route -> stringResource(R.string.rms_calculator_title)
                Dest.BjtBiasingCalculator.route -> stringResource(R.string.bjt_biasing_calculator_title)
                Dest.TransformerCalculator.route -> stringResource(R.string.transformer_calculator_title)
                Dest.HealthCategory.route -> stringResource(R.string.health_category_title)
                Dest.BmiCalculator.route -> stringResource(R.string.bmi_calculator_title)
                Dest.BmrCalculator.route -> stringResource(R.string.bmr_calculator_title)
                Dest.BodyFatCalculator.route -> stringResource(R.string.body_fat_calculator_title)
                Dest.MathCategory.route -> stringResource(R.string.math_category_title)
                Dest.QuadraticEquationSolver.route -> stringResource(R.string.quadratic_equation_solver_title)
                Dest.GCDandLCMCalculator.route -> stringResource(R.string.gcd_lcm_calculator_title)
                Dest.PercentageCalculator.route -> stringResource(R.string.percentage_calculator_title)
                Dest.PhysicsCategory.route -> stringResource(R.string.physics_category_title)
                Dest.FreeFallCalculator.route -> stringResource(R.string.free_fall_calculator_title)
                Dest.NewtonsSecondLawCalculator.route -> stringResource(R.string.newtons_second_law_title)
                Dest.ProjectileMotionCalculator.route -> stringResource(R.string.projectile_motion_calculator_title)
                Dest.IdealGasLawCalculator.route -> stringResource(R.string.ideal_gas_law_calculator_title)
                Dest.BernoulliCalculator.route -> stringResource(R.string.bernoulli_calculator_title)
                Dest.ChemistryCategory.route -> stringResource(R.string.chemistry_category_title)
                Dest.MolarMassCalculator.route -> stringResource(R.string.molar_mass_calculator_title)
                Dest.SIPrefixes.route -> stringResource(R.string.si_prefixes_title)
                Dest.SIConstants.route -> stringResource(R.string.si_constants_title)
                Dest.SIUnits.route -> stringResource(R.string.si_units_title)
                Dest.SIDerivedUnits.route -> stringResource(R.string.si_derived_units_title)
                // Ressources électroniques
                Dest.ElectronicSymbols.route -> stringResource(R.string.electronic_symbols)
                Dest.ComponentPinouts.route -> stringResource(R.string.component_pinouts)
                Dest.WireGauge.route -> stringResource(R.string.wire_gauge_reference)
                Dest.GreekAlphabet.route -> "Alphabet Grec"
                Dest.LogicGates.route -> "Portes Logiques"
                Dest.PeriodicTable.route -> "Tableau Périodique"
                Dest.BatteryTech.route -> "Technologies de batterie"
                Dest.ComponentPackages.route -> "Boîtiers de composants"
                Dest.ConnectorsPinouts.route -> "Brochage des connecteurs"
                Dest.MorseCode.route -> "Code Morse"
                Dest.NatoAlphabet.route -> "Alphabet de l'OTAN"
                Dest.RomanNumerals.route -> "Chiffres Romains"
                Dest.GitCheatSheet.route -> "Git Cheat Sheet"
                Dest.HttpCodes.route -> "Codes HTTP"
                Dest.LatexSyntax.route -> "Syntaxe LaTeX"
                Dest.MarkdownSyntax.route -> "Syntaxe Markdown"
                Dest.RegexCheatSheet.route -> "Regex Cheat Sheet"
                Dest.TcpUdpPorts.route -> "Ports TCP/UDP"
                Dest.UsefulCommands.route -> "Commandes Utiles"
                Dest.ElectromagneticSpectrum.route -> "Spectre Électromagnétique"
                Dest.MaterialProperties.route -> "Propriétés des Matériaux"
                Dest.RedoxPotential.route -> "Potentiel Redox"
                Dest.DerivativesIntegrals.route -> "Dérivées et Intégrales"
                Dest.LaplaceTransforms.route -> "Transformées de Laplace"
                Dest.TrigIdentities.route -> "Identités Trigonométriques"
                Dest.BearingDesignation.route -> "Désignation des Roulements"
                Dest.TappingDrill.route -> "Perçage de Taraudage"
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
                actions = {
                    // Show the f(x) icon only on specified utility screens
                    if (route in utilityRoutesWithInfo) {
                        IconButton(onClick = {
                            // The route is guaranteed to be non-null here
                            nav.navigate(Dest.UtilityInfo.createRoute(route!!))
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_functions),
                                contentDescription = stringResource(R.string.info_button_description)
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
                    val (labelRes, icon) = when (dest) {
                        Dest.Settings -> Pair(R.string.settings_title, Icons.Default.Settings)
                        Dest.Search -> Pair(R.string.search_label, Icons.Default.Search)
                        else -> Pair(R.string.home_label, Icons.Default.Home)
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
                                imageVector = icon,
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
                    onOpenConverter = { nav.navigate(Dest.Converter.route) },
                    onOpenGraph = { nav.navigate(Dest.Graph.route) },
                    onOpenUtilities = { nav.navigate(Dest.Utilities.route) },
                    onOpenRessources = { nav.navigate(Dest.Ressources.route) }
                )
            }
            composable(Dest.Calculator.route) {
                CalculatorScreen(hapticFeedbackEnabled = settingsState.hapticFeedbackEnabled)
            }
            composable(Dest.Converter.route) {
                ConverterScreen()
            }
            composable(Dest.Settings.route) {
                SettingsScreen(vm = settingsVm)
            }
            composable(Dest.Search.route) {
                val searchViewModel: SearchViewModel = viewModel()
                val searchableList = getSearchableList()

                LaunchedEffect(searchableList) {
                    searchViewModel.setSearchableItems(searchableList)
                }

                SearchScreen(
                    onNavigate = { route -> nav.navigate(route) },
                    searchViewModel = searchViewModel
                )
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
                    onOpenPhysics = { nav.navigate(Dest.PhysicsCategory.route) },
                    onOpenDate = { nav.navigate(Dest.DateCategory.route) },
                    onOpenChemistry = { nav.navigate(Dest.ChemistryCategory.route) }
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
                    onOpenEnergyCostCalculator = { nav.navigate(Dest.EnergyCostCalculator.route) },
                    onOpenBatteryLifeCalculator = { nav.navigate(Dest.BatteryLifeCalculator.route) },
                    onOpenWavelengthFrequencyCalculator = { nav.navigate(Dest.WavelengthFrequencyCalculator.route) },
                    onOpenZenerDiodeCalculator = { nav.navigate(Dest.ZenerDiodeCalculator.route) },
                    onOpenReactanceCalculator = { nav.navigate(Dest.ReactanceCalculator.route) },
                    onOpenPowerCalculator = { nav.navigate(Dest.PowerCalculator.route) },
                    onOpenWheatstoneBridgeCalculator = { nav.navigate(Dest.WheatstoneBridgeCalculator.route) },
                    onOpenOpAmpCalculator = { nav.navigate(Dest.OpAmpCalculator.route) },
                    onOpenCapacitorCodeCalculator = { nav.navigate(Dest.CapacitorCodeCalculator.route) },
                    onOpenSmdResistorCalculator = { nav.navigate("smd_resistor_calculator") },
                    onOpenPowerAcCalculator = { nav.navigate("power_ac_calculator") },
                    onOpenDeltaStarConverter = { nav.navigate("delta_star_converter") },
                    onOpenComponentToleranceCalculator = { nav.navigate("component_tolerance_calculator") },
                    onOpenStandardValueCalculator = { nav.navigate("standard_value_calculator") },
                    onOpenRlcImpedanceCalculator = { nav.navigate("rlc_impedance_calculator") },
                    onOpenRlcResonantCircuitCalculator = { nav.navigate("rlc_resonant_circuit_calculator") },
                    onOpenPassiveFilterCalculator = { nav.navigate(Dest.PassiveFilterCalculator.route) },
                    onOpenRmsCalculator = { nav.navigate("rms_calculator") },
                    onOpenBjtBiasingCalculator = { nav.navigate("bjt_biasing_calculator") },
                    onOpenTransformerCalculator = { nav.navigate("transformer_calculator") }
                )
            }
            composable(Dest.ResistorCalculator.route) { ResistorCalculatorScreen() }
            composable(Dest.InductanceCalculator.route) { InductanceCalculatorScreen() }
            composable(Dest.TimeConstantCalculator.route) { TimeConstantCalculatorScreen() }
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
            composable(Dest.BatteryLifeCalculator.route) { BatteryLifeCalculatorScreen() }
            composable(Dest.WavelengthFrequencyCalculator.route) { WavelengthFrequencyCalculatorScreen() }
            composable(Dest.ZenerDiodeCalculator.route) { ZenerDiodeCalculatorScreen() }
            composable(Dest.ReactanceCalculator.route) { ReactanceCalculatorScreen() }
            composable(Dest.PowerCalculator.route) { PowerCalculatorScreen() }
            composable(Dest.WheatstoneBridgeCalculator.route) { WheatstoneBridgeCalculatorScreen() }
            composable(Dest.OpAmpCalculator.route) { OpAmpCalculatorScreen() }
            composable(Dest.CapacitorCodeCalculator.route) { CapacitorCodeCalculatorScreen() }
            composable(Dest.SmdResistorCalculator.route) { SmdResistorCalculatorScreen() }
            composable(Dest.PowerAcCalculator.route) { PowerAcCalculatorScreen() }
            composable(Dest.DeltaStarConverter.route) { DeltaStarConverterScreen() }
            composable(Dest.ComponentToleranceCalculator.route) { ComponentToleranceCalculatorScreen() }
            composable(Dest.StandardValueCalculator.route) { StandardValueCalculatorScreen() }
            composable(Dest.RlcImpedanceCalculator.route) { RlcImpedanceCalculatorScreen() }
            composable(Dest.RlcResonantCircuitCalculator.route) { RlcResonantCircuitCalculatorScreen() }
            composable(Dest.PassiveFilterCalculator.route) { PassiveFilterCalculatorScreen() }
            composable(Dest.RmsCalculator.route) { RmsCalculatorScreen() }
            composable(Dest.BjtBiasingCalculator.route) { BjtBiasingCalculatorScreen() }
            composable(Dest.TransformerCalculator.route) { TransformerCalculatorScreen() }


            composable(Dest.HealthCategory.route) {
                HealthCategoryScreen(
                    onOpenBmiCalculator = { nav.navigate(Dest.BmiCalculator.route) },
                    onOpenBmrCalculator = { nav.navigate(Dest.BmrCalculator.route) },
                    onOpenBodyFatCalculator = { nav.navigate(Dest.BodyFatCalculator.route) }
                )
            }
            composable(Dest.BmiCalculator.route) { BmiCalculatorScreen() }
            composable(Dest.BmrCalculator.route) { BmrCalculatorScreen() }
            composable(Dest.BodyFatCalculator.route) { BodyFatCalculatorScreen() }

            composable(Dest.MathCategory.route) {
                MathCategoryScreen(
                    onOpenQuadraticEquationSolver = { nav.navigate(Dest.QuadraticEquationSolver.route) },
                    onOpenGCDandLCMCalculator = { nav.navigate(Dest.GCDandLCMCalculator.route) },
                    onOpenPercentageCalculator = { nav.navigate(Dest.PercentageCalculator.route) }
                )
            }
            composable(Dest.QuadraticEquationSolver.route) { QuadraticEquationSolverScreen() }
            composable(Dest.PercentageCalculator.route) { PercentageCalculatorScreen() }
            composable(Dest.GCDandLCMCalculator.route) { GcdLcmCalculatorScreen() }

            composable(Dest.PhysicsCategory.route) {
                PhysicsCategoryScreen(
                    onOpenFreeFallCalculator = { nav.navigate(Dest.FreeFallCalculator.route) },
                    onOpenNewtonsSecondLawCalculator = { nav.navigate(Dest.NewtonsSecondLawCalculator.route) },
                    onOpenProjectileMotionCalculator = { nav.navigate(Dest.ProjectileMotionCalculator.route) },
                    onOpenIdealGasLawCalculator = { nav.navigate(Dest.IdealGasLawCalculator.route) },
                    onOpenBernoulliCalculator = { nav.navigate(Dest.BernoulliCalculator.route) }
                )
            }
            composable(Dest.FreeFallCalculator.route) { FreeFallCalculatorScreen() }
            composable(Dest.NewtonsSecondLawCalculator.route) { NewtonsSecondLawCalculatorScreen() }
            composable(Dest.ProjectileMotionCalculator.route) { ProjectileMotionCalculatorScreen() }
            composable(Dest.IdealGasLawCalculator.route) { IdealGasLawCalculatorScreen() }
            composable(Dest.BernoulliCalculator.route) { BernoulliCalculatorScreen() }


            composable(Dest.DateCategory.route) {
                DateCategoryScreen(
                    onOpenDateCalculator = { nav.navigate(Dest.DateCalculator.route) }
                )
            }
            composable(Dest.DateCalculator.route) {
                DateCalculatorScreen()
            }

            composable(Dest.ChemistryCategory.route) {
                ChemistryCategoryScreen(
                    onOpenMolarMassCalculator = { nav.navigate(Dest.MolarMassCalculator.route) }
                )
            }
            composable(Dest.MolarMassCalculator.route) {
                MolarMassCalculatorScreen()
            }

            // Mathematics category screen (registered to avoid missing destination crash)
            composable(Dest.Mathematics.route) {
                MathematicsCategoryScreen(
                    onOpenDerivativesIntegrals = { nav.navigate(Dest.DerivativesIntegrals.route) },
                    onOpenLaplaceTransforms = { nav.navigate(Dest.LaplaceTransforms.route) },
                    onOpenTrigIdentities = { nav.navigate(Dest.TrigIdentities.route) }
                )
            }

            composable(
                route = Dest.UtilityInfo.route,
                arguments = listOf(navArgument("utilityId") { type = NavType.StringType })
            ) { backStackEntry ->
                UtilityInfoScreen(utilityId = backStackEntry.arguments?.getString("utilityId"))
            }

            composable(Dest.Ressources.route) {
                RessourcesScreen(
                    onOpenSIUnitsSystem = { nav.navigate(Dest.SIUnitsSystem.route) },
                    onOpenElectronics = { nav.navigate(Dest.RessourcesElectronicsCategory.route) },
                    onOpenGeneralReferences = { nav.navigate(Dest.GeneralReferences.route) },
                    onOpenChemistryPhysics = { nav.navigate(Dest.ChemistryPhysics.route) },
                    onOpenComputing = { nav.navigate(Dest.Computing.route) },
                    onOpenMathematics = { nav.navigate(Dest.Mathematics.route) },
                    onOpenMechanics = { nav.navigate(Dest.Mechanics.route) }
                )
            }

            composable(Dest.SIUnitsSystem.route) {
                SIUnitsSystemCategoryScreen(
                    onOpenSIPrefixes = { nav.navigate(Dest.SIPrefixes.route) },
                    onOpenSIConstants = { nav.navigate(Dest.SIConstants.route) },
                    onOpenSIUnits = { nav.navigate(Dest.SIUnits.route) },
                    onOpenSIDerivedUnits = { nav.navigate(Dest.SIDerivedUnits.route) }
                )
            }
            // Category écran pour ressources électroniques
            composable(Dest.RessourcesElectronicsCategory.route) {
                com.joviansapps.ganymede.ui.screens.ressources.electronics.ElectronicsResourcesCategoryScreen(
                    onOpenElectronicSymbols = { nav.navigate(Dest.ElectronicSymbols.route) },
                    onOpenComponentPinouts = { nav.navigate(Dest.ComponentPinouts.route) },
                    onOpenWireGauge = { nav.navigate(Dest.WireGauge.route) },
                    onOpenBatteryTech = { nav.navigate(Dest.BatteryTech.route) },
                    onOpenComponentPackages = { nav.navigate(Dest.ComponentPackages.route) },
                    onOpenConnectorsPinouts = { nav.navigate(Dest.ConnectorsPinouts.route) }
                )
            }
            composable(Dest.GeneralReferences.route) {
                GeneralReferencesCategoryScreen(
                    onOpenGreekAlphabet = { nav.navigate(Dest.GreekAlphabet.route) },
                    onOpenMorseCode = { nav.navigate(Dest.MorseCode.route) },
                    onOpenNatoAlphabet = { nav.navigate(Dest.NatoAlphabet.route) },
                    onOpenRomanNumerals = { nav.navigate(Dest.RomanNumerals.route) }
                )
            }
            composable(Dest.Computing.route) {
                ComputingCategoryScreen(
                    onOpenASCIITables = { nav.navigate(Dest.ASCIITables.route) },
                    onOpenLogicGates = { nav.navigate(Dest.LogicGates.route) },
                    onOpenGitCheatSheet = { nav.navigate(Dest.GitCheatSheet.route) },
                    onOpenHttpCodes = { nav.navigate(Dest.HttpCodes.route) },
                    onOpenLatexSyntax = { nav.navigate(Dest.LatexSyntax.route) },
                    onOpenMarkdownSyntax = { nav.navigate(Dest.MarkdownSyntax.route) },
                    onOpenRegexCheatSheet = { nav.navigate(Dest.RegexCheatSheet.route) },
                    onOpenTcpUdpPorts = { nav.navigate(Dest.TcpUdpPorts.route) },
                    onOpenUsefulCommands = { nav.navigate(Dest.UsefulCommands.route) }
                )
            }
            composable(Dest.ChemistryPhysics.route) {
                ChemistryPhysicsCategoryScreen(
                    onOpenPeriodicTable = { nav.navigate(Dest.PeriodicTable.route) },
                    onOpenElectromagneticSpectrum = { nav.navigate(Dest.ElectromagneticSpectrum.route) },
                    onOpenMaterialProperties = { nav.navigate(Dest.MaterialProperties.route) },
                    onOpenRedoxPotential = { nav.navigate(Dest.RedoxPotential.route) },
                    modifier = Modifier
                )
            }

            // Mechanics category screen
            composable(Dest.Mechanics.route) {
                MechanicsCategoryScreen(
                    onOpenBearingDesignation = { nav.navigate(Dest.BearingDesignation.route) },
                    onOpenTappingDrill = { nav.navigate(Dest.TappingDrill.route) }
                )
            }

            // Individual resource screens
            composable(Dest.SIPrefixes.route) { SIPrefixesScreen() }
            composable(Dest.SIConstants.route) { SIConstantsScreen() }
            composable(Dest.SIUnits.route) { SIUnitsScreen() }
            composable(Dest.SIDerivedUnits.route) { DerivedSIUnitsScreen() }
            composable(Dest.GreekAlphabet.route) { GreekAlphabetScreen() }
            composable(Dest.LogicGates.route) { LogicGatesScreen() }
            composable(Dest.PeriodicTable.route) { PeriodicTableScreen() }
            composable(Dest.ASCIITables.route) { ASCIITablesScreen() }
            // Ressources électroniques (nouveaux)
            composable(Dest.ElectronicSymbols.route) { ElectronicSymbolsScreen()
            }
            composable(Dest.ComponentPinouts.route) {
                ComponentPinoutsScreen()
            }
            composable(Dest.WireGauge.route) {
                WireGaugeScreen()
            }
            composable(Dest.BatteryTech.route) { BatteryTechScreen() }
            composable(Dest.ComponentPackages.route) { ComponentPackagesScreen() }
            composable(Dest.ConnectorsPinouts.route) { ConnectorsPinoutsScreen() }
            composable(Dest.MorseCode.route) { MorseCodeScreen() }
            composable(Dest.NatoAlphabet.route) { NatoAlphabetScreen() }
            composable(Dest.RomanNumerals.route) { RomanNumeralsScreen() }
            composable(Dest.GitCheatSheet.route) { GitCheatSheetScreen() }
            composable(Dest.HttpCodes.route) { HttpCodesScreen() }
            composable(Dest.LatexSyntax.route) { LatexSyntaxScreen() }
            composable(Dest.MarkdownSyntax.route) { MarkdownSyntaxScreen() }
            composable(Dest.RegexCheatSheet.route) { RegexCheatSheetScreen() }
            composable(Dest.TcpUdpPorts.route) { TcpUdpPortsScreen() }
            composable(Dest.UsefulCommands.route) { UsefulCommandsScreen() }
            composable(Dest.ElectromagneticSpectrum.route) { ElectromagneticSpectrumScreen() }
            composable(Dest.MaterialProperties.route) { MaterialPropertiesScreen() }
            composable(Dest.RedoxPotential.route) { RedoxPotentialScreen() }
            composable(Dest.DerivativesIntegrals.route) { DerivativesIntegralsScreen() }
            composable(Dest.LaplaceTransforms.route) { LaplaceTransformsScreen() }
            composable(Dest.TrigIdentities.route) { TrigIdentitiesScreen() }
            composable(Dest.BearingDesignation.route) { BearingDesignationScreen() }
            composable(Dest.TappingDrill.route) { TappingDrillScreen() }
        }
    }
}