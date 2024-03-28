package sp.kx.lwjgl.entity

import sp.kx.math.Point
import sp.kx.math.Size

interface Canvas {
    val vectors: VectorDrawer
    val texts: TextDrawer

    fun drawPoint(color: Color, point: Point)

    fun drawLineLoop(
        color: Color,
        points: Iterable<Point>,
        lineWidth: Float,
    )

    @Deprecated(message = "polygons")
    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Float
    )

    @Deprecated(message = "polygons")
    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Float,
        direction: Double,
        pointOfRotation: Point
    )

    @Deprecated(message = "polygons")
    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Float
    )

    @Deprecated(message = "polygons")
    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
    )
}
