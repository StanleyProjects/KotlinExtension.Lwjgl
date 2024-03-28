package sp.kx.lwjgl.entity

import sp.kx.math.Point
import sp.kx.math.Size

interface PolygonDrawer {
    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
    )

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Double,
    )

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
    )
}
