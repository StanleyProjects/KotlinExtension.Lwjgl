package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.Point
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.div
import sp.kx.math.measure.times
import sp.kx.math.offsetOf
import sp.kx.math.sizeOf

internal class TextsEngineLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Minus -> {
                    when (measure.magnitude) {
                        24.0 -> measure.magnitude = 16.0
                        32.0 -> measure.magnitude = 24.0
                        40.0 -> measure.magnitude = 32.0
                        48.0 -> measure.magnitude = 40.0
                        56.0 -> measure.magnitude = 48.0
                        64.0 -> measure.magnitude = 56.0
                        else -> Unit
                    }
                }
                KeyboardButton.Equal -> {
                    when (measure.magnitude) {
                        16.0 -> measure.magnitude = 24.0
                        24.0 -> measure.magnitude = 32.0
                        32.0 -> measure.magnitude = 40.0
                        40.0 -> measure.magnitude = 48.0
                        48.0 -> measure.magnitude = 56.0
                        56.0 -> measure.magnitude = 64.0
                        else -> Unit
                    }
                }
                KeyboardButton.Escape -> ses = Unit
                else -> Unit
            }
        }
    }
    private val measure = MutableDoubleMeasure(48.0)

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    override fun onRender(canvas: Canvas) {
        val fontHeight = 1.0
        val text = "Hello world!"
        val offset = offsetOf(1, 1)
        val pointTopLeft = Point.Center
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = text,
            pointTopLeft = pointTopLeft,
            offset = offset,
            measure = measure,
        )
        val width = canvas.texts.getTextUnits(fontHeight = fontHeight, text = text, measure = measure)
        canvas.polygons.drawRectangle(
            color = Color.Yellow,
            pointTopLeft = pointTopLeft,
            size = sizeOf(height = fontHeight, width = width),
            lineWidth = 0.05,
            offset = offset,
            measure = measure,
        )
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = 24.0,
            text = "measure: ${measure.magnitude}",
            pointTopLeft = Point.Center,
            offset = offsetOf(dX = 0.0, engine.property.pictureSize.height - 24.0),
        )
    }
}
