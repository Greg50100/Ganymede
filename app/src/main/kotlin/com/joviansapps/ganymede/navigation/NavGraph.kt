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
import com.joviansapps.ganymede.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.joviansapps.ganymede.ui.screens.calculator.CalculatorScreen
import com.joviansapps.ganymede.ui.screens.converter.ConverterScreen
import com.joviansapps.ganymede.ui.screens.home.HomeScreen
import com.joviansapps.ganymede.ui.screens.settings.SettingsScreen
import com.joviansapps.ganymede.ui.screens.graph.GraphScreen
import com.joviansapps.ganymede.ui.screens.utilities.UtilitiesScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.ElectronicCategoryScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.TimeConstantCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.inductancecalculator.InductanceCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.resistorcalculator.ResistorCalculatorScreen
import com.joviansapps.ganymede.viewmodel.SettingsViewModel


sealed class Dest(val route: String) {
    data object Home             : Dest("home")
    data object Calculator       : Dest("calculator")
    data object Converter        : Dest("converter")
    data object Graph            : Dest("graph")
    data object Utilities        : Dest("utilities")
    data object Settings         : Dest("settings")
    data object ElectronicsCategory : Dest("electronics_category")
    data object ResistorCalculator : Dest("resistor_calculator")
    data object InductanceCalculator : Dest("inductance_calculator")
    data object TimeConstantCalculator : Dest("time_constant_calculator")
}

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
                // Pour ajuster la hauteur, décommentez et modifiez la ligne suivante :
                // modifier = Modifier.height(80.dp),
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
                // Pour ajuster la hauteur, décommentez et modifiez la ligne suivante :
                // modifier = Modifier.height(100.dp),
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
                GraphScreen(onBack = { nav.popBackStack() })
            }
            composable(Dest.Utilities.route) {
                UtilitiesScreen(
                    onOpenElectronics = { nav.navigate(Dest.ElectronicsCategory.route) }
                )
            }
            composable(Dest.ElectronicsCategory.route) {
                ElectronicCategoryScreen(
                    onOpenResistorCalculator = { nav.navigate(Dest.ResistorCalculator.route) },
                    onOpenInductanceCalculator = { nav.navigate(Dest.InductanceCalculator.route) },
                    onOpenCondensatorChargeCalculator = { nav.navigate(Dest.TimeConstantCalculator.route) }
                )
            }
            composable(Dest.ResistorCalculator.route) { ResistorCalculatorScreen() }
            composable(Dest.InductanceCalculator.route) { InductanceCalculatorScreen() }
            composable(Dest.TimeConstantCalculator.route) { TimeConstantCalculatorScreen(onBack = { nav.popBackStack() })
            }
        }
    }
}