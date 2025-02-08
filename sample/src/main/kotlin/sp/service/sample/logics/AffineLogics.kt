package sp.service.sample.logics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.MutableOffset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.div
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.speedOf
import sp.kx.math.pointOf
import sp.kx.math.radians
import sp.kx.math.sizeOf
import sp.service.sample.MutableMatrix
import sp.service.sample.identity
import sp.service.sample.onMatrix
import sp.service.sample.ortho
import sp.service.sample.rotateX
import sp.service.sample.rotateY

internal class AffineLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                else -> Unit
            }
        }
    }
    private val measure = MutableDoubleMeasure(24.0)
    private val offset = engine.property.let {
        val ps = engine.property.pictureSize / measure
        MutableOffset(ps.width / 2, ps.height / 2)
    }

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private val matrix = MutableMatrix()
    private val buffer = BufferUtils.createDoubleBuffer(16)

    private fun rectangleXY(x: Double, y: Double, z: Double, w: Double, h: Double) {
//        GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
//        GL11.glVertex3d(x, y, z)
//        GL11.glVertex3d(x + w, y, z)
//        GL11.glVertex3d(x, y + h, z)
//        GL11.glVertex3d(x + w, y + h, z)
//        GL11.glEnd()
        line(x, y, z, x + w, y, z)
        line(x, y, z, x, y + h, z)
        line(x + w, y, z, x + w, y + h, z)
        line(x, y + h, z, x + w, y + h, z)
    }

    private fun rectangleXZ(x: Double, y: Double, z: Double, w: Double, d: Double) {
//        GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
//        GL11.glVertex3d(x, y, z)
//        GL11.glVertex3d(x + w, y, z)
//        GL11.glVertex3d(x, y, z + d)
//        GL11.glVertex3d(x + w, y, z + d)
//        GL11.glEnd()
        line(x, y, z, x + w, y, z)
        line(x, y, z, x, y, z + d)
        line(x + w, y, z, x + w, y, z + d)
        line(x, y, z + d, x + w, y, z + d)
    }

    private fun rectangleYZ(x: Double, y: Double, z: Double, h: Double, d: Double) {
//        GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
//        GL11.glVertex3d(x, y, z)
//        GL11.glVertex3d(x, y + h, z)
//        GL11.glVertex3d(x, y, z + d)
//        GL11.glVertex3d(x, y + h, z + d)
//        GL11.glEnd()
        line(x, y, z, x, y + h, z)
        line(x, y, z, x, y, z + d)
        line(x, y + h, z, x, y + h, z + d)
        line(x, y, z + d, x, y + h, z + d)
    }

    private fun line(
        x0: Double, y0: Double, z0: Double,
        x1: Double, y1: Double, z1: Double,
    ) {
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex3d(x0, y0, z0)
        GL11.glVertex3d(x1, y1, z1)
        GL11.glEnd()
    }

    private var angle = 0.0

    private fun onPreRender() {
        val diff = engine.property.time.diff()
        if (engine.input.keyboard.isPressed(KeyboardButton.Up)) {
            angle = (angle + speedOf(0.5).length(diff)).radians()
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Down)) {
            angle = (angle - speedOf(0.5).length(diff)).radians()
        }
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        onPreRender()
        //
//        val w = 128.0
//        val h = 128.0
//        val d = 128.0
        val w = 0.5
        val h = 0.5
        val d = 0.5
        matrix.identity()
//        matrix.ortho(width = ps.width, height = ps.height)
        matrix.rotateX(value = angle)
//        matrix.rotateY(value = angle)
//        matrix.ortho(r = ps.width, b = ps.height)
//        matrix.rotateX(value = angle)
//        matrix.rotateY(value = angle)
//        matrix.m22 = 0.0
//        val x0 = ps.width / 2 - w / 2
//        val y0 = ps.height / 2 - h / 2
        val x0 = -0.5
        val y0 = -0.5
        val z0 = +0.0
        onMatrix(matrix = matrix, buffer = buffer) {
            GLUtil.colorOf(Color.Red)
            rectangleXY(x = x0, y = y0, z = z0, w = w, h = h)
            GLUtil.colorOf(Color.Green)
            rectangleXZ(x = x0, y = y0, z = z0, w = w, d = d)
            GLUtil.colorOf(Color.Blue)
            rectangleYZ(x = x0, y = y0, z = z0, h = h, d = d)
            GLUtil.colorOf(Color.Yellow)
            rectangleYZ(x = x0 + w, y = y0, z = z0, h = h, d = d)
//            GLUtil.colorOf(Color.Gray)
//            line(0.0, ps.height / 2, z0, ps.width, ps.height / 2, z0)
//            GLUtil.colorOf(Color.Yellow.copy(alpha = 0.5f))
//            line(ps.width / 2, 0.0, z0, ps.width / 2, ps.height, z0)
//            GLUtil.colorOf(Color.Green.copy(alpha = 0.5f))
//            line(0.0, 0.0, z0 - d, 0.0, 0.0, z0 + d)
        }
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = ps.width - 128.0, y = ps.height - fontHeight * 2),
        )
        listOf(
            String.format("a: %+6.2f %+6.2f", angle, java.lang.Math.toDegrees(angle)),
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
