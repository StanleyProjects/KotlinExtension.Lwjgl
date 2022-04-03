package sp.kx.lwjgl.opengl

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.Point

object GLUtil {
    fun clearColor(color: Color) {
        GL11.glClearColor(color.red, color.green, color.blue, color.alpha)
    }

    fun colorOf(color: Color) {
        GL11.glColor4f(color.red, color.green, color.blue, color.alpha)
    }

    fun vertexOf(point: Point) {
        GL11.glVertex2d(point.x, point.y)
    }

    fun transaction(mode: Int, block: () -> Unit) {
        GL11.glBegin(mode)
        block()
        GL11.glEnd()
    }

    fun ortho(
        left: Double = 0.0,
        top: Double = 0.0,
        right: Double = 0.0,
        bottom: Double = 0.0,
        near: Double = 0.0,
        far: Double = 1.0
    ) {
        GL11.glOrtho(
            left,
            right,
            bottom,
            top,
            near,
            far
        )
    }

}
