package sp.kx.lwjgl.glfw

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.VectorDrawer
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Offset
import sp.kx.math.Vector
import sp.kx.math.angle
import sp.kx.math.angleOf
import sp.kx.math.measure.Measure
import sp.kx.math.moved

internal object GLVectorDrawer : VectorDrawer {
    override fun draw(color: Color, vector: Vector, lineWidth: Double) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val angle = vector.angle()
            GLUtil.vertexOfMoved(vector.start, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2)
            GLUtil.vertexOfMoved(vector.start, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2)
            GLUtil.vertexOfMoved(vector.finish, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2)
            GLUtil.vertexOfMoved(vector.finish, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2)
        }
    }

    override fun draw(color: Color, vector: Vector, offset: Offset, lineWidth: Double) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val angle = vector.angle()
            GLUtil.vertexOfMoved(vector.start, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, offset = offset)
            GLUtil.vertexOfMoved(vector.start, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, offset = offset)
            GLUtil.vertexOfMoved(vector.finish, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, offset = offset)
            GLUtil.vertexOfMoved(vector.finish, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, offset = offset)
        }
    }

    override fun draw(color: Color, vector: Vector, measure: Measure<Double, Double>, lineWidth: Double) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val angle = vector.angle()
            GLUtil.vertexOfMoved(vector.start, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, measure = measure)
            GLUtil.vertexOfMoved(vector.start, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, measure = measure)
            GLUtil.vertexOfMoved(vector.finish, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, measure = measure)
            GLUtil.vertexOfMoved(vector.finish, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, measure = measure)
        }
    }

    override fun draw(
        color: Color,
        vector: Vector,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Double,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            val angle = vector.angle()
            GLUtil.vertexOfMoved(vector.start, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, offset = offset, measure = measure)
            GLUtil.vertexOfMoved(vector.start, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, offset = offset, measure = measure)
            GLUtil.vertexOfMoved(vector.finish, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, offset = offset, measure = measure)
            GLUtil.vertexOfMoved(vector.finish, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, offset = offset, measure = measure)
        }
    }
}
