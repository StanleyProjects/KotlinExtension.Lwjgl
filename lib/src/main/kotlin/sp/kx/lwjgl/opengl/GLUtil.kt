package sp.kx.lwjgl.opengl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import sp.kx.calculations.algebra.Matrix
import sp.kx.calculations.rotations.rxyz
import sp.kx.lwjgl.entity.Color
import java.nio.DoubleBuffer

object GLUtil {
    fun vertexOf(
        x: Double, y: Double, z: Double,
        aX: Double, aY: Double, aZ: Double,
        rX: Double, rY: Double, rZ: Double,
        dX: Double, dY: Double, dZ: Double,
        scale: Double,
    ) {
        val vertex = rxyz(
            x = x, y = y, z = z,
            aX = aX, aY = aY, aZ = aZ,
            rX = rX, rY = rY, rZ = rZ,
        )
        GL11.glVertex3d(
            (vertex.x + dX) * scale,
            (vertex.y + dY) * scale,
            (vertex.z + dZ) * scale,
        )
    }

    fun vertexOf(
        x: Double, y: Double, z: Double,
        matrix: Matrix,
    ) {
        GL11.glVertex3d(
            matrix.m00 * x + matrix.m01 * y + matrix.m02 * z + matrix.m03,
            matrix.m10 * x + matrix.m11 * y + matrix.m12 * z + matrix.m13,
            matrix.m20 * x + matrix.m21 * y + matrix.m22 * z + matrix.m23,
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

    fun onMatrix(block: () -> Unit) {
        GL11.glPushMatrix()
        block()
        GL11.glPopMatrix()
    }

    private fun load(buffer: DoubleBuffer, matrix: Matrix) {
        buffer.put(0,  matrix.m00)
            .put(1,  matrix.m01)
            .put(2,  matrix.m02)
            .put(3,  matrix.m03)
            .put(4,  matrix.m10)
            .put(5,  matrix.m11)
            .put(6,  matrix.m12)
            .put(7,  matrix.m13)
            .put(8,  matrix.m20)
            .put(9,  matrix.m21)
            .put(10, matrix.m22)
            .put(11, matrix.m23)
            .put(12, matrix.m30)
            .put(13, matrix.m31)
            .put(14, matrix.m32)
            .put(15, matrix.m33)
    }

    private val buffer = BufferUtils.createDoubleBuffer(16)

    fun onMatrix(matrix: Matrix, block: () -> Unit) {
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPushMatrix()
        GL11.glLoadIdentity()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPushMatrix()
        load(buffer = buffer, matrix = matrix)
        GL11.glLoadMatrixd(buffer)
        block()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPopMatrix()
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPopMatrix()
    }

    fun onMatrix(pm: Matrix, mv: Matrix, block: () -> Unit) {
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPushMatrix()
        load(buffer = buffer, matrix = pm)
        GL11.glLoadMatrixd(buffer)
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPushMatrix()
        load(buffer = buffer, matrix = mv)
        GL11.glLoadMatrixd(buffer)
        block()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPopMatrix()
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPopMatrix()
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
