package sp.kx.lwjgl.util

import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.Size
import sp.kx.math.foundation.entity.geometry.Point
import sp.kx.math.implementation.entity.geometry.pointOf
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

fun Canvas.drawRectangle(
    color: Color,
    pointTopLeft: Point,
    size: Size,
    lineWidth: Float
) {
    val pointBottomRight = pointTopLeft.updated(
        dX = size.width,
        dY = size.height
    )
    val points = setOf(
        pointTopLeft,
        pointOf(pointBottomRight.x, pointTopLeft.y),
        pointBottomRight,
        pointOf(pointTopLeft.x, pointBottomRight.y)
    )
    drawLineLoop(
        color = color,
        points = points,
        lineWidth = lineWidth
    )
}
