package sp.kx.lwjgl.opengl

import org.lwjgl.opengl.GL11
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Rotation
import sp.kx.calculations.geometry.Vertex
import sp.kx.lwjgl.entity.Color
import java.nio.DoubleBuffer

object GLUtil {
    fun vertexOf(
        x: Double, y: Double, z: Double,
        dX: Double, dY: Double, dZ: Double,
    ) {
        GL11.glVertex3d(x + dX, y + dY, z + dZ)
    }

    fun vertexOf(
        x: Double, y: Double, z: Double,
        offset: Offset,
        scale: Double,
    ) {
        GL11.glVertex3d(
            (x + offset.dX) * scale,
            (y + offset.dY) * scale,
            (z + offset.dZ) * scale,
        )
    }

    fun vertexOf(
        x: Double, y: Double, z: Double,
        offset: Offset,
        rotation: Rotation,
        scale: Double,
    ) {
        //
//        var c = kotlin.math.cos(rotation.aX)
//        var s = kotlin.math.sin(rotation.aX)
//        var y1 = y * c - z * s
//        var z1 = y1 * s + z * c
//        c = kotlin.math.cos(rotation.aY)
//        s = kotlin.math.sin(rotation.aY)
//        var x1 = x * c - z1 * s
//        z1 = x1 * s + z1 * c
//        c = kotlin.math.cos(rotation.aZ)
//        s = kotlin.math.sin(rotation.aZ)
//        x1 = x1 * c - y1 * s
//        y1 = x1 * s + y1 * c
        //
//        var c = kotlin.math.cos(rotation.aX)
//        var s = kotlin.math.sin(rotation.aX)
//        var x = x
//        var y = y * c - z * s
//        var z = y * s + z * c
//        c = kotlin.math.cos(rotation.aY)
//        s = kotlin.math.sin(rotation.aY)
//        x = x * c - z * s
//        z = x * s + z * c
//        c = kotlin.math.cos(rotation.aZ)
//        s = kotlin.math.sin(rotation.aZ)
//        x = x * c - y * s
//        y = x * s + y * c
        //
        var c = kotlin.math.cos(rotation.aX)
        var s = kotlin.math.sin(rotation.aX)
        val y1 = y * c - z * s
        var z1 = y * s + z * c
        c = kotlin.math.cos(rotation.aY)
        s = kotlin.math.sin(rotation.aY)
        val x1 = x * c - z1 * s
        z1 = x * s + z1 * c
        c = kotlin.math.cos(rotation.aZ)
        s = kotlin.math.sin(rotation.aZ)
        //
        GL11.glVertex3d(
            (x1 * c - y1 * s + offset.dX) * scale,
            (x1 * s + y1 * c + offset.dY) * scale,
            (z1 +              offset.dZ) * scale,
        )
    }

    fun vertexOf(
        x: Double, y: Double, z: Double,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        scale: Double,
    ) {
        var x1 = x - about.x
        var y1 = y - about.y
        var z1 = z - about.z
        //
        var c = kotlin.math.cos(rotation.aX)
        var s = kotlin.math.sin(rotation.aX)
        y1 = y1 * c - z1 * s
        z1 = y1 * s + z1 * c
        c = kotlin.math.cos(rotation.aY)
        s = kotlin.math.sin(rotation.aY)
        x1 = x1 * c - z1 * s
        z1 = x1 * s + z1 * c
        c = kotlin.math.cos(rotation.aZ)
        s = kotlin.math.sin(rotation.aZ)
        //
        GL11.glVertex3d(
            (x1 * c - y1 * s + about.x + offset.dX) * scale,
            (x1 * s + y1 * c + about.y + offset.dY) * scale,
            (z1 + about.z +              offset.dZ) * scale,
        )
//        GL11.glVertex3d(
//            measure.transform(x1 * c - y1 * s + about.x),
//            measure.transform(x1 * s + y1 * c + about.y),
//            measure.transform(z1 + about.z),
//        )
    }

    fun vertexOf(
        vertex: Vertex,
        offset: Offset,
        rotation: Rotation,
        scale: Double,
    ) {
        vertexOf(
            x = vertex.x,
            y = vertex.y,
            z = vertex.z,
            offset = offset,
            rotation = rotation,
            scale = scale,
        )
    }

    fun vertexOf(
        vertex: Vertex,
        offset: Offset,
        about: Vertex,
        rotation: Rotation,
        scale: Double,
    ) {
        vertexOf(
            x = vertex.x,
            y = vertex.y,
            z = vertex.z,
            about = about,
            offset = offset,
            rotation = rotation,
            scale = scale,
        )
    }

    fun colorOf(color: Color) {
        GL11.glColor4ub(
            color.red,
            color.green,
            color.blue,
            color.alpha,
        )
    }

    fun transaction(mode: Int, block: () -> Unit) {
        GL11.glBegin(mode)
        block()
        GL11.glEnd()
    }

    fun ortho(
        buffer: DoubleBuffer,
        width: Double,
        height: Double,
        zNear: Double = 0.0,
        zFar: Double = 1.0,
    ) {
        val m00 = 2.0 / width
        val m11 = -2.0 / height
        val m22 = 2.0 / (zNear - zFar)
        val m30 = -1.0
        val m31 = 1.0
        val m32 = (zFar + zNear) / (zNear - zFar)
        buffer.put(0,  m00)
            .put(1,  0.0)
            .put(2,  0.0)
            .put(3,  0.0)
            .put(4,  0.0)
            .put(5,  m11)
            .put(6,  0.0)
            .put(7,  0.0)
            .put(8,  0.0)
            .put(9,  0.0)
            .put(10, m22)
            .put(11, 0.0)
            .put(12, m30)
            .put(13, m31)
            .put(14, m32)
            .put(15, 1.0)
    }
}
