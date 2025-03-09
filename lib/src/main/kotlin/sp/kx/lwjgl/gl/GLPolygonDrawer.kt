package sp.kx.lwjgl.gl

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.drawer.PolygonDrawer
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Measure
import sp.kx.math.Offset
import sp.kx.math.Rotation
import sp.kx.math.Vertex

internal object GLPolygonDrawer : PolygonDrawer {
    override fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        measure: Measure<Double, Double>
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
                    measure = measure,
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
        measure: Measure<Double, Double>
    ) {
        drawCircle(
            color = color,
            x = measure.transform(center.x),
            y = measure.transform(center.y),
            z = measure.transform(center.z),
            radius = measure.transform(radius),
            edgeCount = edgeCount,
        )
    }

    override fun drawCircle(
        color: Color,
        center: Vertex,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        measure: Measure<Double, Double>
    ) {
        drawCircle(
            color = color,
            x = measure.transform(center.x + offset.dX),
            y = measure.transform(center.y + offset.dY),
            z = measure.transform(center.z + offset.dZ),
            radius = measure.transform(radius),
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
        measure: Measure<Double, Double>,
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
                    measure = measure,
                )
            }
        }
    }
}
