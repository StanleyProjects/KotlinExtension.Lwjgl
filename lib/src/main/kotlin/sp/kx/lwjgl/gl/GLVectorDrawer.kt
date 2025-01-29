package sp.kx.lwjgl.gl

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.drawer.VectorDrawer
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Vector
import sp.kx.math.angle
import sp.kx.math.measure.Measure

internal object GLVectorDrawer : VectorDrawer {
    override fun draw(color: Color, vector: Vector) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINES) {
            GLUtil.vertexOf(vector.start)
            GLUtil.vertexOf(vector.finish)
        }
    }

    override fun draw(color: Color, vector: Vector, offset: Offset) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINES) {
            GLUtil.vertexOf(vector.start, offset = offset)
            GLUtil.vertexOf(vector.finish, offset = offset)
        }
    }

    override fun draw(color: Color, vector: Vector, measure: Measure<Double, Double>) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINES) {
            GLUtil.vertexOf(point = vector.start, measure = measure)
            GLUtil.vertexOf(point = vector.finish, measure = measure)
        }
    }

    override fun draw(color: Color, vector: Vector, offset: Offset, measure: Measure<Double, Double>) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINES) {
            GLUtil.vertexOf(vector.start, offset = offset, measure = measure)
            GLUtil.vertexOf(vector.finish, offset = offset, measure = measure)
        }
    }

    override fun draw(color: Color, vector: Vector, lineWidth: Double) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val angle = vector.angle()
            val length = java.lang.Math.round(lineWidth) / 2.0
            GLUtil.vertexOfMoved(point = vector.start, length = length, angle = angle - kotlin.math.PI / 2)
            GLUtil.vertexOfMoved(point = vector.start, length = length, angle = angle + kotlin.math.PI / 2)
            GLUtil.vertexOfMoved(point = vector.finish, length = length, angle = angle - kotlin.math.PI / 2)
            GLUtil.vertexOfMoved(point = vector.finish, length = length, angle = angle + kotlin.math.PI / 2)
        }
    }

    override fun draw(color: Color, vector: Vector, offset: Offset, lineWidth: Double) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val angle = vector.angle()
            val length = java.lang.Math.round(lineWidth) / 2.0
            GLUtil.vertexOfMoved(vector.start, length = length, angle = angle - kotlin.math.PI / 2, offset = offset)
            GLUtil.vertexOfMoved(vector.start, length = length, angle = angle + kotlin.math.PI / 2, offset = offset)
            GLUtil.vertexOfMoved(vector.finish, length = length, angle = angle - kotlin.math.PI / 2, offset = offset)
            GLUtil.vertexOfMoved(vector.finish, length = length, angle = angle + kotlin.math.PI / 2, offset = offset)
        }
    }

    override fun draw(color: Color, vector: Vector, measure: Measure<Double, Double>, lineWidth: Double) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val angle = vector.angle()
            val t = measure.transform(lineWidth)
            val r = java.lang.Math.round(t)
            val length = measure.units(r.toDouble()) / 2.0
            GLUtil.vertexOfMoved(vector.start, length = length, angle = angle - kotlin.math.PI / 2, measure = measure)
            GLUtil.vertexOfMoved(vector.start, length = length, angle = angle + kotlin.math.PI / 2, measure = measure)
            GLUtil.vertexOfMoved(vector.finish, length = length, angle = angle - kotlin.math.PI / 2, measure = measure)
            GLUtil.vertexOfMoved(vector.finish, length = length, angle = angle + kotlin.math.PI / 2, measure = measure)
        }
    }

    override fun draw(
        color: Color,
        vector: Vector,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Double,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val angle = vector.angle()
            val t = measure.transform(lineWidth)
            val r = java.lang.Math.round(t)
            val length = measure.units(r.toDouble()) / 2.0
            GLUtil.vertexOfMoved(vector.start, length = length, angle = angle - kotlin.math.PI / 2, offset = offset, measure = measure)
            GLUtil.vertexOfMoved(vector.start, length = length, angle = angle + kotlin.math.PI / 2, offset = offset, measure = measure)
            GLUtil.vertexOfMoved(vector.finish, length = length, angle = angle - kotlin.math.PI / 2, offset = offset, measure = measure)
            GLUtil.vertexOfMoved(vector.finish, length = length, angle = angle + kotlin.math.PI / 2, offset = offset, measure = measure)
        }
    }

    private fun vertexOf(
        vector: Vector,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val angle = vector.angle()
        val t = measure.transform(lineWidth)
        val r = java.lang.Math.round(t)
        val length = measure.units(r.toDouble()) / 2.0
        GLUtil.vertexOfMoved(vector.start, length = length, angle = angle - kotlin.math.PI / 2, offset = offset, measure = measure)
        GLUtil.vertexOfMoved(vector.start, length = length, angle = angle + kotlin.math.PI / 2, offset = offset, measure = measure)
        GLUtil.vertexOfMoved(vector.finish, length = length, angle = angle - kotlin.math.PI / 2, offset = offset, measure = measure)
        GLUtil.vertexOfMoved(vector.finish, length = length, angle = angle + kotlin.math.PI / 2, offset = offset, measure = measure)
    }

    override fun draw(
        color: Color,
        vectors: List<Vector>,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_LINES) {
            vectors.forEach {
                GLUtil.vertexOf(it.start, offset = offset, measure = measure)
                GLUtil.vertexOf(it.finish, offset = offset, measure = measure)
            }
        }
    }

    override fun draw(
        color: Color,
        vectors: List<Vector>,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Double,
    ) {
        GLUtil.colorOf(color)
        val first = vectors.first()
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            vertexOf(vector = first, lineWidth = lineWidth, offset = offset, measure = measure)
        }
        for (i in 1 until vectors.size) {
            GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
                vertexOf(vector = vectors[i], lineWidth = lineWidth, offset = offset, measure = measure)
            }
        }
    }
}
