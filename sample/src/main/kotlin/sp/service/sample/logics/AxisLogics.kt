package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableOffset
import sp.kx.math.Offset
import sp.kx.math.angleOf
import sp.kx.math.div
import sp.kx.math.isEmpty
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.speedOf
import sp.kx.math.pointOf
import sp.kx.math.radians

internal class AxisLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                KeyboardButton.C -> {
                    val ps = engine.property.pictureSize
                    offset.set(dX = ps.width / 2, dY = ps.height / 2)
                    aX = 0.0
                }
                else -> Unit
            }
        }
    }
    private val offset = engine.property.let {
        val ps = it.pictureSize
        MutableOffset(ps.width / 2, ps.height / 2)
    }
    private var aX = 0.0

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private fun getOffset(keyboard: Keyboard): Offset {
        val offset = MutableOffset(0.0, 0.0)
        val left = keyboard.isPressed(KeyboardButton.A)
        if (keyboard.isPressed(KeyboardButton.D)) {
            if (!left) offset.dX = 1.0
        } else if (left) {
            offset.dX = -1.0
        }
        val top = keyboard.isPressed(KeyboardButton.W)
        if (keyboard.isPressed(KeyboardButton.S)) {
            if (!top) offset.dY = 1.0
        } else if (top) {
            offset.dY = -1.0
        }
        return offset
    }

    private fun onPreRender() {
        val diff = engine.property.time.diff()
        val offset = getOffset(keyboard = engine.input.keyboard)
        if (!offset.isEmpty()) {
            val length = speedOf(8.0).length(diff)
            val angle = angleOf(offset)
            this.offset.add(
                dX = length * kotlin.math.cos(angle),
                dY = length * kotlin.math.sin(angle),
            )
        }
        val aS = speedOf(2.0)
        if (engine.input.keyboard.isPressed(KeyboardButton.Up)) {
            if (aX < kotlin.math.PI / 4) {
                aX = kotlin.math.min(kotlin.math.PI / 4, aX + aS.length(diff))
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Down)) {
            if (aX > - kotlin.math.PI / 4) {
                aX = kotlin.math.max(- kotlin.math.PI / 4, aX - aS.length(diff))
            }
        }
    }

    private fun onRenderOffset(canvas: Canvas, offset: Offset) {

    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        onPreRender()
        onRenderOffset(canvas = canvas, offset = offset)
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = ps.width - 96.0, y = ps.height - fontHeight * 2),
        )
        listOf(
            String.format("dX: %+6.2f", offset.dX - ps.width / 2),
            String.format("dY: %+6.2f", offset.dY - ps.height / 2),
            String.format("aX: %+6.2f", aX),
        ).forEachIndexed { index, text ->
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = fontHeight,
                text = text,
                pointTopLeft = pointOf(x = fontHeight, y = fontHeight * (1 + index)),
            )
        }
    }
}
