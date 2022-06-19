package sp.service.sample.game.module.journey

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.util.drawRectangle
import sp.kx.math.foundation.entity.geometry.Point
import sp.kx.math.implementation.entity.geometry.pointOf
import sp.kx.math.implementation.entity.geometry.updated
import sp.kx.math.implementation.entity.geometry.getAngle
import sp.kx.math.implementation.entity.geometry.isEmpty
import sp.kx.math.implementation.entity.geometry.vectorOf
import sp.service.sample.game.entity.Direction
import sp.service.sample.game.entity.MutablePoint
import sp.service.sample.util.FontInfoUtil
import java.util.concurrent.TimeUnit

class JourneyModule(private val engine: Engine, private val broadcast: (Broadcast) -> Unit) {
    sealed interface Broadcast {
        object Exit : Broadcast
    }

    companion object {
        private const val pixelsPerUnit = 16.0 // todo
    }

    private val velocity: Double = 5 / TimeUnit.SECONDS.toNanos(1).toDouble()
    private var direction: Double = 0.0 // radians
    private val point = MutablePoint(x = 0.0, y = 0.0)

    fun init() {
        point.x = engine.property.pictureSize.width / 2
        point.y = engine.property.pictureSize.height / 2
    }

    private fun debug(canvas: Canvas) {
        val padding = pixelsPerUnit * 1
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val x = padding
        val v = velocity * TimeUnit.SECONDS.toNanos(1).toDouble()
//        val degrees = (direction * -180.0 / kotlin.math.PI).let {
//            it + kotlin.math.ceil(-it / 360.0) * 360.0
//        }
        val degrees = Math.toDegrees(direction)
        val values = setOf(
            String.format("x: %05.1f", point.x),
            String.format("y: %05.1f", point.y),
            String.format("v: %03.1f", v),
            String.format("r: %05.1f (%05.1f)", direction, degrees)
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

    fun onRender(canvas: Canvas) {
        val dTime = engine.property.timeNow - engine.property.timeLast
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
            direction = vector.getAngle()
            val units = velocity * dTime * pixelsPerUnit
            point.move(length = units, direction = direction)
        }
        canvas.drawPoint(
            color = Color.WHITE,
            point = point
        )
        val size = size(width = pixelsPerUnit * 2, height = pixelsPerUnit * 2)
        canvas.drawRectangle(
            color = Color.YELLOW,
            pointTopLeft = point.updated(dX = - size.width / 2, dY = - size.height / 2),
            size = size,
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
