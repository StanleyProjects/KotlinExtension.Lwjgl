package sp.kx.lwjgl.entity

import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.Vector

interface Canvas {
    fun drawPoint(color: Color, point: Point)
    fun drawText(color: Color, info: FontInfo, pointTopLeft: Point, text: CharSequence)
    fun drawLine(
        color: Color,
        vector: Vector,
        lineWidth: Float
    )
    fun drawLineLoop(
        color: Color,
        points: Iterable<Point>,
        lineWidth: Float
    )
    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Float
    )
    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Float,
        direction: Double,
        pointOfRotation: Point
    )
}
