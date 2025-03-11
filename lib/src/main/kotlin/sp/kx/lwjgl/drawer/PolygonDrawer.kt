package sp.kx.lwjgl.drawer

import sp.kx.calculations.Size
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Rotation
import sp.kx.calculations.geometry.Vertex
import sp.kx.lwjgl.entity.Color

interface PolygonDrawer {
    fun drawRectangle(
        color: Color,
        x: Double,
        y: Double,
        z: Double,
        width: Double,
        height: Double,
    )

    fun drawRectangle(
        color: Color,
        topLeft: Vertex,
        size: Size,
        offset: Offset,
        rotation: Rotation,
        scale: Double,
    )

    fun drawRectangle(
        color: Color,
        topLeft: Vertex,
        size: Size,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        scale: Double,
    )

    fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        scale: Double,
    )

    fun drawCircle(
        color: Color,
        x: Double,
        y: Double,
        z: Double,
        radius: Double,
        edgeCount: Int,
    )

    fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
    )

    fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
    )

    fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        scale: Double,
    )

    fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        scale: Double,
    )

    fun drawCircle(
        color: Color,
        x: Double,
        y: Double,
        z: Double,
        aX: Double,
        aY: Double,
        aZ: Double,
        radius: Double,
        edgeCount: Int,
    )

    fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        rotation: Rotation,
        scale: Double,
    )
}
