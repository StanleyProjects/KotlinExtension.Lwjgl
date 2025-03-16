package sp.kx.lwjgl.gl

import org.lwjgl.opengl.GL11
import sp.kx.calculations.Size
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Rotation
import sp.kx.calculations.geometry.Vertex
import sp.kx.lwjgl.drawer.PolygonDrawer
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.opengl.GLUtil

internal object GLPolygonDrawer : PolygonDrawer {
    override fun drawRectangle(
        color: Color,
        topLeft: Vertex,
        size: Size,
        rotation: Rotation,
        about: Vertex,
        offset: Offset,
        scale: Double,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOf(
                x = topLeft.x, y = topLeft.y, z = topLeft.z,
                aX = rotation.aX, aY = rotation.aY, aZ = rotation.aZ,
                rX = about.x, rY = about.y, rZ = about.z,
                dX = offset.dX, dY = offset.dY, dZ = offset.dZ,
                scale = scale,
            )
            GLUtil.vertexOf(
                x = topLeft.x + size.width, y = topLeft.y, z = topLeft.z,
                aX = rotation.aX, aY = rotation.aY, aZ = rotation.aZ,
                rX = about.x, rY = about.y, rZ = about.z,
                dX = offset.dX, dY = offset.dY, dZ = offset.dZ,
                scale = scale,
            )
            GLUtil.vertexOf(
                x = topLeft.x, y = topLeft.y + size.height, z = topLeft.z,
                aX = rotation.aX, aY = rotation.aY, aZ = rotation.aZ,
                rX = about.x, rY = about.y, rZ = about.z,
                dX = offset.dX, dY = offset.dY, dZ = offset.dZ,
                scale = scale,
            )
            GLUtil.vertexOf(
                x = topLeft.x + size.width, y = topLeft.y + size.height, z = topLeft.z,
                aX = rotation.aX, aY = rotation.aY, aZ = rotation.aZ,
                rX = about.x, rY = about.y, rZ = about.z,
                dX = offset.dX, dY = offset.dY, dZ = offset.dZ,
                scale = scale,
            )
        }
    }

    override fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        rotation: Rotation,
        about: Vertex,
        offset: Offset,
        scale: Double,
    ) {
        if (edgeCount < 3) TODO()
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0 until edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                //
                GLUtil.vertexOf(
                    x = center.x + kotlin.math.cos(radians) * radius,
                    y = center.y + kotlin.math.sin(radians) * radius,
                    z = center.z,
                    aX = rotation.aX, aY = rotation.aY, aZ = rotation.aZ,
                    rX = about.x, rY = about.y, rZ = about.z,
                    dX = offset.dX, dY = offset.dY, dZ = offset.dZ,
                    scale = scale,
                )
            }
        }
    }
}
