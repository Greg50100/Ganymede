package com.joviansapps.ganymede.graphing

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

enum class TrigMode { RADIANS, DEGREES }

class GraphViewModel : ViewModel() {
    var window = mutableStateOf(Window())
    val functions = mutableStateListOf<GraphFunction>()
    val constants = mutableStateListOf<Pair<String, Double>>()
    var mode = TrigMode.RADIANS
}
