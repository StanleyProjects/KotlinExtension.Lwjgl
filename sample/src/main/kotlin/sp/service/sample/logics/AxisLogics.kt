package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableOffset
import sp.kx.math.MutableVertex
import sp.kx.math.Offset
import sp.kx.math.frequency
import sp.kx.math.diff
import sp.service.sample.isEmpty
import sp.service.sample.angleOf
import sp.service.sample.length
import java.util.concurrent.TimeUnit

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
                    offset.dX = ps.width / 2
                    offset.dY = ps.height / 2
                    offset.dZ = 0.0
                    aX = 0.0
                }
                else -> Unit
            }
        }
    }
    private val offset = engine.property.let {
        val ps = it.pictureSize
        MutableOffset(dX = ps.width / 2, dY = ps.height / 2, dZ = 0.0)
    }
    private var aX = 0.0

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private fun getOffset(keyboard: Keyboard): Offset {
        val offset = MutableOffset(dX = 0.0, dY = 0.0, dZ = 0.0)
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
            val length = length(8.0, TimeUnit.SECONDS, diff)
            val angle = angleOf(x = offset.dX, y = offset.dY)
            this.offset.dX += length * kotlin.math.cos(angle)
            this.offset.dY += length * kotlin.math.sin(angle)
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Up)) {
            if (aX < kotlin.math.PI / 4) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                aX = kotlin.math.min(kotlin.math.PI / 4, aX + radians)
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Down)) {
            if (aX > - kotlin.math.PI / 4) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                aX = kotlin.math.max(- kotlin.math.PI / 4, aX - radians)
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
            topLeft = MutableVertex(x = ps.width - 96.0, y = ps.height - fontHeight * 2, z = 0.0),
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
                topLeft = MutableVertex(x = fontHeight, y = fontHeight * (1 + index), z = 0.0),
            )
        }
    }
}
