package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableDoubleMeasure
import sp.kx.math.MutableOffset
import sp.kx.math.MutableVertex
import sp.kx.math.Offset
import sp.kx.math.diff
import sp.kx.math.frequency
import sp.service.sample.angleOf
import sp.service.sample.center
import sp.service.sample.div
import sp.service.sample.isEmpty
import sp.service.sample.length
import sp.service.sample.mut
import java.util.concurrent.TimeUnit

internal class TestLogics(
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
    private val offset = engine.property.pictureSize.div(measure).center(dZ = 0.0).mut()

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
            val radians = angleOf(x = offset.dX, y = offset.dY)
            this.offset.dX += length * kotlin.math.cos(radians)
            this.offset.dY += length * kotlin.math.sin(radians)
        }
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        val psu = ps / measure
        onPreRender()
        //
        canvas.polygons.drawCircle(
            color = Color.Green,
            center = MutableVertex(0.0, 0.0, 0.0),
            radius = 0.25,
            edgeCount = 4,
            offset = offset,
            measure = measure,
        )
        canvas.polygons.drawCircle(
            color = Color.Yellow,
            center = MutableVertex(0.0, 0.0, 0.0),
            radius = 0.25,
            edgeCount = 4,
            offset = psu.center(dZ = 0.0),
            measure = measure,
        )
        canvas.vectors.draw(
            color = Color.Gray,
            start = MutableVertex(x = 0.0, y = ps.height / 2, z = 0.0),
            finish = MutableVertex(x = ps.width, y = ps.height / 2, z = 0.0),
        )
        canvas.vectors.draw(
            color = Color.Gray,
            start = MutableVertex(x = ps.width / 2, y = 0.0, z = 0.0),
            finish = MutableVertex(x = ps.width / 2, y = ps.height, z = 0.0),
        )
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            topLeft = MutableVertex(x = ps.width - 96.0, y = ps.height - fontHeight * 2, z = 0.0),
        )
        listOf(
            String.format("dX: %+6.2f", offset.dX - psu.width / 2),
            String.format("dY: %+6.2f", offset.dY - psu.height / 2),
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
