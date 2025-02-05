package sp.service.sample.logics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.MutableOffset
import sp.kx.math.MutablePoint
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.angleOf
import sp.kx.math.centerPoint
import sp.kx.math.copy
import sp.kx.math.div
import sp.kx.math.isEmpty
import sp.kx.math.measure.Measure
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.speedOf
import sp.kx.math.measure.times
import sp.kx.math.moved
import sp.kx.math.pointOf
import sp.kx.math.vectorOf
import sp.service.sample.Matrix
import sp.service.sample.MutableMatrix
import sp.service.sample.angleOf
import sp.service.sample.distanceOf
import java.nio.DoubleBuffer

internal class TranslateLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                KeyboardButton.Minus -> {
                    if (measure.magnitude > 8.0) {
                        setMagnitude(magnitude = measure.magnitude - 8.0)
                    }
                }
                KeyboardButton.Equal -> {
                    if (measure.magnitude < 64.0) {
                        setMagnitude(magnitude = measure.magnitude + 8.0)
                    }
                }
                KeyboardButton.O -> {
                    debug = !debug
                }
                else -> Unit
            }
        }
    }
    private val measure = MutableDoubleMeasure(24.0)
    private val offset = engine.property.let {
        val ps = engine.property.pictureSize / measure
        MutableOffset(ps.width / 2, ps.height / 2)
    }
    private var debug = true
    private val p1 = MutablePoint(4.0, 0.0)
    private val p2 = MutablePoint(0.0, 6.0)

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private fun setMagnitude(magnitude: Double) {
        val ps = engine.property.pictureSize
        val op = ps / measure
        val dw = op.width / 2 - offset.dX
        val dh = op.height / 2 - offset.dY
        measure.magnitude = magnitude
        val np = ps / measure
        offset.set(
            dX = np.width / 2 - dw,
            dY = np.height / 2 - dh,
        )
    }

    private fun getOffset(keyboard: Keyboard): Offset {
        val offset = MutableOffset(0.0, 0.0)
        val left = keyboard.isPressed(KeyboardButton.A)
        if (keyboard.isPressed(KeyboardButton.D)) {
            if (!left) offset.dX = -1.0
        } else if (left) {
            offset.dX = 1.0
        }
        val top = keyboard.isPressed(KeyboardButton.W)
        if (keyboard.isPressed(KeyboardButton.S)) {
            if (!top) offset.dY = -1.0
        } else if (top) {
            offset.dY = 1.0
        }
        return offset
    }

    private fun onPreRender() {
        val diff = engine.property.time.diff()
        val moved = getOffset(keyboard = engine.input.keyboard)
        if (moved.isEmpty()) {
            // todo
        } else {
            val length = speedOf(8.0).length(diff)
            val angle = angleOf(moved)
            this.offset.add(
                dX = length * kotlin.math.cos(angle),
                dY = length * kotlin.math.sin(angle),
            )
        }
        p1.set(Point.Center.moved(length = distanceOf(p1), angle = angleOf(p1) + speedOf(1.0).length(diff)))
        p2.set(Point.Center.moved(length = distanceOf(p2), angle = angleOf(p2) - speedOf(1.5).length(diff)))
    }

    private fun onRenderOffset(canvas: Canvas, offset: Offset) {
        val ps = engine.property.pictureSize
        val psu = ps / measure
        for (it in 2..psu.width.toInt()) {
            val dX = it - offset.dX
            val value = java.lang.Math.floor(dX).toInt()
            val x = offset.dX + value
            val color = when {
                value == 0 -> Color.Yellow.copy(0.5f)
                value % 2 == 0 -> Color.Gray
                else -> Color.Gray.copy(0.5f)
            }
            canvas.texts.draw(
                color = color,
                fontHeight = 0.75,
                pointTopLeft = Point.Center.copy(x = x),
                text = "$value",
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = vectorOf(x, 0.0, x, psu.height),
                measure = measure,
            )
        }
        for (it in 2..psu.height.toInt()) {
            val dY = it - offset.dY
            val value = java.lang.Math.floor(dY).toInt()
            val y = offset.dY + value
            val color = when {
                value == 0 -> Color.Yellow.copy(0.5f)
                value % 2 == 0 -> Color.Gray
                else -> Color.Gray.copy(0.5f)
            }
            canvas.texts.draw(
                color = color,
                fontHeight = 0.75,
                pointTopLeft = Point.Center.copy(y = y),
                text = "$value",
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = vectorOf(0.0, y, psu.width, y),
                measure = measure,
            )
        }
        canvas.vectors.draw(
            color = Color.Green.copy(alpha = 0.5f),
            vector = vectorOf(ps.width / 2, 0.0, ps.width / 2, ps.height),
        )
        canvas.vectors.draw(
            color = Color.Green.copy(alpha = 0.5f),
            vector = vectorOf(0.0, ps.height / 2, ps.width, ps.height / 2),
        )
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

    private fun scale(matrix: MutableMatrix, dX: Double, dY: Double, dZ: Double = 1.0) {
        matrix.m00 *= dX
        matrix.m01 *= dX
        matrix.m02 *= dX
        matrix.m03 *= dX
        matrix.m10 *= dY
        matrix.m11 *= dY
        matrix.m12 *= dY
        matrix.m13 *= dY
        matrix.m20 *= dZ
        matrix.m21 *= dZ
        matrix.m22 *= dZ
        matrix.m23 *= dZ
    }

    private fun scale(matrix: MutableMatrix, value: Double) {
        scale(
            matrix = matrix,
            dX = value,
            dY = value,
            dZ = value,
        )
    }

    private fun scale(matrix: MutableMatrix, measure: Measure<Double, Double>) {
        matrix.m00 *= measure
        matrix.m01 *= measure
        matrix.m02 *= measure
        matrix.m03 *= measure
        matrix.m10 *= measure
        matrix.m11 *= measure
        matrix.m12 *= measure
        matrix.m13 *= measure
        matrix.m20 *= measure
        matrix.m21 *= measure
        matrix.m22 *= measure
        matrix.m23 *= measure
    }

    private fun translate(matrix: MutableMatrix, dX: Double, dY: Double, dZ: Double = 0.0) {
        matrix.m30 = Math.fma(matrix.m00, dX, Math.fma(matrix.m10, dY, Math.fma(matrix.m20, dZ, matrix.m30)))
        matrix.m31 = Math.fma(matrix.m01, dX, Math.fma(matrix.m11, dY, Math.fma(matrix.m21, dZ, matrix.m31)))
        matrix.m32 = Math.fma(matrix.m02, dX, Math.fma(matrix.m12, dY, Math.fma(matrix.m22, dZ, matrix.m32)))
    }

    private fun translate(matrix: MutableMatrix, offset: Offset, dZ: Double = 0.0) {
        translate(
            matrix = matrix,
            dX = offset.dX,
            dY = offset.dY,
            dZ = dZ,
        )
    }

    private fun translate(matrix: MutableMatrix, dX: Double, dY: Double, dZ: Double = 0.0, measure: Measure<Double, Double>) {
        translate(
            matrix = matrix,
            dX = measure.transform(dX),
            dY = measure.transform(dY),
            dZ = measure.transform(dZ),
        )
    }

    private fun translate(matrix: MutableMatrix, offset: Offset, dZ: Double = 0.0, measure: Measure<Double, Double>) {
        translate(
            matrix = matrix,
            dX = measure.transform(offset.dX),
            dY = measure.transform(offset.dY),
            dZ = measure.transform(dZ),
        )
    }

    fun ortho(
        matrix: MutableMatrix,
        width: Double,
        height: Double,
    ) {
        matrix.m00 = 2.0 / width
        matrix.m11 = -2.0 / height
        matrix.m22 = -2.0
        matrix.m30 = -1.0
        matrix.m31 = 1.0
        matrix.m32 = -1.0
    }

    fun ortho(
        matrix: MutableMatrix,
        size: Size,
    ) {
        matrix.m00 = 2.0 / size.width
        matrix.m11 = -2.0 / size.height
        matrix.m22 = -2.0
        matrix.m30 = -1.0
        matrix.m31 = 1.0
        matrix.m32 = -1.0
    }

    fun identity(matrix: MutableMatrix) {
        matrix.m00 = 1.0
        matrix.m01 = 0.0
        matrix.m02 = 0.0
        matrix.m03 = 0.0
        matrix.m10 = 0.0
        matrix.m11 = 1.0
        matrix.m12 = 0.0
        matrix.m13 = 0.0
        matrix.m20 = 0.0
        matrix.m21 = 0.0
        matrix.m22 = 1.0
        matrix.m23 = 0.0
        matrix.m30 = 0.0
        matrix.m31 = 0.0
        matrix.m32 = 0.0
        matrix.m33 = 1.0
    }

    private val matrix = MutableMatrix()
    private val buffer = BufferUtils.createDoubleBuffer(16)

    private fun onMatrix(matrix: Matrix, block: () -> Unit) {
        GL11.glPushMatrix()
        load(buffer = buffer, matrix = matrix)
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glLoadMatrixd(buffer)
        block()
        GL11.glPopMatrix()
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        val psu = ps / measure
        onPreRender()
        if (debug) onRenderOffset(canvas = canvas, offset = offset)
        //
        identity(matrix = matrix)
        ortho(matrix = matrix, size = ps)
//        translate(matrix = matrix, offset = offset)
        translate(matrix = matrix, offset = offset, measure = measure)
        scale(matrix = matrix, measure = measure)
        onMatrix(matrix = matrix) {
//            GLUtil.colorOf(Color.Green.copy(alpha = 0.5f))
//            GLUtil.transaction(GL11.GL_LINES) {
//                GL11.glVertex3d(0.0, 0.0, 0.0)
//                GL11.glVertex3d(0.0, 0.0, 100.0)
//            }
//            GLUtil.colorOf(Color.Blue.copy(alpha = 0.5f))
//            GLUtil.transaction(GL11.GL_LINES) {
//                GL11.glVertex3d(0.0, 0.0, 0.0)
//                GL11.glVertex3d(0.0, 0.0, -100.0)
//            }
            canvas.polygons.drawCircle(
                color = Color.Red,
                pointCenter = Point.Center,
                radius = 0.25,
                edgeCount = 4,
//                measure = measure,
            )
            canvas.polygons.drawCircle(
                color = Color.Green,
                pointCenter = p1,
                radius = 0.25,
                edgeCount = 4,
//                measure = measure,
            )
            canvas.polygons.drawCircle(
                color = Color.Blue,
                pointCenter = p2,
                radius = 0.25,
                edgeCount = 4,
//                measure = measure,
            )
        }
        canvas.polygons.drawCircle(
            color = Color.Yellow,
            pointCenter = psu.centerPoint(),
            radius = 0.25,
            edgeCount = 4,
            measure = measure,
        )
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = ps.width - 128.0, y = ps.height - fontHeight * 2),
        )
        val p = Point.Center
        listOf(
            String.format("m: %6.2f", measure.magnitude),
            String.format("o: %+6.2f %+6.2f", offset.dX, offset.dY),
            String.format("c: $p"),
        ).forEachIndexed { index, text ->
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = fontHeight,
                text = text,
                pointTopLeft = pointOf(x = fontHeight * 2, y = ps.height - fontHeight * (2 + index)),
            )
        }
    }
}
