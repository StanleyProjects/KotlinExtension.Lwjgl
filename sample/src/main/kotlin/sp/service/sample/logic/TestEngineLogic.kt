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
import sp.kx.math.Point
import sp.kx.math.Vector
import sp.kx.math.angleOf
import sp.kx.math.center
import sp.kx.math.ct
import sp.kx.math.eq
import sp.kx.math.isEmpty
import sp.kx.math.measure.Deviation
import sp.kx.math.measure.Measure
import sp.kx.math.measure.MutableDeviation
import sp.kx.math.measure.MutableSpeed
import sp.kx.math.measure.Speed
import sp.kx.math.measure.diff
import sp.kx.math.measure.measureOf
import sp.kx.math.measure.speedOf
import sp.kx.math.minus
import sp.kx.math.offsetOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.sizeOf
import sp.kx.math.toString
import sp.kx.math.toVector
import sp.kx.math.vectorOf
import sp.service.sample.util.FontInfoUtil
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds

internal class TestEngineLogic(private val engine: Engine) : EngineLogic {
    class Player(
        val point: MutablePoint = MutablePoint(x = 0.0, y = 0.0),
        val speed: MutableSpeed = MutableSpeed(5.0, TimeUnit.SECONDS),
        val direction: Direction = Direction(0.0, 0.0),
    ) {
        companion object {
            val size = sizeOf(width = 2.0, height = 2.0)
            val radius: Double = kotlin.math.sqrt(2.0) * size.width / 2
        }

        class Direction(
            override var actual: Double,
            override var expected: Double,
        ) : Deviation<Double> {
            companion object {
                val speed: Speed = speedOf(kotlin.math.PI * 2)
            }
        }
    }

    private val player = Player()
    private val measure = measureOf(16.0)

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

    override fun shouldEngineStop(): Boolean {
        return ::shouldEngineStopUnit.isInitialized
    }

    private fun debug(canvas: Canvas) {
        val padding = measure.transform(1.0)
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val x = padding
        val values = listOf(
//            "x: ${point.x.toString(5, 1)}",
//            "y: ${point.y.toString(5, 1)}",
//            String.format("x: %+05.1f", point.x),
//            String.format("y: %+05.1f", point.y),
            String.format("x: %7s", String.format("%+.1f", player.point.x)),
            String.format("y: %7s", String.format("%+.1f", player.point.y)),
            String.format("v: %s", player.speed.toString()),
            String.format("a: %03.2f - %05.1f", player.direction.actual, Math.toDegrees(player.direction.actual)),
            String.format("e: %03.2f - %05.1f", player.direction.expected, Math.toDegrees(player.direction.expected)),
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

    private fun Keyboard.getPlayerOffset(): Offset {
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

    private fun vec(
        startX: Double,
        startY: Double,
        finishX: Double,
        finishY: Double,
        offset: Offset,
    ): Vector {
        return vectorOf(
            startX = startX + offset.dX,
            startY = startY + offset.dY,
            finishX = finishX + offset.dX,
            finishY = finishY + offset.dY,
        )
    }

    private fun Offset.foo(transform: (Double) -> Double): Offset {
        return offsetOf(
            dX = transform(dX),
            dY = transform(dY),
        )
    }

    private fun Point.foo(transform: (Double) -> Double): Point {
        return pointOf(
            x = transform(x),
            y = transform(y),
        )
    }

    private fun Vector.foo(transform: (Double) -> Double): Vector {
        return vectorOf(
            startX = transform(start.x),
            startY = transform(start.y),
            finishX = transform(finish.x),
            finishY = transform(finish.y),
        )
    }

    private infix fun Vector.bar(offset: Offset): Vector {
        return vectorOf(
            startX = start.x + offset.dX,
            startY = start.y + offset.dY,
            finishX = finish.x + offset.dX,
            finishY = finish.y + offset.dY,
        )
    }

    private fun Double.baz(): Double {
        return div(absoluteValue)
//        return this / absoluteValue
    }

    private fun Double.orNull(): Double? {
        if (isNaN()) return null
        return this
    }

    override fun onRender(canvas: Canvas) {
        val padding = measure.transform(1.0)
        val diff = engine.property.time.diff()
        val fps = 1.seconds / diff
        canvas.drawText(
            info = FontInfoUtil.getFontInfo(height = 16f),
            pointTopLeft = pointOf(x = padding, y = padding),
            color = Color.GREEN,
            text = fps.toString(6, 2)
        )
        val center = Point.Center + engine.property.pictureSize.center()
//        val relative = center - pointOf(x = measure.transform(player.point.x), y = measure.transform(player.point.y))
//        val relative = center - player.point
        val relative = center - player.point.foo(measure::transform)
//        val length = measure.transform(2.0)
        measure.transform(2.0).also { length ->
            canvas.drawLine(
                color = Color.GREEN,
                vector = vectorOf(startX = 0.0, startY = length, finishX = 0.0, finishY = -length).bar(relative),
                lineWidth = 1f
            )
            canvas.drawLine(
                color = Color.GREEN,
                vector = vectorOf(startX = -length, startY = 0.0, finishX = length, finishY = 0.0).bar(relative),
                lineWidth = 1f
            )
        }
        /*
        val length = 2.0
        canvas.drawLine(
            color = Color.GREEN,
            vector = vectorOf(startX = 0.0, startY = length, finishX = 0.0, finishY = -length).foo(measure::transform).bar(relative),
//            vector = pointOf(x = 0.0, y = length).toVector(pointOf(x = 0.0, y = -length), relative),
            lineWidth = 1f
        )
        canvas.drawLine(
            color = Color.GREEN,
            vector = vectorOf(startX = -length, startY = 0.0, finishX = length, finishY = 0.0).foo(measure::transform).bar(relative),
//            vector = pointOf(x = -length, y = 0.0).toVector(pointOf(x = length, y = 0.0), relative),
            lineWidth = 1f
        )
        */
        val offset = engine.input.keyboard.getPlayerOffset()
        if (!offset.isEmpty()) {
            player.direction.expected = angleOf(offset).ct()
            if (player.direction.actual.isNaN()) error("actual before is NaN!")
            if (!player.direction.expected.eq(player.direction.actual, points = 4)) {
//                val difference = player.direction.actual - player.direction.expected
//                val d = Player.Direction.speed.length(diff)
//                if (d > difference.absoluteValue) {
//                    player.direction.actual = player.direction.expected
//                } else {
//                    val actual: Double = if (difference.absoluteValue > kotlin.math.PI) {
//                        player.direction.actual + d * difference / difference.absoluteValue
//                    } else {
//                        player.direction.actual + d * difference / difference.absoluteValue * -1
//                    }
//                    player.direction.actual = actual.ct()
//                }
                val angle = player.direction.actual - player.direction.expected
                val alpha = Player.Direction.speed.length(diff)
//                val k = angle / angle.absoluteValue
                if (alpha > angle.absoluteValue) {
                    player.direction.actual = player.direction.expected
                } else {
//                    val m = (kotlin.math.PI - angle.absoluteValue) / (kotlin.math.PI - angle.absoluteValue).absoluteValue
                    val m = (kotlin.math.PI - angle.absoluteValue).baz().orNull() ?: 1.0
//                    val actual = player.direction.actual - alpha * k * m
                    val actual = player.direction.actual - alpha * angle.baz() * m
                    player.direction.actual = actual.ct()
                }
//                val m = (kotlin.math.PI - angle.absoluteValue) / (kotlin.math.PI - angle.absoluteValue).absoluteValue
//                val actual = player.direction.actual - alpha * k * m
//                player.direction.actual = actual.ct()
//                player.direction.actual += Player.Direction.speed.length(diff)
            }
            player.point.move(
                length = player.speed.length(diff),
                angle = player.direction.expected,
            )
        }
        canvas.drawLine(
            color = Color.WHITE,
            vector = vectorOf(center, length = measure.transform(Player.radius), angle = player.direction.actual),
            lineWidth = 1f
        )
        canvas.drawCircle(
            color = Color.WHITE,
            pointCenter = center,
            radius = measure.transform(Player.radius),
            edgeCount = 16,
            lineWidth = 1f
        )
        debug(canvas)
        // todo
    }
}
