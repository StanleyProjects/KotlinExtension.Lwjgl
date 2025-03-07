package sp.kx.lwjgl.gl

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.drawer.VectorDrawer
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Measure
import sp.kx.math.Offset
import sp.kx.math.Vertex

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

    override fun draw(color: Color, start: Vertex, finish: Vertex, measure: Measure<Double, Double>) {
        draw(
            color = color,
            x0 = measure.transform(start.x), y0 = measure.transform(start.y), z0 = measure.transform(start.z),
            x1 = measure.transform(finish.x), y1 = measure.transform(finish.y), z1 = measure.transform(finish.z),
        )
    }

    override fun draw(color: Color, start: Vertex, finish: Vertex, offset: Offset, measure: Measure<Double, Double>) {
        draw(
            color = color,
            x0 = measure.transform(start.x + offset.dX), y0 = measure.transform(start.y + offset.dY), z0 = measure.transform(start.z + offset.dZ),
            x1 = measure.transform(finish.x + offset.dX), y1 = measure.transform(finish.y + offset.dY), z1 = measure.transform(finish.z + offset.dZ),
        )
    }
}
