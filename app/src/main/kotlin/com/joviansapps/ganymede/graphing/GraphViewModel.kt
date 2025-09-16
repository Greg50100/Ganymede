package com.joviansapps.ganymede.graphing

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

enum class TrigMode { RADIANS, DEGREES }

class GraphViewModel : ViewModel() {
    var window = mutableStateOf(Window())
    val functions = mutableStateListOf<GraphFunction>()
    val constants = mutableStateListOf<Pair<String, Double>>()
    var mode = TrigMode.RADIANS

    // State for tracking cursor position in graph coordinates
    private val _cursorPosition = mutableStateOf<Offset?>(null)
    val cursorPosition: State<Offset?> = _cursorPosition

    // Method to update cursor position from the view
    fun setCursorPosition(position: Offset?) {
        _cursorPosition.value = position
    }

    // Add / remove / clear functions
    fun addFunction(name: String, expression: String) {
        val displayName = if (name.isBlank()) expression else name
        // pick a color based on current count
        val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Magenta, Color.Cyan)
        val color = colors[functions.size % colors.size]
        val gf = GraphFunction(expression, color, displayName)
        functions.add(gf)
    }

    fun removeFunction(id: String) {
        val idx = functions.indexOfFirst { it.id == id }
        if (idx >= 0) functions.removeAt(idx)
    }

    fun clearFunctions() {
        functions.clear()
    }

    // Zoom controls used by UI
    fun zoomIn() {
        val w = window.value
        val cx = (w.xMin + w.xMax) / 2f
        val factor = 0.8f
        val newW = Window()
        val halfWidth = (w.xMax - w.xMin) * factor / 2f
        val halfHeight = (w.yMax - w.yMin) * factor / 2f
        newW.xMin = cx - halfWidth
        newW.xMax = cx + halfWidth
        val cy = (w.yMin + w.yMax) / 2f
        newW.yMin = cy - halfHeight
        newW.yMax = cy + halfHeight
        newW.findAutoScale()
        window.value = newW
    }

    fun zoomOut() {
        val w = window.value
        val cx = (w.xMin + w.xMax) / 2f
        val factor = 1.25f
        val newW = Window()
        val halfWidth = (w.xMax - w.xMin) * factor / 2f
        val halfHeight = (w.yMax - w.yMin) * factor / 2f
        newW.xMin = cx - halfWidth
        newW.xMax = cx + halfWidth
        val cy = (w.yMin + w.yMax) / 2f
        newW.yMin = cy - halfHeight
        newW.yMax = cy + halfHeight
        newW.findAutoScale()
        window.value = newW
    }

    fun resetZoom() {
        window.value = Window()
    }
}
