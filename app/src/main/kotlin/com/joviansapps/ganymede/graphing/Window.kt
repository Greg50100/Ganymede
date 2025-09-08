package com.joviansapps.ganymede.graphing

/**
 * Window used for graph rendering (adapted from IO project)
 */
class Window {

    var xMin: Float = -20f
    var yMin: Float = -40f
    var xMax: Float = 20f
    var yMax: Float = 40f

    var xScale: Float = 4f
    var yScale: Float = 4f

    val minHorizontalGridLines: Int = 8
    val minVerticalGridLines: Int = 4

    val maxHorizontalGridLines: Int = 24
    val maxVerticalGridLines: Int = 12

    fun findAutoScale() {
        var xScale = this.xScale
        var yScale = this.yScale

        assert(xMax > xMin)
        assert(yMax > yMin)

        if (xScale <= 0f || yScale <= 0f) {
            xScale = 4f
            yScale = 4f
        }
        var windowWidth = (xMax - xMin) / xScale
        var windowHeight = (yMax - yMin) / yScale

        while (windowWidth > maxVerticalGridLines) {
            xScale *= 2
            windowWidth = (xMax - xMin) / xScale
        }
        while (windowHeight > maxHorizontalGridLines) {
            yScale *= 2
            windowHeight = (yMax - yMin) / yScale
        }
        while (windowWidth < minVerticalGridLines) {
            xScale /= 2
            windowWidth = (xMax - xMin) / xScale
        }
        while (windowHeight < minHorizontalGridLines) {
            yScale /= 2
            windowHeight = (yMax - yMin) / yScale
        }
        this.xScale = xScale
        this.yScale = yScale
    }
}

