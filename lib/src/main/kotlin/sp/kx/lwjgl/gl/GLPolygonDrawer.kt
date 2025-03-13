package sp.kx.lwjgl.gl

import org.lwjgl.opengl.GL11
import sp.kx.calculations.Size
import sp.kx.calculations.algebra.Matrix
import sp.kx.calculations.geometry.MutableVertex
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Rotation
import sp.kx.calculations.geometry.Vertex
import sp.kx.calculations.operators.times
import sp.kx.lwjgl.drawer.PolygonDrawer
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.opengl.GLUtil

internal object GLPolygonDrawer : PolygonDrawer {
    override fun drawRectangle(
        color: Color,
        x: Double, y: Double, z: Double,
        width: Double,
        height: Double,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GL11.glVertex3d(x, y, z)
            GL11.glVertex3d(x + width, y, z)
            GL11.glVertex3d(x, y + height, z)
            GL11.glVertex3d(x + width, y + height, z)
        }
    }

    override fun drawRectangle(
        color: Color,
        x: Double, y: Double, z: Double,
        width: Double,
        height: Double,
        matrix: Matrix,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOf(x, y, z, matrix = matrix)
            GLUtil.vertexOf(x + width, y, z, matrix = matrix)
            GLUtil.vertexOf(x, y + height, z, matrix = matrix)
            GLUtil.vertexOf(x + width, y + height, z, matrix = matrix)
        }
    }

    override fun drawRectangle(
        color: Color,
        topLeft: Vertex,
        size: Size,
        offset: Offset,
        rotation: Rotation,
        scale: Double,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOf(
                topLeft.x, topLeft.y,
                z = topLeft.z,
                offset = offset, rotation = rotation, scale = scale,
            )
            GLUtil.vertexOf(
                topLeft.x + size.width, topLeft.y,
                z = topLeft.z,
                offset = offset, rotation = rotation, scale = scale,
            )
            GLUtil.vertexOf(
                topLeft.x, topLeft.y + size.height,
                z = topLeft.z,
                offset = offset, rotation = rotation, scale = scale,
            )
            GLUtil.vertexOf(
                topLeft.x + size.width, topLeft.y + size.height,
                z = topLeft.z,
                offset = offset, rotation = rotation, scale = scale,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        topLeft: Vertex,
        size: Size,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        scale: Double,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOf(
                topLeft.x, topLeft.y,
                z = topLeft.z,
                offset = offset,
                about = about,
                rotation = rotation,
                scale = scale,
            )
            GLUtil.vertexOf(
                topLeft.x + size.width, topLeft.y,
                z = topLeft.z,
                offset = offset,
                about = about,
                rotation = rotation,
                scale = scale,
            )
            GLUtil.vertexOf(
                topLeft.x, topLeft.y + size.height,
                z = topLeft.z,
                offset = offset,
                about = about,
                rotation = rotation,
                scale = scale,
            )
            GLUtil.vertexOf(
                topLeft.x + size.width, topLeft.y + size.height,
                z = topLeft.z,
                offset = offset,
                about = about,
                rotation = rotation,
                scale = scale,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        topLeft: Vertex,
        size: Size,
        offset: Offset,
        pictureSize: Size,
        rotation: Rotation,
        scale: Double
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOf(
                topLeft.x, topLeft.y,
                z = topLeft.z,
                offset = offset,
                pictureSize = pictureSize,
                rotation = rotation,
                scale = scale,
            )
            GLUtil.vertexOf(
                topLeft.x + size.width, topLeft.y,
                z = topLeft.z,
                offset = offset,
                pictureSize = pictureSize,
                rotation = rotation,
                scale = scale,
            )
            GLUtil.vertexOf(
                topLeft.x, topLeft.y + size.height,
                z = topLeft.z,
                offset = offset,
                pictureSize = pictureSize,
                rotation = rotation,
                scale = scale,
            )
            GLUtil.vertexOf(
                topLeft.x + size.width, topLeft.y + size.height,
                z = topLeft.z,
                offset = offset,
                pictureSize = pictureSize,
                rotation = rotation,
                scale = scale,
            )
        }
    }

    override fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        scale: Double,
    ) {
        if (edgeCount < 3) TODO()
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0 until edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                //
//                var x = center.x
//                var y = center.y
//                var z = center.z
//                x += kotlin.math.cos(radians) * radius
//                y += kotlin.math.sin(radians) * radius
//                x -= about.x
//                y -= about.y
//                z -= about.z
//                var c = kotlin.math.cos(rotation.aX)
//                var s = kotlin.math.sin(rotation.aX)
//                y = y * c - z * s
//                z = y * s + z * c
//                c = kotlin.math.cos(rotation.aY)
//                s = kotlin.math.sin(rotation.aY)
//                x = x * c - z * s
//                z = x * s + z * c
//                c = kotlin.math.cos(rotation.aZ)
//                s = kotlin.math.sin(rotation.aZ)
//                x = x * c - y * s
//                y = x * s + y * c
//                x += about.x
//                y += about.y
//                z += about.z
                //
                GLUtil.vertexOf(
                    x = center.x + kotlin.math.cos(radians) * radius,
                    y = center.y + kotlin.math.sin(radians) * radius,
                    z = center.z,
                    offset = offset,
                    about = about,
                    rotation = rotation,
                    scale = scale,
                )
            }
        }
    }

    override fun drawCircle(
        color: Color,
        x: Double,
        y: Double,
        z: Double,
        radius: Double,
        edgeCount: Int,
    ) {
        if (edgeCount < 3) TODO()
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0 until edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                GL11.glVertex3d(
                    x + kotlin.math.cos(radians) * radius,
                    y + kotlin.math.sin(radians) * radius,
                    z,
                )
            }
        }
    }

    override fun drawCircle(color: Color, center: Vertex, radius: Double, edgeCount: Int) {
        drawCircle(
            color = color,
            x = center.x,
            y = center.y,
            z = center.z,
            radius = radius,
            edgeCount = edgeCount,
        )
    }

    override fun drawCircle(color: Color, center: Vertex, radius: Double, edgeCount: Int, offset: Offset) {
        drawCircle(
            color = color,
            x = center.x + offset.dX,
            y = center.y + offset.dY,
            z = center.z + offset.dZ,
            radius = radius,
            edgeCount = edgeCount,
        )
    }

    override fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        scale: Double,
    ) {
        drawCircle(
            color = color,
            x = center.x * scale,
            y = center.y * scale,
            z = center.z * scale,
            radius = radius * scale,
            edgeCount = edgeCount,
        )
    }

    override fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        scale: Double,
    ) {
        drawCircle(
            color = color,
            x = (center.x + offset.dX) * scale,
            y = (center.y + offset.dY) * scale,
            z = (center.z + offset.dZ) * scale,
            radius = radius * scale,
            edgeCount = edgeCount,
        )
    }

    override fun drawCircle(
        color: Color,
        x: Double,
        y: Double,
        z: Double,
        aX: Double,
        aY: Double,
        aZ: Double,
        radius: Double,
        edgeCount: Int,
    ) {
        if (edgeCount < 3) TODO()
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0 until edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                var x = x + kotlin.math.cos(radians) * radius
                var y = y + kotlin.math.sin(radians) * radius
                var c = kotlin.math.cos(aX)
                var s = kotlin.math.sin(aX)
                y = y * c - z * s
                var z = y * s + z * c
                c = kotlin.math.cos(aY)
                s = kotlin.math.sin(aY)
                x = x * c - z * s
                z = x * s + z * c
                c = kotlin.math.cos(aZ)
                s = kotlin.math.sin(aZ)
                GL11.glVertex3d(
                    x * c - y * s,
                    x * s + y * c,
                    z,
                )
            }
        }
    }

    override fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        rotation: Rotation,
        scale: Double,
    ) {
        if (edgeCount < 3) TODO()
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0 until edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                //
//                var x = center.x + kotlin.math.cos(radians) * radius
//                var y = center.y + kotlin.math.sin(radians) * radius
//                var c = kotlin.math.cos(rotation.aX)
//                var s = kotlin.math.sin(rotation.aX)
//                y = y * c - center.z * s
//                var z = y * s + center.z * c
//                c = kotlin.math.cos(rotation.aY)
//                s = kotlin.math.sin(rotation.aY)
//                x = x * c - z * s
//                z = x * s + z * c
//                c = kotlin.math.cos(rotation.aZ)
//                s = kotlin.math.sin(rotation.aZ)
//                GL11.glVertex3d(
//                    measure.transform(x * c - y * s + offset.dX),
//                    measure.transform(x * s + y * c + offset.dY),
//                    measure.transform(z + offset.dZ),
//                )
                //
                GLUtil.vertexOf(
                    x = center.x + kotlin.math.cos(radians) * radius,
                    y = center.y + kotlin.math.sin(radians) * radius,
                    z = center.z,
                    offset = offset,
                    rotation = rotation,
                    scale = scale,
                )
            }
        }
    }
}
