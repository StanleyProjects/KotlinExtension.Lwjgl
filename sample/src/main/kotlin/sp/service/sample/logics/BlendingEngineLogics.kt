package sp.service.sample.logics

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableOffset
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.angleOf
import sp.kx.math.copy
import sp.kx.math.div
import sp.kx.math.isEmpty
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.speedOf
import sp.kx.math.pointOf
import sp.kx.math.sizeOf
import sp.kx.math.times
import sp.kx.math.vectorOf

internal class BlendingEngineLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                KeyboardButton.Minus -> {
                    if (measure.magnitude > step) {
                        setMagnitude(magnitude = measure.magnitude - step)
                    }
                }
                KeyboardButton.Equal -> {
                    if (measure.magnitude < step * 16.0) {
                        setMagnitude(magnitude = measure.magnitude + step)
                    }
                }
                else -> Unit
            }
        }
    }
    private val step = 8.0
    private val measure = MutableDoubleMeasure(step * 3)
    private val offset = MutableOffset(2.0, 2.0)

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

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
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
        val offset = getOffset(keyboard = engine.input.keyboard)
        if (offset.isEmpty()) return
        val length = speedOf(8.0).length(engine.property.time.diff())
        val angle = angleOf(offset)
        this.offset.add(
            dX = length * kotlin.math.cos(angle),
            dY = length * kotlin.math.sin(angle),
        )
    }

    private fun onRenderOffset(canvas: Canvas, offset: Offset) {
        val ps = engine.property.pictureSize / measure
        val color = Color.Yellow.copy(alpha = 0.5f)
        for (it in 2..ps.width.toInt()) {
            val dX = it - offset.dX
            val value = java.lang.Math.floor(dX).toInt()
            if (value != 0) continue // todo
            val x = offset.dX + value
            canvas.texts.draw(
                color = color,
                fontHeight = 0.75,
                pointTopLeft = Point.Center.copy(x = x),
                text = "$value",
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = vectorOf(x, 0.0, x, ps.height) * measure,
            )
            break // todo
        }
        for (it in 2..ps.height.toInt()) {
            val dY = it - offset.dY
            val value = java.lang.Math.floor(dY).toInt()
            if (value != 0) continue // todo
            val y = offset.dY + value
            canvas.texts.draw(
                color = color,
                fontHeight = 0.75,
                pointTopLeft = Point.Center.copy(y = y),
                text = "$value",
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = vectorOf(0.0, y, ps.width, y) * measure,
            )
            break // todo
        }
    }

    private fun onRenderPolygons(canvas: Canvas, dY: Double, sfactor: Int, dfactor: Int, title: String) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        canvas.texts.draw(
            color = Color.Black,
            pointTopLeft = pointOf(2.0, dY),
            fontHeight = 1.0,
            text = title,
            offset = offset,
            measure = measure
        )
        val size = sizeOf(4.0, 4.0)
        listOf(
            Color.Red to Color.Yellow.copy(alpha = 0.5f),
            Color.Red.copy(alpha = 0.5f) to Color.Yellow,
            Color.Red.copy(alpha = 0.5f) to Color.Yellow.copy(alpha = 0.5f),
        ).forEachIndexed { index, (c1, c2) ->
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            canvas.texts.draw(
                color = Color.Black,
                pointTopLeft = pointOf(2.0 + 6.0 * index, dY + 1.0),
                fontHeight = 0.5,
                text = "$c1/$c2",
                offset = offset,
                measure = measure
            )
            GL11.glBlendFunc(sfactor, dfactor)
            canvas.polygons.drawRectangle(
                color = c1,
                pointTopLeft = pointOf(2.0 + 6.0 * index, dY + 2.0),
                size = size,
                offset = offset,
                measure = measure,
            )
            canvas.polygons.drawRectangle(
                color = c2,
                pointTopLeft = pointOf(2.0 + 6.0 * index + 1.0, dY + 3.0),
                size = size,
                offset = offset,
                measure = measure,
            )
        }
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val pictureSize = engine.property.pictureSize
        //
        onPreRender()
        canvas.polygons.drawRectangle(
            color = Color.White,
            pointTopLeft = pointOf(1.0, 1.0),
            size = sizeOf(24, 24 * 6),
            offset = offset,
            measure = measure
        )
        onRenderOffset(canvas = canvas, offset = offset)
        listOf(
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_ZERO, "GL_SRC_ALPHA/GL_ZERO"),
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_ONE, "GL_SRC_ALPHA/GL_ONE"),
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_SRC_COLOR, "GL_SRC_ALPHA/GL_SRC_COLOR"),
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_COLOR, "GL_SRC_ALPHA/GL_ONE_MINUS_SRC_COLOR"),
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_DST_COLOR, "GL_SRC_ALPHA/GL_DST_COLOR"),
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_COLOR, "GL_SRC_ALPHA/GL_ONE_MINUS_DST_COLOR"),
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_SRC_ALPHA, "GL_SRC_ALPHA/GL_SRC_ALPHA"),
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, "GL_SRC_ALPHA/GL_ONE_MINUS_SRC_ALPHA"),
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA, "GL_SRC_ALPHA/GL_DST_ALPHA"),
            Triple(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA, "GL_SRC_ALPHA/GL_ONE_MINUS_DST_ALPHA"),
        ).forEachIndexed { index, (sfactor, dfactor, title) ->
            onRenderPolygons(canvas = canvas, dY = 2.0 + 8.0 * index, sfactor = sfactor, dfactor = dfactor, title = title)
        }
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = pictureSize.width - 128.0, y = pictureSize.height - fontHeight * 2),
        )
        listOf(
            String.format("m: %6.2f", measure.magnitude),
        ).forEachIndexed { index, text ->
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = fontHeight,
                text = text,
                pointTopLeft = pointOf(x = fontHeight * 2, y = pictureSize.height - fontHeight * (index + 2)),
            )
        }
    }
}
