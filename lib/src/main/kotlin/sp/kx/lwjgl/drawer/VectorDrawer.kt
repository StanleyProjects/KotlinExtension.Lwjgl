package sp.kx.lwjgl.drawer

import sp.kx.lwjgl.entity.Color
import sp.kx.math.Measure
import sp.kx.math.Offset
import sp.kx.math.Rotation
import sp.kx.math.Vertex

interface VectorDrawer {
    fun draw(
        color: Color,
        x0: Double, y0: Double, z0: Double,
        x1: Double, y1: Double, z1: Double,
    )

    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
    )

    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
    )

    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        measure: Measure<Double, Double>,
    )

    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
        rotation: Rotation,
        measure: Measure<Double, Double>,
    )

    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        measure: Measure<Double, Double>,
    )
}
