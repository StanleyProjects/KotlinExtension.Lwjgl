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
                x = start.x, y = start.y, z = start.z,
                aX = rotation.aX, aY = rotation.aY, aZ = rotation.aZ,
                rX = about.x, rY = about.y, rZ = about.z,
                dX = offset.dX, dY = offset.dY, dZ = offset.dZ,
                scale = scale,
            )
            GLUtil.vertexOf(
                x = finish.x, y = finish.y, z = finish.z,
                aX = rotation.aX, aY = rotation.aY, aZ = rotation.aZ,
                rX = about.x, rY = about.y, rZ = about.z,
                dX = offset.dX, dY = offset.dY, dZ = offset.dZ,
                scale = scale,
            )
        }
    }
}
