package com.joviansapps.ganymede.graphing

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sign

fun DrawScope.drawGridLines(window: Window, lineWidth: Float, gridLinesColor: Color) {
    val xRange = IntRange(
        ceil(window.xMin / window.xScale).toInt(),
        floor(window.xMax / window.xScale).toInt()
    )

    for (i in xRange) {
        if (i == 0) continue
        val xDraw = Offset(i * window.xScale, 0f).unitToPxCoordinates(window, size.width, size.height).x
        drawLine(gridLinesColor, Offset(xDraw, 0f), Offset(xDraw, size.height), lineWidth)
    }
    val yRange = IntRange(
        ceil(window.yMin / window.yScale).toInt(),
        floor(window.yMax / window.yScale).toInt()
    )

    for (i in yRange) {
        if (i == 0) continue
        val yDraw = Offset(0f, i * window.yScale).unitToPxCoordinates(window, size.width, size.height).y
        drawLine(gridLinesColor, Offset(0f, yDraw), Offset(size.width, yDraw), lineWidth)
    }
}

internal fun Float.toDisplayString(): String {
    return when {
        (this % 1f == 0f) && (this in 0.0001f..10000f) -> {
            this.toInt().toString()
        }

        abs(this) <= 0.0001 -> {
            "%.3e".format(this)
        }

        abs(this) < 10000 -> {
            this.toString()
        }

        else -> {
            "%.2e".format(this)
        }
    }
}

fun DrawScope.drawAxes(
    window: Window,
    lineWidth: Float,
    canvasScale: Float,
    textMeasurer: TextMeasurer,
    axesColor: Color
) {
    val windowCenterInCanvas = Offset(0f, 0f).unitToPxCoordinates(window, size.width, size.height)
    drawLine(
        axesColor,
        Offset(windowCenterInCanvas.x, 0f),
        Offset(windowCenterInCanvas.x, size.height),
        lineWidth
    )
    drawLine(
        axesColor,
        Offset(0f, windowCenterInCanvas.y),
        Offset(size.width, windowCenterInCanvas.y),
        lineWidth
    )

    val xTickRange = IntRange(
        ceil(window.xMin / window.xScale).toInt(), floor(window.xMax / window.xScale).toInt()
    )

    for (i in xTickRange) {
        if (i == 0) continue
        val xDisplayValue = (i * window.xScale).toDisplayString()
        val xDraw = Offset(i * window.xScale, 0f).unitToPxCoordinates(window, size.width, size.height).x
        val yDraw = Offset(0f, 0f).unitToPxCoordinates(window, size.width, size.height).y
        val textWidth = 200 / canvasScale
        val textHeight = 40 / canvasScale
        val textPadding = 20 / canvasScale
        drawText(
            textMeasurer,
            xDisplayValue,
            topLeft = Offset(xDraw - textWidth / 2, yDraw + textPadding),
            size = Size(textWidth, textHeight),
            style = TextStyle(
                color = axesColor,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        )
    }

    val yTickRange = IntRange(
        ceil(window.yMin / window.yScale).toInt(), floor(window.yMax / window.yScale).toInt()
    )

    for (i in yTickRange) {
        if (i == 0) continue
        val yDisplayValue = (i * window.yScale).toDisplayString()
        val xDraw = Offset(0f, 0f).unitToPxCoordinates(window, size.width, size.height).x
        val yDraw = Offset(0f, i * window.yScale).unitToPxCoordinates(window, size.width, size.height).y
        val textWidth = 200 / canvasScale
        val textHeight = 40 / canvasScale
        val textPadding = 20 / canvasScale
        drawText(
            textMeasurer,
            yDisplayValue,
            topLeft = Offset(xDraw + textPadding, yDraw - textHeight / 2),
            size = Size(textWidth, textHeight),
            style = TextStyle(color = axesColor, fontWeight = FontWeight.Medium)
        )
    }
}

fun DrawScope.graphAroundAsymptote(
    window: Window,
    function: GraphFunction,
    aX1: Float,
    aX2: Float,
    pDerivative: Float,
    depth: Int,
    lineWidth: Float
) {
    var previousDerivative = pDerivative
    val precision = 2
    for (j in 0 until precision) {
        val currentX = aX1 + (aX2 - aX1) * j / precision
        val nextX = aX1 + (aX2 - aX1) * (j + 1) / precision
        val currentY = function.execute(currentX) ?: 0f
        val nextY = function.execute(nextX) ?: 0f

        val currentDerivative = (nextY - currentY) / (nextX - currentX)
        if ((currentDerivative >= 0 && previousDerivative >= 0) || (currentDerivative <= 0 && previousDerivative <= 0)) {
            drawLine(
                function.color,
                Offset(currentX, currentY).unitToPxCoordinates(window, size.width, size.height),
                Offset(nextX, nextY).unitToPxCoordinates(window, size.width, size.height),
                lineWidth
            )
        } else {
            if (depth > 1) {
                graphAroundAsymptote(
                    window,
                    function,
                    currentX,
                    nextX,
                    previousDerivative,
                    depth - 1,
                    lineWidth
                )
            }
            return
        }
        previousDerivative = currentDerivative
    }
}

fun DrawScope.drawGraph(window: Window, function: GraphFunction, lineWidth: Float) {
    val resolution = 500
    var previousX = 0f
    var previousDerivative = 0f
    for (i in 0 until resolution) {
        val currentX = window.xMin + i / resolution.toFloat() * (window.xMax - window.xMin)
        val nextX = window.xMin + (i + 1) / resolution.toFloat() * (window.xMax - window.xMin)

        val currentY = function.execute(currentX) ?: 0f
        val nextY = function.execute(nextX) ?: 0f

        val currentDerivative = (nextY - currentY) / (nextX - currentX)
        if ((currentDerivative >= 0 && previousDerivative >= 0) || (currentDerivative <= 0 && previousDerivative <= 0)) {
            drawLine(
                function.color,
                Offset(currentX, currentY).unitToPxCoordinates(window, size.width, size.height),
                Offset(nextX, nextY).unitToPxCoordinates(window, size.width, size.height),
                lineWidth
            )
        } else {
            if (abs(previousDerivative) < abs(currentDerivative)) {
                graphAroundAsymptote(
                    window,
                    function,
                    currentX,
                    nextX,
                    previousDerivative,
                    20,
                    lineWidth
                )
            } else {
                graphAroundAsymptote(
                    window,
                    function,
                    nextX,
                    previousX,
                    currentDerivative,
                    20,
                    lineWidth
                )
            }
            drawLine(
                function.color,
                Offset(currentX, currentY).unitToPxCoordinates(window, size.width, size.height),
                Offset(nextX, currentY).unitToPxCoordinates(window, size.width, size.height),
                lineWidth
            )
        }
        previousDerivative = currentDerivative
        previousX = currentX
    }
}


fun DrawScope.drawTrackingCrosshair(
    window: Window,
    vm: GraphViewModel,
    textMeasurer: TextMeasurer,
    axesColor: Color,
    surfaceColor: Color
) {
    val cursorPosition = vm.cursorPosition.value ?: return
    val functions = vm.functions

    // Convert graph coordinates to pixel coordinates
    val cursorPx = cursorPosition.unitToPxCoordinates(window, size.width, size.height)

    // Clamp to canvas bounds
    if (cursorPx.x < 0 || cursorPx.x > size.width || cursorPx.y < 0 || cursorPx.y > size.height) {
        vm.setCursorPosition(null) // Hide cursor if it's dragged off-screen
        return
    }

    // Draw crosshair lines (dashed)
    val pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    drawLine(axesColor, Offset(cursorPx.x, 0f), Offset(cursorPx.x, size.height), strokeWidth = 2f, pathEffect = pathEffect)
    drawLine(axesColor, Offset(0f, cursorPx.y), Offset(size.width, cursorPx.y), strokeWidth = 2f, pathEffect = pathEffect)


    // For each function, find the y-value at the cursor's x and draw info
    functions.forEach { function ->
        function.execute(cursorPosition.x)?.let { yValue ->
            val pointOnCurve = Offset(cursorPosition.x, yValue)
            val pointOnCurvePx = pointOnCurve.unitToPxCoordinates(window, size.width, size.height)

            // Draw a circle on the curve
            if(pointOnCurvePx.y > 0 && pointOnCurvePx.y < size.height) {
                drawCircle(function.color, radius = 10f, center = pointOnCurvePx)

                // Draw the text label with coordinates
                val labelText = "(${String.format("%.2f", cursorPosition.x)}, ${String.format("%.2f", yValue)})"
                val textResult = textMeasurer.measure(
                    text = labelText,
                    style = TextStyle(color = function.color, background = surfaceColor.copy(alpha = 0.8f))
                )
                drawText(
                    textLayoutResult = textResult,
                    topLeft = Offset(pointOnCurvePx.x + 20f, pointOnCurvePx.y - textResult.size.height - 20f)
                )
            }
        }
    }
}

/**
 * Utility function to find and draw the roots (x-intercepts) of a function.
 * It detects where the function's sign changes and marks the spot.
 */
fun DrawScope.drawRoots(
    window: Window,
    function: GraphFunction,
    resolution: Int = 500
) {
    var prevY: Float? = null

    for (i in 0..resolution) {
        val x = window.xMin + (window.xMax - window.xMin) * (i.toFloat() / resolution)
        val y = function.execute(x)

        if (prevY != null && y != null && y.isFinite() && prevY.isFinite()) {
            // A change of sign indicates that a root is between the previous and current point.
            if (sign(y) != sign(prevY)) {
                val rootPointPx = Offset(x, 0f).unitToPxCoordinates(window, size.width, size.height)
                // Rendre le marqueur plus visible
                drawCircle(
                    color = function.color.copy(alpha = 0.5f),
                    radius = 16f,
                    center = rootPointPx,
                )
                drawCircle(
                    color = Color.Black,
                    radius = 16f,
                    center = rootPointPx,
                    style = Stroke(width = 2f)
                )
            }
        }
        prevY = y
    }
}

/**
 * Utility function to find and draw intersection points between two functions.
 */
fun DrawScope.drawIntersections(
    window: Window,
    func1: GraphFunction,
    func2: GraphFunction,
    resolution: Int = 500
) {
    var prevDiff: Float? = null

    for (i in 0..resolution) {
        val x = window.xMin + (window.xMax - window.xMin) * (i.toFloat() / resolution)
        val y1 = func1.execute(x)
        val y2 = func2.execute(x)

        if (y1 != null && y2 != null && y1.isFinite() && y2.isFinite()) {
            val diff = y1 - y2
            if (prevDiff != null) {
                // A change of sign in the difference between the two functions indicates an intersection.
                if (sign(diff) != sign(prevDiff)) {
                    val intersectPoint = Offset(x, y1) // Approximate y-value with y1
                    val intersectPx = intersectPoint.unitToPxCoordinates(window, size.width, size.height)

                    // Rendre le marqueur plus visible
                     drawCircle(
                        color = Color.White,
                        radius = 18f,
                        center = intersectPx
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 16f,
                        center = intersectPx
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 14f,
                        center = intersectPx
                    )
                }
            }
            prevDiff = diff
        } else {
            prevDiff = null // Reset if one of the functions is undefined
        }
    }
}


fun DrawScope.renderCanvas(
    window: Window,
    vm: GraphViewModel,
    canvasScale: Float,
    textMeasurer: TextMeasurer,
    gridLinesColor: Color,
    axesColor: Color,
    surfaceColor: Color = Color.Gray // Provide a default color
) {
    val lineWidth = 5f / canvasScale
    window.findAutoScale()
    drawGridLines(window, lineWidth, gridLinesColor)
    drawAxes(window, lineWidth, canvasScale, textMeasurer, axesColor)

    vm.functions.forEach { function ->
        runCatching {
            drawGraph(window, function, lineWidth)
            // NEW: Draw roots for the function
            drawRoots(window, function)
        }
    }

    // NEW: Draw intersections between the first two functions, if they exist
    if (vm.functions.size >= 2) {
        runCatching {
            drawIntersections(window, vm.functions[0], vm.functions[1])
        }
    }


    // Call the crosshair drawing function at the end (so it draws on top)
    drawTrackingCrosshair(window, vm, textMeasurer, axesColor, surfaceColor)
}
