package sp.kx.lwjgl.drawer

import sp.kx.calculations.Size
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Rotation
import sp.kx.calculations.geometry.Vertex
import sp.kx.lwjgl.entity.Color

interface PolygonDrawer {
    fun drawRectangle(
        color: Color,
        topLeft: Vertex,
        size: Size,
        rotation: Rotation,
        about: Vertex,
        offset: Offset,
        scale: Double,
    )

    fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        rotation: Rotation,
        about: Vertex,
        offset: Offset,
        scale: Double,
    )
}
