package sp.kx.lwjgl.opengl

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Color
import java.nio.DoubleBuffer

object GLUtil {
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
