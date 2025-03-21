package sp.service.sample.logics

import sp.kx.calculations.geometry.MutableVertex
import sp.kx.calculations.physics.frequency
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton

internal class OrthoLogics(
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

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private fun onPreRender() {
        // todo
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val pic = engine.property.picture
        onPreRender()
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            topLeft = MutableVertex(x = pic.size.width - 96.0, y = pic.size.height - fontHeight * 2, z = 0.0),
        )
    }
}
