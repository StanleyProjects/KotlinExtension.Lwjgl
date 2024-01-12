package sp.kx.lwjgl.glfw

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Offset
import sp.kx.math.Vector
import sp.kx.math.measure.Measure

internal object GLVectorDrawer : Canvas.VectorDrawer {
    override fun draw(color: Color, vector: Vector, lineWidth: Float) {
        GL11.glLineWidth(lineWidth)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINE_STRIP) {
            GLUtil.vertexOf(vector.start)
            GLUtil.vertexOf(vector.finish)
        }
    }

    override fun draw(color: Color, vector: Vector, offset: Offset, lineWidth: Float) {
        GL11.glLineWidth(lineWidth)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINE_STRIP) {
            GLUtil.vertexOf(point = vector.start, offset = offset)
            GLUtil.vertexOf(point = vector.finish, offset = offset)
        }
    }

    override fun draw(color: Color, vector: Vector, measure: Measure<Double, Double>, lineWidth: Float) {
        GL11.glLineWidth(lineWidth)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINE_STRIP) {
            GLUtil.vertexOf(point = vector.start, measure = measure)
            GLUtil.vertexOf(point = vector.finish, measure = measure)
        }
    }

    override fun draw(
        color: Color,
        vector: Vector,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Float
    ) {
        GL11.glLineWidth(lineWidth)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINE_STRIP) {
            GLUtil.vertexOf(point = vector.start, offset = offset, measure = measure)
            GLUtil.vertexOf(point = vector.finish, offset = offset, measure = measure)
        }
    }
}
