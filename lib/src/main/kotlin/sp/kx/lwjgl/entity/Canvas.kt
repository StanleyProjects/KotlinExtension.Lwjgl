package sp.kx.lwjgl.entity

import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.Vector

interface Canvas {
    val vectors: VectorDrawer
    val texts: TextDrawer

    @Deprecated(message = "replace with vectors.draw")
    fun drawLine(
        color: Color,
        vector: Vector,
        lineWidth: Float,
    ) {
        vectors.draw(color, vector, lineWidth)
    }

    @Deprecated(message = "replace with texts.draw")
    fun drawText(color: Color, info: FontInfo, pointTopLeft: Point, text: CharSequence) {
        texts.draw(color, info, pointTopLeft, text)
    }

    fun drawPoint(color: Color, point: Point)
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
