package com.joviansapps.ganymede.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.joviansapps.ganymede.viewmodel.SettingsViewModel


sealed class Dest(val route: String) {
    data object Home       : Dest("home")
    data object Calculator : Dest("calculator")
    data object Converter  : Dest("converter")
    data object Settings   : Dest("settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(settingsVm: SettingsViewModel) {
    val nav = rememberNavController()
    val bottomItems = listOf(Dest.Home, Dest.Settings)

    Scaffold(
        topBar = {
            val current by nav.currentBackStackEntryAsState()
            val route = current?.destination?.route
            val titleRes = when (route) {
                Dest.Home.route -> R.string.home_label
                Dest.Calculator.route -> R.string.calculator_title
                Dest.Converter.route -> R.string.converter_title
                Dest.Settings.route -> R.string.settings_title
                else -> R.string.app_name
            }
            TopAppBar(
                title = { Text(text = stringResource(titleRes)) },
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
                        onClick = { nav.navigate(dest.route) },
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
                    onOpenConverter  = { nav.navigate(Dest.Converter.route) }
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
        }
    }
}