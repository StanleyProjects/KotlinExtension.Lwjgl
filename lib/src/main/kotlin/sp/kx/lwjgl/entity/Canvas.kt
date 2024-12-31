package sp.kx.lwjgl.entity

import sp.kx.lwjgl.drawer.PolygonDrawer
import sp.kx.lwjgl.drawer.TextDrawer
import sp.kx.lwjgl.drawer.VectorDrawer
import sp.kx.math.Point

interface Canvas {
    val vectors: VectorDrawer
    val polygons: PolygonDrawer
    val texts: TextDrawer

    fun drawPoint(color: Color, point: Point)

    fun drawLineLoop(
        color: Color,
        points: Iterable<Point>,
        lineWidth: Float,
    )
}
