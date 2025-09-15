package com.joviansapps.ganymede.graphing

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import com.joviansapps.ganymede.graphing.GraphViewModel

@Composable
fun CanvasView(vm: GraphViewModel) {
    Box(Modifier.background(MaterialTheme.colorScheme.background)) {
        val textMeasurer = rememberTextMeasurer()
        val gridLinesColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
        val gridAxesColor = MaterialTheme.colorScheme.onBackground

        var widthPx by remember { mutableStateOf(0f) }
        var heightPx by remember { mutableStateOf(0f) }

        val drawModifier = Modifier
            .fillMaxSize()
            .onSizeChanged { widthPx = it.width.toFloat(); heightPx = it.height.toFloat() }
            .pointerInput(Unit) {
                // use widthPx/heightPx captured from onSizeChanged
                detectTransformGestures { centroid, pan, zoom, _ ->
                    // ignore if size not known yet
                    if (widthPx <= 0f || heightPx <= 0f) return@detectTransformGestures

                    // debug log to verify gesture reception
                    Log.d("CanvasView", "gesture zoom=$zoom pan=$pan centroid=${centroid.x},${centroid.y}")

                    val w = vm.window.value
                    val width = widthPx
                    val height = heightPx
                    val centerXUnit = w.xMin + (centroid.x / width) * (w.xMax - w.xMin)
                    val centerYUnit = w.yMax - (centroid.y / height) * (w.yMax - w.yMin)

                    if (zoom != 1f) {
                        w.xMin = centerXUnit + (w.xMin - centerXUnit) / zoom
                        w.xMax = centerXUnit + (w.xMax - centerXUnit) / zoom
                        w.yMin = centerYUnit + (w.yMin - centerYUnit) / zoom
                        w.yMax = centerYUnit + (w.yMax - centerYUnit) / zoom
                    }
                    if (pan.x != 0f || pan.y != 0f) {
                        val dxUnit = pan.x * (w.xMax - w.xMin) / width
                        val dyUnit = pan.y * (w.yMax - w.yMin) / height
                        w.xMin -= dxUnit
                        w.xMax -= dxUnit
                        w.yMin += dyUnit
                        w.yMax += dyUnit
                    }
                    // clamp
                    val minWidth = 0.01f
                    val maxWidth = 10000f
                    var wWidth = w.xMax - w.xMin
                    if (wWidth < minWidth) {
                        val cx = (w.xMin + w.xMax) / 2f
                        w.xMin = cx - minWidth / 2f
                        w.xMax = cx + minWidth / 2f
                    } else if (wWidth > maxWidth) {
                        val cx = (w.xMin + w.xMax) / 2f
                        w.xMin = cx - maxWidth / 2f
                        w.xMax = cx + maxWidth / 2f
                    }
                    // create new Window instance to trigger recomposition reliably
                    val newW = Window()
                    newW.xMin = w.xMin
                    newW.xMax = w.xMax
                    newW.yMin = w.yMin
                    newW.yMax = w.yMax
                    newW.xScale = w.xScale
                    newW.yScale = w.yScale
                    vm.window.value = newW
                }
            }

        Canvas(modifier = drawModifier) {
            val scale = 1f
            scale(scale) {
                renderCanvas(vm.window.value, vm, scale, textMeasurer, gridLinesColor, gridAxesColor)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CanvasViewPreview() {
    CanvasView(vm = androidx.lifecycle.viewmodel.compose.viewModel())
}
