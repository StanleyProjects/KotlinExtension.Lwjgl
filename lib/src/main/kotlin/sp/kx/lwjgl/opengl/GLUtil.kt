package sp.kx.lwjgl.opengl

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Color
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.measure.Measure
import java.nio.DoubleBuffer

object GLUtil {
    fun clearColor(color: Color) {
        GL11.glClearColor(
            color.red.toInt().and(0xff).toFloat() / 256,
            color.green.toInt().and(0xff).toFloat() / 256,
            color.blue.toInt().and(0xff).toFloat() / 256,
            color.alpha.toInt().and(0xff).toFloat() / 256,
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

    fun vertexOf(first: Float, second: Float) {
        GL11.glVertex2f(first, second)
    }

    fun vertexOf(first: Double, second: Double) {
        GL11.glVertex2d(first, second)
    }

    fun vertexOf(x: Double, y: Double, offset: Offset) {
        GL11.glVertex2d(x + offset.dX, y + offset.dY)
    }

    fun vertexOf(x: Double, y: Double, measure: Measure<Double, Double>) {
        GL11.glVertex2d(measure.transform(x), measure.transform(y))
    }

    fun vertexOf(x: Double, y: Double, offset: Offset, measure: Measure<Double, Double>) {
        GL11.glVertex2d(measure.transform(x + offset.dX), measure.transform(y + offset.dY))
    }

    fun vertexOf(point: Point) {
        vertexOf(point.x, point.y)
    }

    fun vertexOf(pointTopLeft: Point, size: Size) {
        // #1
        vertexOf(point = pointTopLeft)
        // #2
        vertexOf(first = pointTopLeft.x + size.width, second = pointTopLeft.y)
        // #3
        vertexOf(first = pointTopLeft.x, second = pointTopLeft.y + size.height)
        // #4
        vertexOf(first = pointTopLeft.x + size.width, second = pointTopLeft.y + size.height)
    }

    fun vertexOf(pointTopLeft: Point, size: Size, offset: Offset) {
        // #1
        vertexOf(
            point = pointTopLeft,
            offset = offset,
        )
        // #2
        vertexOf(
            x = pointTopLeft.x + size.width,
            y = pointTopLeft.y,
            offset = offset,
        )
        // #3
        vertexOf(
            x = pointTopLeft.x,
            y = pointTopLeft.y + size.height,
            offset = offset,
        )
        // #4
        vertexOf(
            x = pointTopLeft.x + size.width,
            y = pointTopLeft.y + size.height,
            offset = offset,
        )
    }

    fun vertexOf(pointTopLeft: Point, size: Size, measure: Measure<Double, Double>) {
        // #1
        vertexOf(
            point = pointTopLeft,
            measure = measure,
        )
        // #2
        vertexOf(
            x = pointTopLeft.x + size.width,
            y = pointTopLeft.y,
            measure = measure,
        )
        // #3
        vertexOf(
            x = pointTopLeft.x,
            y = pointTopLeft.y + size.height,
            measure = measure,
        )
        // #4
        vertexOf(
            x = pointTopLeft.x + size.width,
            y = pointTopLeft.y + size.height,
            measure = measure,
        )
    }

    /**
     *
     * ```
     *  1    2
     * *----*
     * |    |
     * |3   |4
     * *----*
     * ```
     *
     **/
    fun vertexOf(pointTopLeft: Point, size: Size, offset: Offset, measure: Measure<Double, Double>) {
        // #1
        vertexOf(
            point = pointTopLeft,
            offset = offset,
            measure = measure,
        )
        // #2
        vertexOf(
            x = pointTopLeft.x + size.width,
            y = pointTopLeft.y,
            offset = offset,
            measure = measure,
        )
        // #3
        vertexOf(
            x = pointTopLeft.x,
            y = pointTopLeft.y + size.height,
            offset = offset,
            measure = measure,
        )
        // #4
        vertexOf(
            x = pointTopLeft.x + size.width,
            y = pointTopLeft.y + size.height,
            offset = offset,
            measure = measure,
        )
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

    private fun vertexOfMoved(
        point: Point,
        length: Double,
        a1: Double,
        a2: Double,
    ) {
        vertexOf(
            first = point.x + length * kotlin.math.cos(a1),
            second = point.y + length * kotlin.math.sin(a1),
        )
        vertexOf(
            first = point.x + length * kotlin.math.cos(a2),
            second = point.y + length * kotlin.math.sin(a2),
        )
    }

    fun vertexOfMoved(
        tl: Point,
        tr: Point,
        br: Point,
        bl: Point,
        lineWidth: Double,
    ) {
        // todo math
        val pi12 = kotlin.math.PI / 2
        val pi14 = kotlin.math.PI / 4
        val pi34 = pi12 + pi14
        val lw12 = lineWidth / 2
        val lw12s2 = lw12 * kotlin.math.sqrt(2.0)
        vertexOfMoved(
            point = tl,
            a1 = pi14,
            a2 = -pi34,
            length = lw12s2,
        )
        vertexOfMoved(
            point = tr,
            a1 = pi34,
            a2 = -pi14,
            length = lw12s2,
        )
        vertexOfMoved(
            point = br,
            a1 = -pi34,
            a2 = pi14,
            length = lw12s2,
        )
        vertexOfMoved(
            point = bl,
            a1 = -pi14,
            a2 = pi34,
            length = lw12s2,
        )
        vertexOfMoved(
            point = tl,
            a1 = pi14,
            a2 = -pi34,
            length = lw12s2,
        )
    }

    private fun vertexOfMoved(
        point: Point,
        length: Double,
        a1: Double,
        a2: Double,
        offset: Offset,
    ) {
        vertexOf(
            x = point.x + length * kotlin.math.cos(a1),
            y = point.y + length * kotlin.math.sin(a1),
            offset = offset,
        )
        vertexOf(
            x = point.x + length * kotlin.math.cos(a2),
            y = point.y + length * kotlin.math.sin(a2),
            offset = offset,
        )
    }

    fun vertexOfMoved(
        tl: Point,
        tr: Point,
        br: Point,
        bl: Point,
        lineWidth: Double,
        offset: Offset,
    ) {
        // todo math
        val pi12 = kotlin.math.PI / 2
        val pi14 = kotlin.math.PI / 4
        val pi34 = pi12 + pi14
        val lw12 = lineWidth / 2
        val lw12s2 = lw12 * kotlin.math.sqrt(2.0)
        vertexOfMoved(
            point = tl,
            a1 = pi14,
            a2 = -pi34,
            length = lw12s2,
            offset = offset,
        )
        vertexOfMoved(
            point = tr,
            a1 = pi34,
            a2 = -pi14,
            length = lw12s2,
            offset = offset,
        )
        vertexOfMoved(
            point = br,
            a1 = -pi34,
            a2 = pi14,
            length = lw12s2,
            offset = offset,
        )
        vertexOfMoved(
            point = bl,
            a1 = -pi14,
            a2 = pi34,
            length = lw12s2,
            offset = offset,
        )
        vertexOfMoved(
            point = tl,
            a1 = pi14,
            a2 = -pi34,
            length = lw12s2,
            offset = offset,
        )
    }

    private fun vertexOfMoved(
        point: Point,
        length: Double,
        a1: Double,
        a2: Double,
        measure: Measure<Double, Double>,
    ) {
        vertexOf(
            x = point.x + length * kotlin.math.cos(a1),
            y = point.y + length * kotlin.math.sin(a1),
            measure = measure,
        )
        vertexOf(
            x = point.x + length * kotlin.math.cos(a2),
            y = point.y + length * kotlin.math.sin(a2),
            measure = measure,
        )
    }

    fun vertexOfMoved(
        tl: Point,
        tr: Point,
        br: Point,
        bl: Point,
        lineWidth: Double,
        measure: Measure<Double, Double>,
    ) {
        // todo math
        val pi12 = kotlin.math.PI / 2
        val pi14 = kotlin.math.PI / 4
        val pi34 = pi12 + pi14
        val lw12 = lineWidth / 2
        val lw12s2 = lw12 * kotlin.math.sqrt(2.0)
        vertexOfMoved(
            point = tl,
            a1 = pi14,
            a2 = -pi34,
            length = lw12s2,
            measure = measure,
        )
        vertexOfMoved(
            point = tr,
            a1 = pi34,
            a2 = -pi14,
            length = lw12s2,
            measure = measure,
        )
        vertexOfMoved(
            point = br,
            a1 = -pi34,
            a2 = pi14,
            length = lw12s2,
            measure = measure,
        )
        vertexOfMoved(
            point = bl,
            a1 = -pi14,
            a2 = pi34,
            length = lw12s2,
            measure = measure,
        )
        vertexOfMoved(
            point = tl,
            a1 = pi14,
            a2 = -pi34,
            length = lw12s2,
            measure = measure,
        )
    }

    private fun vertexOfMoved(
        point: Point,
        length: Double,
        a1: Double,
        a2: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        vertexOf(
            x = point.x + length * kotlin.math.cos(a1),
            y = point.y + length * kotlin.math.sin(a1),
            offset = offset,
            measure = measure,
        )
        vertexOf(
            x = point.x + length * kotlin.math.cos(a2),
            y = point.y + length * kotlin.math.sin(a2),
            offset = offset,
            measure = measure,
        )
    }

    fun vertexOfMoved(
        tl: Point,
        tr: Point,
        br: Point,
        bl: Point,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        // todo math
        val pi12 = kotlin.math.PI / 2
        val pi14 = kotlin.math.PI / 4
        val pi34 = pi12 + pi14
        val lw12 = lineWidth / 2
        val lw12s2 = lw12 * kotlin.math.sqrt(2.0)
        vertexOfMoved(
            point = tl,
            a1 = pi14,
            a2 = -pi34,
            length = lw12s2,
            offset = offset,
            measure = measure,
        )
        vertexOfMoved(
            point = tr,
            a1 = pi34,
            a2 = -pi14,
            length = lw12s2,
            offset = offset,
            measure = measure,
        )
        vertexOfMoved(
            point = br,
            a1 = -pi34,
            a2 = pi14,
            length = lw12s2,
            offset = offset,
            measure = measure,
        )
        vertexOfMoved(
            point = bl,
            a1 = -pi14,
            a2 = pi34,
            length = lw12s2,
            offset = offset,
            measure = measure,
        )
        vertexOfMoved(
            point = tl,
            a1 = pi14,
            a2 = -pi34,
            length = lw12s2,
            offset = offset,
            measure = measure,
        )
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
        far: Double = 1.0,
    ) {
        GL11.glOrtho(
            left,
            right,
            bottom,
            top,
            near,
            far,
        )
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
