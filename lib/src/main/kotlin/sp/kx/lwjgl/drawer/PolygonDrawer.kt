package sp.kx.lwjgl.drawer

import sp.kx.lwjgl.entity.Color
import sp.kx.math.Measure
import sp.kx.math.Offset
import sp.kx.math.Rotation
import sp.kx.math.Vertex

interface PolygonDrawer {
    fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        measure: Measure<Double, Double>,
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
        measure: Measure<Double, Double>,
    )

    fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        measure: Measure<Double, Double>,
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
        measure: Measure<Double, Double>,
    )
}
