package sp.service.sample.game.module.journey

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.util.drawCircle
import sp.kx.math.implementation.entity.geometry.getAngle
import sp.kx.math.implementation.entity.geometry.isEmpty
import sp.kx.math.implementation.entity.geometry.pointOf
import sp.kx.math.implementation.entity.geometry.updated
import sp.kx.math.implementation.entity.geometry.vectorOf
import sp.service.sample.game.entity.Direction
import sp.service.sample.game.entity.MutablePoint
import sp.service.sample.util.FontInfoUtil
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class JourneyModule(private val engine: Engine, private val broadcast: (Broadcast) -> Unit) {
    sealed interface Broadcast {
        object Exit : Broadcast
    }

    companion object {
        private const val pixelsPerUnit = 16.0 // todo
    }

    class Direction(var actual: Double, var expected: Double, val velocity: Double)
    private val velocity: Double = 5 / TimeUnit.SECONDS.toNanos(1).toDouble()
    private val direction: Direction = Direction(
        actual = 0.0,
        expected = 0.0,
        velocity = kotlin.math.PI * 2 / TimeUnit.SECONDS.toNanos(1).toDouble()
    )
    private val point = MutablePoint(x = 0.0, y = 0.0)
    private val width = pixelsPerUnit * 2
    private val radius = kotlin.math.sqrt(2.0) * width / 2

    fun init() {
        point.x = engine.property.pictureSize.width / 2
        point.y = engine.property.pictureSize.height / 2
    }

    private fun debug(canvas: Canvas) {
        val padding = pixelsPerUnit * 1
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val x = padding
        val v = velocity * TimeUnit.SECONDS.toNanos(1).toDouble()
        val values = setOf(
            String.format("x: %05.1f", point.x),
            String.format("y: %05.1f", point.y),
            String.format("v: %03.1f", v),
            String.format("a: %05.1f (%05.1f)", direction.actual, Math.toDegrees(direction.actual)),
            String.format("e: %05.1f (%05.1f)", direction.expected, Math.toDegrees(direction.expected))
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

    private fun Double.isSame(that: Double, epsilon: Double): Boolean {
        check(epsilon < 1.0)
        return (this - that).absoluteValue < epsilon
    }

    private fun Double.normalize(k: Double): Double {
        val result = this % k
        if (result < 0) return k - result.absoluteValue
        return result
    }

    private fun move(angle: Double) {
        val dTime = engine.property.timeNow - engine.property.timeLast
        direction.expected = angle.normalize(kotlin.math.PI * 2)
        if (!direction.expected.isSame(direction.actual, epsilon = 0.0001)) {
            val difference = direction.actual - direction.expected
            val d = direction.velocity * dTime
            if (d > difference.absoluteValue) {
                direction.actual = direction.expected
            } else {
                // todo
                val actual: Double = if (difference.absoluteValue > kotlin.math.PI) {
                    direction.actual + d * difference / difference.absoluteValue
                } else {
                    direction.actual + d * difference / difference.absoluteValue * -1
                }
                direction.actual = actual.normalize(kotlin.math.PI * 2)
            }
        }
        val units = velocity * dTime * pixelsPerUnit
        point.move(length = units, direction = direction.expected)
    }

    fun onRender(canvas: Canvas) {
        val keyboard = engine.input.keyboard
        var dX = 0.0
        var dY = 0.0
        if (keyboard.isPressed(KeyboardButton.W)) {
            if (!keyboard.isPressed(KeyboardButton.S)) {
                dY = -1.0
            }
        } else {
            if (keyboard.isPressed(KeyboardButton.S)) {
                dY = 1.0
            }
        }
        if (keyboard.isPressed(KeyboardButton.A)) {
            if (!keyboard.isPressed(KeyboardButton.D)) {
                dX = -1.0
            }
        } else {
            if (keyboard.isPressed(KeyboardButton.D)) {
                dX = 1.0
            }
        }
        val vector = vectorOf(start = point, finish = point.updated(dX = dX, dY = dY))
        if (!vector.isEmpty(epsilon = 0.0001)) {
            move(angle = vector.getAngle())
        }
        canvas.drawPoint(
            color = Color.YELLOW,
            point = point
        )
        canvas.drawLine(
            color = Color.WHITE,
            vector = vectorOf(start = point, length = radius, direction = direction.actual),
            lineWidth = 1f
        )
        val size = size(width = width, height = width)
//        canvas.drawRectangle(
//            color = Color.YELLOW,
//            pointTopLeft = point.updated(dX = - size.width / 2, dY = - size.height / 2),
//            size = size,
//            lineWidth = 1f
//        )
        canvas.drawRectangle(
            color = Color.YELLOW,
            pointTopLeft = point.updated(dX = - size.width / 2, dY = - size.height / 2),
            size = size,
            direction = direction.actual,
            pointOfRotation = point,
            lineWidth = 1f
        )
        canvas.drawCircle(
            color = Color.WHITE,
            pointCenter = point,
            radius = radius,
            edgeCount = 16,
            lineWidth = 1f
        )
        debug(canvas)
    }

    fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
        when (button) {
            KeyboardButton.ESCAPE -> {
                if (isPressed) {
                    broadcast(Broadcast.Exit)
                }
            }
        }
    }
}
