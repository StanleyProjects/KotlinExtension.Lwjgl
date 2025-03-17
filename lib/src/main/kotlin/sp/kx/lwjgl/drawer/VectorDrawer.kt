package sp.kx.lwjgl.drawer

import sp.kx.calculations.algebra.Matrix
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Rotation
import sp.kx.calculations.geometry.Vertex
import sp.kx.lwjgl.entity.Color

interface VectorDrawer {
    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        scale: Double,
    )

    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        matrix: Matrix,
    )
}
