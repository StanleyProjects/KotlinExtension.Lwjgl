package sp.kx.lwjgl.entity

import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.Vector
import sp.kx.math.measure.Measure

interface Canvas {
    interface VectorDrawer {
        fun draw(
            color: Color,
            vector: Vector,
            lineWidth: Float,
        )

        fun draw(
            color: Color,
            vector: Vector,
            offset: Offset,
            lineWidth: Float,
        )

        fun draw(
            color: Color,
            vector: Vector,
            measure: Measure<Double, Double>,
            lineWidth: Float,
        )

        fun draw(
            color: Color,
            vector: Vector,
            offset: Offset,
            measure: Measure<Double, Double>,
            lineWidth: Float,
        )
    }

    val vectors: VectorDrawer

    @Deprecated(message = "replace with vectors.draw")
    fun drawLine(
        color: Color,
        vector: Vector,
        lineWidth: Float,
    ) {
        vectors.draw(color, vector, lineWidth)
    }

    fun drawPoint(color: Color, point: Point)
    fun drawText(color: Color, info: FontInfo, pointTopLeft: Point, text: CharSequence)
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
