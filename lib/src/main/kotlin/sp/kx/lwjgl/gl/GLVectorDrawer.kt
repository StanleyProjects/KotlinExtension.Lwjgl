package sp.kx.lwjgl.gl

import org.lwjgl.opengl.GL11
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Rotation
import sp.kx.calculations.geometry.Vertex
import sp.kx.lwjgl.drawer.VectorDrawer
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.opengl.GLUtil

internal object GLVectorDrawer : VectorDrawer {
    override fun draw(
        color: Color,
        x0: Double, y0: Double, z0: Double,
        x1: Double, y1: Double, z1: Double,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINES) {
            GL11.glVertex3d(x0, y0, z0)
            GL11.glVertex3d(x1, y1, z1)
        }
    }

    override fun draw(color: Color, start: Vertex, finish: Vertex) {
        draw(
            color = color,
            x0 = start.x, y0 = start.y, z0 = start.z,
            x1 = finish.x, y1 = finish.y, z1 = finish.z,
        )
    }

    override fun draw(color: Color, start: Vertex, finish: Vertex, offset: Offset) {
        draw(
            color = color,
            x0 = start.x + offset.dX, y0 = start.y + offset.dY, z0 = start.z + offset.dZ,
            x1 = finish.x + offset.dX, y1 = finish.y + offset.dY, z1 = finish.z + offset.dZ,
        )
    }

    override fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        scale: Double,
    ) {
        draw(
            color = color,
            x0 = start.x * scale,
            y0 = start.y * scale,
            z0 = start.z * scale,
            x1 = finish.x * scale,
            y1 = finish.y * scale,
            z1 = finish.z * scale,
        )
    }

    override fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
        scale: Double,
    ) {
        draw(
            color = color,
            x0 = (start.x + offset.dX) * scale,
            y0 = (start.y + offset.dY) * scale,
            z0 = (start.z + offset.dZ) * scale,
            x1 = (finish.x + offset.dX) * scale,
            y1 = (finish.y + offset.dY) * scale,
            z1 = (finish.z + offset.dZ) * scale,
        )
    }

    override fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
        rotation: Rotation,
        scale: Double,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINES) {
            GLUtil.vertexOf(
                vertex = start,
                offset = offset,
                rotation = rotation,
                scale = scale,
            )
            GLUtil.vertexOf(
                vertex = finish,
                offset = offset,
                rotation = rotation,
                scale = scale,
            )
        }
    }

    override fun draw(
        color: Color,
        start: Vertex,
        finish: Vertex,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        scale: Double,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINES) {
            GLUtil.vertexOf(
                vertex = start,
                offset = offset,
                about = about,
                rotation = rotation,
                scale = scale,
            )
            GLUtil.vertexOf(
                vertex = finish,
                offset = offset,
                about = about,
                rotation = rotation,
                scale = scale,
            )
        }
    }
}
