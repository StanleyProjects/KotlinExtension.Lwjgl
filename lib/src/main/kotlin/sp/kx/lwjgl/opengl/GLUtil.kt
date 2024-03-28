package sp.kx.lwjgl.opengl

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Color
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.measure.Measure
import sp.kx.math.plus

object GLUtil {
    fun clearColor(color: Color) {
        GL11.glClearColor(color.red, color.green, color.blue, color.alpha)
    }

    fun colorOf(color: Color) {
        GL11.glColor4f(color.red, color.green, color.blue, color.alpha)
    }

    fun vertexOf(first: Float, second: Float) {
        GL11.glVertex2f(first, second)
    }

    fun vertexOf(first: Double, second: Double) {
        GL11.glVertex2d(first, second)
    }

    fun vertexOf(point: Point) {
        vertexOf(point.x, point.y)
    }

    fun translated(x: Double, y: Double) {
        GL11.glTranslated(x, y, 0.0)
    }

    fun translated(point: Point) {
        translated(x = point.x, y = point.y)
    }

    fun translated(point: Point, offset: Offset) {
        translated(x = point.x + offset.dX, y = point.y + offset.dY)
    }

    fun translated(point: Point, measure: Measure<Double, Double>) {
        translated(x = measure.transform(point.x), y = measure.transform(point.y))
    }

    fun translated(point: Point, offset: Offset, measure: Measure<Double, Double>) {
        translated(x = measure.transform(point.x + offset.dX), y = measure.transform(point.y + offset.dY))
    }

    fun vertexOfMoved(
        point: Point,
        length: Double,
        angle: Double,
    ) {
        vertexOf(point.x + length * kotlin.math.cos(angle), point.y + length * kotlin.math.sin(angle))
    }

    fun vertexOf(point: Point, measure: Measure<Double, Double>) {
        vertexOf(measure.transform(point.x), measure.transform(point.y))
    }

    fun vertexOfMoved(
        point: Point,
        length: Double,
        angle: Double,
        measure: Measure<Double, Double>,
    ) {
        val x = point.x + length * kotlin.math.cos(angle)
        val y = point.y + length * kotlin.math.sin(angle)
        vertexOf(measure.transform(x), measure.transform(y))
    }

    fun vertexOf(point: Point, offset: Offset) {
        vertexOf(point.x + offset.dX, point.y + offset.dY)
    }

    fun vertexOfMoved(
        point: Point,
        length: Double,
        angle: Double,
        offset: Offset,
    ) {
        val x = point.x + length * kotlin.math.cos(angle) + offset.dX
        val y = point.y + length * kotlin.math.sin(angle) + offset.dY
        vertexOf(x, y)
    }

    fun vertexOf(point: Point, offset: Offset, measure: Measure<Double, Double>) {
        vertexOf(measure.transform(point.x + offset.dX), measure.transform(point.y + offset.dY))
    }

    fun vertexOfMoved(
        point: Point,
        length: Double,
        angle: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val x = point.x + length * kotlin.math.cos(angle) + offset.dX
        val y = point.y + length * kotlin.math.sin(angle) + offset.dY
        vertexOf(measure.transform(x), measure.transform(y))
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

    fun enabled(target: Int, block: () -> Unit) {
        GL11.glEnable(target)
        block()
        GL11.glDisable(target)
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
