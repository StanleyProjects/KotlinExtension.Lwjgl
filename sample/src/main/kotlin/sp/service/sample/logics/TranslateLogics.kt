package sp.service.sample.logics

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
import sp.kx.math.vectorOf

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
        val moved = getOffset(keyboard = engine.input.keyboard)
        if (moved.isEmpty()) {
            // todo
        } else {
            val length = speedOf(8.0).length(engine.property.time.diff())
            val angle = angleOf(moved)
            this.offset.add(
                dX = length * kotlin.math.cos(angle),
                dY = length * kotlin.math.sin(angle),
            )
        }
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

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        onPreRender()
        if (debug) onRenderOffset(canvas = canvas, offset = offset)
        //
        // todo
        //
        val ps = engine.property.pictureSize
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = ps.width - 128.0, y = ps.height - fontHeight * 2),
        )
        listOf(
            String.format("m: %6.2f", measure.magnitude),
            String.format("o: %+6.2f %+6.2f", offset.dX, offset.dY),
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
