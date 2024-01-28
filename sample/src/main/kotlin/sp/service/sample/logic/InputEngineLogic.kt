package sp.service.sample.logic

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.input.Joystick
import sp.kx.lwjgl.engine.input.JoystickMapper
import sp.kx.lwjgl.engine.input.JoystickMapping
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.entity.input.JoystickAxis
import sp.kx.lwjgl.entity.input.JoystickButton
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.util.drawCircle
import sp.kx.math.Point
import sp.kx.math.measure.diff
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.sizeOf
import sp.service.sample.util.Dualshock4JoystickMapping
import sp.service.sample.util.FlydigiMapping
import sp.service.sample.util.ResourceUtil
import sp.service.sample.util.XBoxSeriesJoystickMapping
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class InputEngineLogic(private val engine: Engine) : EngineLogic {
    private lateinit var shouldEngineStopUnit: Unit

    private fun getFontInfo(name: String, height: Float): FontInfo {
        return object : FontInfo {
            override val id: String = "${name}_${height}"
            override val height: Float = height

            override fun getInputStream(): InputStream {
                return ResourceUtil.requireResourceAsStream(name)
            }
        }
    }
    private fun getFontInfo(height: Float): FontInfo {
        return getFontInfo(name = "JetBrainsMono.ttf", height = height)
    }

    override val joystickMapper: JoystickMapper = object : JoystickMapper {
        override fun map(guid: String, buttons: ByteArray, axes: FloatArray): JoystickMapping? {
            when (guid) {
                "030000005e040000e002000003090000" -> {
                    if (buttons.size == 15 && axes.size == 6) {
                        return FlydigiMapping.Vader3Pro
                    }
                }
                "030000005e040000130b000013050000" -> {
                    if (buttons.size == 20 && axes.size == 6) {
                        return XBoxSeriesJoystickMapping
                    }
                }
                "030000004c050000cc09000000010000" -> {
                    if (buttons.size == 18 && axes.size == 6) {
                        return Dualshock4JoystickMapping
                    }
                }
            }
            return null
        }
    }

    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            when (button) {
                KeyboardButton.ESCAPE -> {
                    if (!isPressed) {
                        shouldEngineStopUnit = Unit
                    }
                }
                else -> {
                    println("on button: $button $isPressed")
                }
            }
        }

        override fun onJoystickButton(button: JoystickButton, isPressed: Boolean) {
            println("Joystick: $button $isPressed")
        }

        override fun onJoystick(guid: String, buttons: ByteArray, axes: FloatArray) {
//            println("Joystick: $guid\n\tbuttons: ${buttons.contentToString()}\n\taxes: ${axes.contentToString()}\n")
        }
    }

    override fun shouldEngineStop(): Boolean {
        return ::shouldEngineStopUnit.isInitialized
    }

    private fun Canvas.drawJoystickTriggers(x: Double, y: Double, joystick: Joystick) {
        val d = 25.0 * 3
        val info = getFontInfo(height = 16f)
        val lv = joystick.getValue(JoystickAxis.LEFT_TRIGGER)
        drawLineLoop(
            color = Color.GREEN,
            points = setOf(pointOf(x = x, y = y + d + 25.0), pointOf(x = x, y = y + d + 25.0 + d * lv)),
            lineWidth = 2f
        )
        drawText(
            color = Color.GREEN,
            pointTopLeft = pointOf(x = x - 16.0, y = y),
            info = info,
            text = String.format("%+.1f", lv)
        )
        val rv = joystick.getValue(JoystickAxis.RIGHT_TRIGGER)
        drawLineLoop(
            color = Color.GREEN,
            points = setOf(
                pointOf(x = x + 16.0 * 6 + 25.0 * 10, y = y + d + 25.0),
                pointOf(x = x + 16.0 * 6 + 25.0 * 10, y = y + d + 25.0 + d * rv)
            ),
            lineWidth = 2f
        )
        drawText(
            color = Color.GREEN,
            pointTopLeft = pointOf(x = x - 16.0 + 16.0 * 6 + 25.0 * 10, y = y),
            info = info,
            text = String.format("%+.1f", rv)
        )
    }

    private fun Canvas.drawJoystickBumpers(x: Double, y: Double, joystick: Joystick) {
        val height = 25.0
        val width = height * 3
        drawJoystickButtonRectangle(
            pointTopLeft = pointOf(x = x, y = y + height * 0),
            width = width,
            height = height,
            text = "LEFT_B",
            isPressed = joystick.isPressed(JoystickButton.LEFT_BUMPER)
        )
        drawJoystickButtonRectangle(
            pointTopLeft = pointOf(x = x + width + height * 4 + 16.0 * 2, y = y + height * 0),
            width = width,
            height = height,
            text = "RIGHT_B",
            isPressed = joystick.isPressed(JoystickButton.RIGHT_BUMPER)
        )
    }

    private fun Canvas.drawJoystickButtonRectangle(pointTopLeft: Point, width: Double, height: Double, text: String, isPressed: Boolean) {
        val color = if (isPressed) Color.YELLOW else Color.GREEN
        drawRectangle(
            color = color,
            pointTopLeft = pointTopLeft,
            size = sizeOf(width = width, height = height),
            lineWidth = 2f
        )
        val textHeight = 16f
        val info = getFontInfo(height = textHeight)
        drawText(
            color = color,
            pointTopLeft = pointTopLeft.plus(
                dX = width / 2 - engine.fontAgent.getTextWidth(info, text) / 2,
                dY = height / 2 - textHeight / 2.0
            ),
            info = info,
            text = text
        )
    }

    private fun Canvas.drawJoystickButtonCircle(pointCenter: Point, radius: Double, text: String, isPressed: Boolean) {
        val color = if (isPressed) Color.YELLOW else Color.GREEN
        drawCircle(
            color = color,
            pointCenter = pointCenter,
            radius = radius,
            edgeCount = 16,
            lineWidth = 2f
        )
        val textHeight = 16f
        val info = getFontInfo(height = textHeight)
        val width = engine.fontAgent.getTextWidth(info, text)
        drawText(
            color = color,
            pointTopLeft = pointCenter.plus(dX = - width / 2, dY = - textHeight / 2.0),
            info = info,
            text = text
        )
    }

    private fun Canvas.drawJoystickButtonsLeft(x: Double, y: Double, joystick: Joystick) {
        val width = 25.0
        drawJoystickButtonRectangle(
            pointTopLeft = pointOf(x = x + width * 1, y = y + width * 0),
            width = width,
            height = width,
            text = "U",
            isPressed = joystick.isPressed(JoystickButton.DPAD_UP)
        )
        drawJoystickButtonRectangle(
            pointTopLeft = pointOf(x = x + width * 0, y = y + width * 1),
            width = width,
            height = width,
            text = "L",
            isPressed = joystick.isPressed(JoystickButton.DPAD_LEFT)
        )
        drawJoystickButtonRectangle(
            pointTopLeft = pointOf(x = x + width * 2, y = y + width * 1),
            width = width,
            height = width,
            text = "R",
            isPressed = joystick.isPressed(JoystickButton.DPAD_RIGHT)
        )
        drawJoystickButtonRectangle(
            pointTopLeft = pointOf(x = x + width * 1, y = y + width * 2),
            width = width,
            height = width,
            text = "D",
            isPressed = joystick.isPressed(JoystickButton.DPAD_DOWN)
        )
    }

    private fun Canvas.drawJoystickJoy(
        pointCenter: Point,
        radius: Double,
        joystick: Joystick,
        button: JoystickButton,
        axisX: JoystickAxis,
        axisY: JoystickAxis
    ) {
        drawJoystickButtonCircle(
            pointCenter = pointCenter,
            radius = radius,
            text = button.name,
            isPressed = joystick.isPressed(button)
        )
        val valueX = joystick.getValue(axisX)
        val valueY = joystick.getValue(axisY)
        drawLineLoop(
            color = Color.GREEN,
            points = setOf(
                pointCenter,
                pointCenter.plus(dX = radius * valueX, dY = radius * valueY)
            ),
            lineWidth = 2f
        )
        val textHeight = 16f
        val info = getFontInfo(height = textHeight)
        drawText(
            color = Color.GREEN,
            pointTopLeft = pointCenter.plus(dX = - radius, dY = radius + 16.0),
            info = info,
            text = String.format("x: %+.1f", valueX)
        )
        drawText(
            color = Color.GREEN,
            pointTopLeft = pointCenter.plus(dX = - radius, dY = radius + 16.0 + textHeight),
            info = info,
            text = String.format("y: %+.1f", valueY)
        )
    }

    private fun Canvas.drawJoystickJoys(x: Double, y: Double, joystick: Joystick) {
        val radius = 25.0 * 2
        drawJoystickJoy(
            pointCenter = pointOf(x = x + 25.0 * 0 + radius, y = y + 25.0 * 0 + radius),
            radius = radius,
            joystick = joystick,
            button = JoystickButton.LEFT_THUMB,
            axisX = JoystickAxis.LEFT_X,
            axisY = JoystickAxis.LEFT_Y
        )
        drawJoystickJoy(
            pointCenter = pointOf(x = x + 25.0 * 6 + 16.0 * 2 + radius, y = y + 25.0 * 0 + radius),
            radius = radius,
            joystick = joystick,
            button = JoystickButton.RIGHT_THUMB,
            axisX = JoystickAxis.RIGHT_X,
            axisY = JoystickAxis.RIGHT_Y
        )
    }

    private fun Canvas.drawJoystickMain(x: Double, y: Double, joystick: Joystick) {
        val height = 25.0
        val width = height * 2
        drawJoystickButtonRectangle(
            pointTopLeft = pointOf(x = x + width / 2, y = y + height * 0),
            width = width,
            height = height,
            text = "GUIDE",
            isPressed = joystick.isPressed(JoystickButton.GUIDE)
        )
        drawJoystickButtonRectangle(
            pointTopLeft = pointOf(x = x, y = y + height * 1),
            width = width,
            height = height,
            text = "BACK",
            isPressed = joystick.isPressed(JoystickButton.BACK)
        )
        drawJoystickButtonRectangle(
            pointTopLeft = pointOf(x = x + width, y = y + height * 1),
            width = width,
            height = height,
            text = "START",
            isPressed = joystick.isPressed(JoystickButton.START)
        )
    }

    private fun Canvas.drawJoystickButtonsRight(x: Double, y: Double, joystick: Joystick) {
        val radius = 25.0 / 2
        val d = radius * 2
        drawJoystickButtonCircle(
            pointCenter = pointOf(x = x + radius + d * 1, y = y + radius + d * 2),
            radius = radius,
            text = "A",
            isPressed = joystick.isPressed(JoystickButton.A)
        )
        drawJoystickButtonCircle(
            pointCenter = pointOf(x = x + radius + d * 2, y = y + radius + d * 1),
            radius = radius,
            text = "B",
            isPressed = joystick.isPressed(JoystickButton.B)
        )
        drawJoystickButtonCircle(
            pointCenter = pointOf(x = x + radius + d * 0, y = y + radius + d * 1),
            radius = radius,
            text = "X",
            isPressed = joystick.isPressed(JoystickButton.X)
        )
        drawJoystickButtonCircle(
            pointCenter = pointOf(x = x + radius + d * 1, y = y + radius + d * 0),
            radius = radius,
            text = "Y",
            isPressed = joystick.isPressed(JoystickButton.Y)
        )
    }

    private fun Canvas.drawJoystick(x: Double, y: Double, joystick: Joystick) {
        drawJoystickTriggers(x = x + 16.0 * 1, y = y + 16.0 * 0, joystick = joystick)
        drawJoystickBumpers(x = x + 16.0 * 3, y = y + 16.0 * 0, joystick = joystick)
        drawJoystickButtonsLeft(x = x + 16.0 * 3, y = y + 16.0 + 25.0 * 1, joystick = joystick)
        drawJoystickButtonsRight(x = x + 16.0 * 3 + 25.0 * 7 + 16.0 * 2, y = y + 16.0 + 25.0 * 1, joystick = joystick)
        drawJoystickMain(x = x + 16.0 * 3 + 25.0 * 3 + 16.0, y = y + 16.0 * 2, joystick = joystick)
        drawJoystickJoys(x = x + 16.0 * 3, y = y + 16.0 * 2 + 25.0 * 4, joystick = joystick)
    }

    private fun Canvas.drawKeyboard(x: Double, y: Double, keyboard: Keyboard) {
        setOf(
            setOf(KeyboardButton.Q, KeyboardButton.W, KeyboardButton.E, KeyboardButton.R, KeyboardButton.T, KeyboardButton.Y, KeyboardButton.U, KeyboardButton.I, KeyboardButton.O, KeyboardButton.P),
            setOf(KeyboardButton.A, KeyboardButton.S, KeyboardButton.D, KeyboardButton.F, KeyboardButton.G, KeyboardButton.H, KeyboardButton.J, KeyboardButton.K, KeyboardButton.L),
            setOf(KeyboardButton.Z, KeyboardButton.X, KeyboardButton.C, KeyboardButton.V, KeyboardButton.B, KeyboardButton.N, KeyboardButton.M)
        ).forEachIndexed { dY, keys ->
            keys.forEachIndexed { dX, button ->
                val isPressed = keyboard.isPressed(button)
                val width = 25.0
                val pointTopLeft = pointOf(x + width * dX, y + width * dY)
                drawText(
                    info = getFontInfo(height = 16f),
                    color = if (isPressed) Color.YELLOW else Color.GREEN,
                    pointTopLeft = pointTopLeft,
                    text = button.name
                )
                if (isPressed) {
                    drawRectangle(
                        color = Color.YELLOW,
                        pointTopLeft = pointTopLeft,
                        size = sizeOf(width = width, height = width),
                        lineWidth = 2f
                    )
                }
            }
        }
    }

    override fun onRender(canvas: Canvas) {
        val fps = 1.seconds / engine.property.time.diff()
        canvas.drawText(
            info = getFontInfo(height = 16f),
            pointTopLeft = Point.Center,
            color = Color.GREEN,
            text = String.format("%.2f", fps)
        )
        canvas.drawKeyboard(x = 16.0, y = 16.0, engine.input.keyboard)
        val joystick = engine.input.joysticks[0]
        if (joystick != null) {
            canvas.drawJoystick(x = 16.0, y = 16.0 + 25.0 * 4, joystick)
            // todo
        }
    }
}
