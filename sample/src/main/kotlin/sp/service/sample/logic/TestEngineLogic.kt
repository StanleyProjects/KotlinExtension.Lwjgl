package sp.service.sample.logic

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.input.JoystickMapper
import sp.kx.lwjgl.engine.input.JoystickMapping
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.JoystickButton
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.util.drawCircle
import sp.kx.math.MutableOffset
import sp.kx.math.MutablePoint
import sp.kx.math.Offset
import sp.kx.math.angleOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.toString
import sp.kx.math.vectorOf
import sp.service.sample.util.FontInfoUtil
import kotlin.time.Duration.Companion.seconds

internal class TestEngineLogic(private val engine: Engine) : EngineLogic {
    companion object {
        private const val pixelsPerUnit = 16.0 // todo
    }

    class Direction(var actual: Double, var expected: Double, val velocity: Double)

    private lateinit var shouldEngineStopUnit: Unit

    override val joystickMapper: JoystickMapper = object : JoystickMapper {
        override fun map(guid: String, buttons: ByteArray, axes: FloatArray): JoystickMapping? {
            return null
        }
    }

    override val inputCallback: EngineInputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            when (button) {
                KeyboardButton.ESCAPE -> {
                    if (isPressed) {
                        shouldEngineStopUnit = Unit
                    }
                }
                else -> {
                    // todo
                }
            }
        }

        override fun onJoystickButton(button: JoystickButton, isPressed: Boolean) {
            // todo
        }
    }

    private val point = MutablePoint(x = 0.0, y = 0.0)
    private val velocity: Double = 5.0 / 1.seconds.inWholeNanoseconds
    private val direction: Direction = Direction(
        actual = 0.0,
        expected = 0.0,
        velocity = kotlin.math.PI * 2 / 1.seconds.inWholeNanoseconds
    )
    private val width = pixelsPerUnit * 2
    private val radius = kotlin.math.sqrt(2.0) * width / 2

    override fun shouldEngineStop(): Boolean {
        return ::shouldEngineStopUnit.isInitialized
    }

    private fun debug(canvas: Canvas) {
        val padding = pixelsPerUnit * 1
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val x = padding
        val v = velocity * 1.seconds.inWholeNanoseconds
        val values = setOf(
            String.format("x: %05.1f", point.x),
            String.format("y: %05.1f", point.y),
//            String.format("v: %03.1f", v),
            String.format("a: %03.2f (%05.1f)", direction.actual, Math.toDegrees(direction.actual)),
            String.format("e: %03.2f (%05.1f)", direction.expected, Math.toDegrees(direction.expected)),
        )
        values.forEachIndexed { index, text ->
            val dY = info.height * values.size - info.height * index
            canvas.drawText(
                color = Color.GREEN,
                info = info,
                pointTopLeft = pointOf(x = x, y = engine.property.pictureSize.height - dY - padding),
                text = text
            )
        }
    }

    private fun Double.normalize(k: Double): Double {
        return ((this % k) + k) % k
    }

    private fun Offset.isEmpty(): Boolean {
        return dX == 0.0 && dY == 0.0
    }

    private fun Keyboard.getOffset(): Offset {
        val result = MutableOffset(dX = 0.0, dY = 0.0)
        if (isPressed(KeyboardButton.W)) {
            if (!isPressed(KeyboardButton.S)) {
                result.dY = -1.0
            }
        } else {
            if (isPressed(KeyboardButton.S)) {
                result.dY = 1.0
            }
        }
        if (isPressed(KeyboardButton.A)) {
            if (!isPressed(KeyboardButton.D)) {
                result.dX = -1.0
            }
        } else {
            if (isPressed(KeyboardButton.D)) {
                result.dX = 1.0
            }
        }
        return result
    }

    override fun onRender(canvas: Canvas) {
        val padding = pixelsPerUnit * 1
        val fps = 1.seconds / engine.property.time.diff()
        canvas.drawText(
            info = FontInfoUtil.getFontInfo(height = 16f),
            pointTopLeft = pointOf(x = padding, y = padding),
            color = Color.GREEN,
            text = fps.toString(2)
        )
        val center = pointOf(x = engine.property.pictureSize.width / 2, y = engine.property.pictureSize.height / 2)
        val relative = center.plus(dX = -point.x, dY = -point.y)
        val length = pixelsPerUnit * 2
        canvas.drawLine(
            color = Color.GREEN,
            vector = relative.plus(dX = 0.0, dY = length) + relative.plus(dX = 0.0, dY = -length),
            lineWidth = 1f
        )
        canvas.drawLine(
            color = Color.GREEN,
            vector = relative.plus(dX = -length, dY = 0.0) + relative.plus(dX = length, dY = 0.0),
            lineWidth = 1f
        )
        val offset = engine.input.keyboard.getOffset()
        if (!offset.isEmpty()) {
            direction.expected = angleOf(point, point + offset).normalize(kotlin.math.PI * 2)
            direction.actual = direction.expected
            val diff = engine.property.time.diff()
            point.move(
                length = velocity * diff.inWholeNanoseconds * pixelsPerUnit,
                angle = direction.expected,
            )
        }
        canvas.drawLine(
            color = Color.WHITE,
            vector = vectorOf(center, length = radius, angle = direction.actual),
            lineWidth = 1f
        )
        canvas.drawCircle(
            color = Color.WHITE,
            pointCenter = center,
            radius = radius,
            edgeCount = 16,
            lineWidth = 1f
        )
        debug(canvas)
        // todo
    }
}
