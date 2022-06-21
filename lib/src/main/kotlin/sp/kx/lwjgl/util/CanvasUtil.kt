package sp.kx.lwjgl.util

import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.math.foundation.entity.geometry.Point
import sp.kx.math.implementation.entity.geometry.updated

fun Canvas.drawCircle(
    color: Color,
    pointCenter: Point,
    radius: Double,
    edgeCount: Int,
    lineWidth: Float
) {
    val points = (0..edgeCount).map {
        val radians = it * 2 * kotlin.math.PI / edgeCount
        pointCenter.updated(
            dX = kotlin.math.cos(radians) * radius,
            dY = kotlin.math.sin(radians) * radius,
        )
    }
    drawLineLoop(
        color = color,
        points = points,
        lineWidth = lineWidth
    )
}
