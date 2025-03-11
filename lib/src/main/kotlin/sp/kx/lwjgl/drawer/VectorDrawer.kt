package sp.kx.lwjgl.drawer

import sp.kx.calculations.Size
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Rotation
import sp.kx.calculations.geometry.Vertex
import sp.kx.lwjgl.entity.Color

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
        scale: Double,
    )

    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
        scale: Double,
    )

    fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
        rotation: Rotation,
        scale: Double,
    )

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
        offset: Offset,
        pictureSize: Size,
        rotation: Rotation,
        scale: Double,
    )
}
