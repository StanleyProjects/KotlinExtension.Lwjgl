package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.copy
import sp.kx.math.measure.frequency
import sp.kx.math.measure.measureOf
import sp.kx.math.offsetOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.sizeOf
import sp.service.sample.util.FontInfoUtil

class InputEngineLogics(private val engine: Engine) : EngineLogics {
    private lateinit var shouldEngineStopUnit: Unit

    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            when (button) {
                KeyboardButton.Escape -> {
                    if (!isPressed) {
                        shouldEngineStopUnit = Unit
                    }
                }
                else -> {
                    println("[InputEngineLogic]: on button: $button $isPressed")
                }
            }
        }
    }

    private val measure = measureOf(24.0)

    private fun drawButton(
        canvas: Canvas,
        fontInfo: FontInfo,
        pointTopLeft: Point,
        height: Double,
        width: Double,
        button: KeyboardButton,
        text: String = getText(button = button),
    ) {
        val isPressed = engine.input.keyboard.isPressed(button)
        val fontHeight = measure.units(fontInfo.height.toDouble())
        val textWidth = measure.units(engine.fontAgent.getTextWidth(fontInfo, text))
        canvas.texts.draw(
            info = fontInfo,
            color = if (isPressed) Color.Yellow else Color.Green,
            pointTopLeft = pointOf(
                x = pointTopLeft.x + width / 2 - textWidth / 2,
                y = pointTopLeft.y + height / 2 - fontHeight / 2,
            ),
            text = text,
            measure = measure,
        )
        if (isPressed) {
            canvas.polygons.drawRectangle(
                color = Color.Yellow,
                pointTopLeft = pointTopLeft,
                size = sizeOf(
                    width = width,
                    height = height,
                ),
                lineWidth = 0.1,
                measure = measure,
            )
        }
    }

    private fun getText(button: KeyboardButton): String {
        return when (button) {
            KeyboardButton.Escape -> "Esc"
            KeyboardButton.Shift -> "LSft"
            KeyboardButton.Control -> "LC"
            KeyboardButton.Alt -> "LA"
            KeyboardButton.Super -> "LS"
            KeyboardButton.Number0 -> "0"
            KeyboardButton.Number1 -> "1"
            KeyboardButton.Number2 -> "2"
            KeyboardButton.Number3 -> "3"
            KeyboardButton.Number4 -> "4"
            KeyboardButton.Number5 -> "5"
            KeyboardButton.Number6 -> "6"
            KeyboardButton.Number7 -> "7"
            KeyboardButton.Number8 -> "8"
            KeyboardButton.Number9 -> "9"
            KeyboardButton.Minus -> "-"
            KeyboardButton.Equal -> "="
            KeyboardButton.Backspace -> "<x"
            KeyboardButton.Enter -> "Ent"
            KeyboardButton.Left -> "<-"
            KeyboardButton.Right -> "->"
            KeyboardButton.Down -> "\\/"
            KeyboardButton.Up -> "/\\"
            else -> button.name
        }
    }

    private fun onRenderKeyboard(
        canvas: Canvas,
        fontInfo: FontInfo,
    ) {
        listOf(
            KeyboardButton.Escape,
            null, // todo tilda
            KeyboardButton.Tab,
            null, // todo caps lock
            KeyboardButton.Shift,
        ).forEachIndexed { index, button ->
            if (button != null) drawButton(
                canvas = canvas,
                fontInfo = fontInfo,
                pointTopLeft = pointOf(
                    x = 1.0,
                    y = 1.0 + 1.0 * index,
                ),
                height = 1.0,
                width = 2.0,
                button = button,
            )
        }
        listOf(
            KeyboardButton.Number1,
            KeyboardButton.Number2,
            KeyboardButton.Number3,
            KeyboardButton.Number4,
            KeyboardButton.Number5,
            KeyboardButton.Number6,
            KeyboardButton.Number7,
            KeyboardButton.Number8,
            KeyboardButton.Number9,
            KeyboardButton.Number0,
            KeyboardButton.Minus,
            KeyboardButton.Equal,
        ).forEachIndexed { index, button ->
            drawButton(
                canvas = canvas,
                fontInfo = fontInfo,
                pointTopLeft = pointOf(
                    x = 2.0 + index,
                    y = 2.0,
                ),
                height = 1.0,
                width = 1.0,
                button = button,
            )
        }
        listOf(
            KeyboardButton.Backspace,
            KeyboardButton.Enter,
        ).forEachIndexed { index, button ->
            drawButton(
                canvas = canvas,
                fontInfo = fontInfo,
                pointTopLeft = pointOf(
                    x = 14.0,
                    y = 2.0 + index,
                ),
                height = 1.0,
                width = 2.0,
                button = button,
            )
        }
        listOf(
            KeyboardButton.Control,
            KeyboardButton.Alt,
            KeyboardButton.Super,
        ).forEachIndexed { index, button ->
            drawButton(
                canvas = canvas,
                fontInfo = fontInfo,
                pointTopLeft = pointOf(
                    x = 1.0 + 1.0 * index,
                    y = 6.0,
                ),
                height = 1.0,
                width = 1.0,
                button = button,
            )
        }
        drawButton(
            canvas = canvas,
            fontInfo = fontInfo,
            pointTopLeft = pointOf(
                x = 13.0 + 1,
                y = 5.0,
            ),
            height = 1.0,
            width = 1.0,
            button = KeyboardButton.Up,
        )
        listOf(
            KeyboardButton.Left,
            KeyboardButton.Down,
            KeyboardButton.Right,
        ).forEachIndexed { index, button ->
            drawButton(
                canvas = canvas,
                fontInfo = fontInfo,
                pointTopLeft = pointOf(
                    x = 13.0 + index,
                    y = 6.0,
                ),
                height = 1.0,
                width = 1.0,
                button = button,
            )
        }
        drawButton(
            canvas = canvas,
            fontInfo = fontInfo,
            pointTopLeft = pointOf(
                x = 4.0,
                y = 6.0,
            ),
            height = 1.0,
            width = 7.0,
            button = KeyboardButton.Space,
        )
        listOf(
            listOf(KeyboardButton.Q, KeyboardButton.W, KeyboardButton.E, KeyboardButton.R, KeyboardButton.T, KeyboardButton.Y, KeyboardButton.U, KeyboardButton.I, KeyboardButton.O, KeyboardButton.P),
            listOf(KeyboardButton.A, KeyboardButton.S, KeyboardButton.D, KeyboardButton.F, KeyboardButton.G, KeyboardButton.H, KeyboardButton.J, KeyboardButton.K, KeyboardButton.L),
            listOf(KeyboardButton.Z, KeyboardButton.X, KeyboardButton.C, KeyboardButton.V, KeyboardButton.B, KeyboardButton.N, KeyboardButton.M)
        ).forEachIndexed { dY, row ->
            val width = 1.0
            val height = 1.0
            row.forEachIndexed { dX, button ->
                drawButton(
                    canvas = canvas,
                    fontInfo = fontInfo,
                    pointTopLeft = pointOf(
                        x = dX * width + 3.0,
                        y = dY * height + 3.0,
                    ),
                    height = 1.0,
                    width = 1.0,
                    button = button,
                )
            }
        }
    }

    private fun onRenderGrid(canvas: Canvas) {
        val max = 16
        val color = Color.Green.copy(alpha = 0.5f)
        val fontInfo = FontInfoUtil.getFontInfo(height = 0.75, measure = measure)
        (1..max).forEach { number ->
            canvas.texts.draw(
                color = color,
                info = fontInfo,
                pointTopLeft = pointOf(x = number, y = 0),
                text = "$number",
                measure = measure,
            )
            canvas.texts.draw(
                color = color,
                info = fontInfo,
                pointTopLeft = pointOf(x = 0, y = number),
                text = "$number",
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = pointOf(x = 0, y = number) + pointOf(x = max, y = number),
                lineWidth = 0.1,
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = pointOf(x = number, y = 0) + pointOf(x = number, y = max),
                lineWidth = 0.1,
                measure = measure,
            )
        }
    }

    override fun onRender(canvas: Canvas) {
        val fontInfo = FontInfoUtil.getFontInfo(height = 1.0, measure = measure)
        val fps = engine.property.time.frequency()
//        canvas.texts.draw(
//            info = fontInfo,
//            pointTopLeft = Point.Center,
//            color = Color.Green,
//            text = String.format("%.2f", fps),
//            measure = measure,
//        )
//        onRenderGrid(canvas = canvas)
        onRenderKeyboard(
            canvas = canvas,
            fontInfo = fontInfo,
        )
    }

    override fun shouldEngineStop(): Boolean {
        return ::shouldEngineStopUnit.isInitialized
    }
}
