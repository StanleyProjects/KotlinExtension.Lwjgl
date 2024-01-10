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
import sp.kx.math.centerPoint
import sp.kx.math.dby
import sp.kx.math.distanceOf
import sp.kx.math.eq
import sp.kx.math.ifNaN
import sp.kx.math.isEmpty
import sp.kx.math.length
import sp.kx.math.map
import sp.kx.math.measure.Measure
import sp.kx.math.measure.MutableDeviation
import sp.kx.math.measure.MutableSpeed
import sp.kx.math.measure.Speed
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.measureOf
import sp.kx.math.measure.speedOf
import sp.kx.math.minus
import sp.kx.math.offsetOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.radians
import sp.kx.math.sizeOf
import sp.kx.math.toString
import sp.kx.math.toVector
import sp.kx.math.vectorOf
import sp.kx.math.whc
import sp.service.sample.util.FontInfoUtil
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

internal class TestEngineLogic(private val engine: Engine) : EngineLogic {
    class Player(
        val point: MutablePoint = MutablePoint(x = 0.0, y = 0.0),
        val speed: MutableSpeed = MutableSpeed(5.0, TimeUnit.SECONDS),
        val direction: MutableDeviation<Double> = MutableDeviation(0.0, 0.0),
        val directionSpeed: Speed = speedOf(kotlin.math.PI * 2),
    ) {
        val size = sizeOf(width = 2.0, height = 2.0)
        val radius: Double = kotlin.math.sqrt(2.0) * size.width / 2
    }

    private val player = Player()
    private val measure = measureOf(16.0)
    private val barriers = listOf(
        pointOf(x = 7.0 + 0 * 2, y = 7.0 + 0 * 2) + pointOf(x = 7.0 + 3 * 2, y = 7.0 - 5 * 2),
        pointOf(x = 7.0 + 3 * 2, y = 7.0 - 5 * 2) + pointOf(x = 7.0 + 5 * 2, y = 7.0 - 5 * 2),
        pointOf(x = 7.0 + 5 * 2, y = 7.0 - 5 * 2) + pointOf(x = 7.0 + 5 * 2, y = 7.0 + 6 * 2)
    )

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
            String.format("direction diff: %05.1f", Math.toDegrees(player.direction.diff())),
            String.format("whc: %02.1f", player.direction.diff().absoluteValue.whc().ifNaN(1.0)),
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

    private fun Point.toOffset(other: Point, measure: Measure<Double, Double>): Offset {
        return offsetOf(
            dX = x - other.x,
            dY = y - other.y,
        )
    }

    private fun Point.toOffsetMeasured(other: Point, measure: Measure<Double, Double>): Offset {
        return offsetOf(
            dX = x - measure.transform(other.x),
            dY = y - measure.transform(other.y),
        )
    }

    private fun Vector.map(measure: Measure<Double, Double>, offset: Offset): Vector {
        return vectorOf(
            startX = measure.transform(start.x) + offset.dX,
            startY = measure.transform(start.y) + offset.dY,
            finishX = measure.transform(finish.x) + offset.dX,
            finishY = measure.transform(finish.y) + offset.dY,
        )
    }

    private fun onRenderBarriers(
        canvas: Canvas,
        center: Point,
        barriers: List<Vector>,
        measure: Measure<Double, Double>,
    ) {
//        val offset = center - (player.point + measure)
//        val offset = offsetOf(
//            dX = center.x - measure.transform(player.point.x),
//            dY = center.y - measure.transform(player.point.y),
//        )
        val offset = center.toOffsetMeasured(player.point, measure)
        barriers.forEach { barrier ->
            barrier + measure
            canvas.drawLine(
                color = Color.GREEN,
                vector = barrier.map(measure, offset),
                lineWidth = 1f,
            )
        }
    }

    private fun getPerpendicular(
        aX: Double,
        aY: Double,
        bX: Double,
        bY: Double,
        cX: Double,
        cY: Double
    ): Point {
        if (bX == cX) return pointOf(x = bX, y = aY)
        if (bY == cY) return pointOf(x = aX, y = bY)
        val b = (bY * cX - cY * bX) / (cX - bX)
        val k = (bY - b) / bX
        val kH = -1 / k
        val bH = aY - kH * aX
        val hX = (b - bH) / (kH - k)
        return pointOf(
            x = hX,
            y = k * hX + b
        )
    }

    private fun getShortest(
        xStart: Double,
        yStart: Double,
        xFinish: Double,
        yFinish: Double,
        xTarget: Double,
        yTarget: Double
    ): Double {
        val dX = xFinish - xStart
        val dY = yFinish - yStart
        val d = kotlin.math.sqrt(dY * dY + dX * dX)
        val dS = kotlin.math.sqrt((yStart - yTarget) * (yStart - yTarget) + (xStart - xTarget) * (xStart - xTarget))
        val dF = kotlin.math.sqrt((yFinish - yTarget) * (yFinish - yTarget) + (xFinish - xTarget) * (xFinish - xTarget))
        val shortest = (dY * xTarget - dX * yTarget + xFinish * yStart - yFinish * xStart).absoluteValue / d
        if (kotlin.math.sqrt(dS * dS - shortest * shortest) > d) return dF
        if (kotlin.math.sqrt(dF * dF - shortest * shortest) > d) return dS
        return shortest
    }

    private fun onRenderTriangles(
        canvas: Canvas,
        center: Point,
        player: Player,
        barriers: List<Vector>,
        measure: Measure<Double, Double>,
    ) {
        val colors = listOf(
            Color.YELLOW,
            Color.RED,
            Color.BLUE,
            Color.GREEN,
        )
        val info = FontInfoUtil.getFontInfo(height = 16f)
//        val offset = center - (player.point + measure)
//        val offset = offsetOf(
//            dX = center.x - measure.transform(player.point.x),
//            dY = center.y - measure.transform(player.point.y),
//        )
        val offset = center.toOffsetMeasured(player.point, measure)
        barriers.forEachIndexed { index, barrier ->
            val color = colors[index % colors.size]
//            val ab = vectorOf(
//                startX = player.point.x + offset.dX,
//                startY = player.point.y + offset.dY,
//                finishX = barrier.start.x + offset.dX,
//                finishY = barrier.start.y + offset.dY,
//            )
            val ab = (player.point + barrier.start).map(measure, offset)
            canvas.drawLine(
                color = color,
                vector = ab,
                lineWidth = 1f,
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = ab.center(),
                text = distanceOf(a = player.point, b = barrier.start).toString(total = 4, points = 2),
            )
            val ac = (player.point + barrier.finish).map(measure, offset)
            canvas.drawLine(
                color = color,
                vector = ac,
                lineWidth = 1f
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = ac.center(),
                text = distanceOf(a = player.point, b = barrier.finish).toString(total = 4, points = 2),
            )
            val bc = ab.finish + ac.finish
            val perpendicular = getPerpendicular(
                aX = player.point.x,
                aY = player.point.y,
                bX = barrier.start.x,
                bY = barrier.start.y,
                cX = barrier.finish.x,
                cY = barrier.finish.y,
            )
            val aH = (player.point + perpendicular).map(measure, offset)
//            val aH = player.point.toVector(
//                finish = barrier.getPerpendicular(target = point),
//                offset = offset
//            )
            canvas.drawLine(
                color = color,
                vector = aH,
                lineWidth = 1f,
            )
            val tPoint = bc.center()
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = tPoint,
                text = (player.point + perpendicular).length().toString(total = 4, points = 2),
            )
            val shortest = getShortest(
                xStart = barrier.start.x,
                yStart = barrier.start.y,
                xFinish = barrier.finish.x,
                yFinish = barrier.finish.y,
                xTarget = player.point.x,
                yTarget = player.point.y,
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = tPoint.plus(dX = 0.0, dY = info.height.toDouble()),
                text = shortest.toString(total = 4, points = 2),
            )
        }
    }

    override fun onRender(canvas: Canvas) {
        val padding = measure.transform(1.0)
        val timeDiff = engine.property.time.diff()
        val fps = engine.property.time.frequency()
        canvas.drawText(
            info = FontInfoUtil.getFontInfo(height = 16f),
            pointTopLeft = pointOf(x = padding, y = padding),
            color = Color.GREEN,
            text = fps.toString(6, 2)
        )
        val center = engine.property.pictureSize.centerPoint()
        val relative = center - (player.point + measure)
        measure.transform(2.0).also { length ->
            canvas.drawLine(
                color = Color.GREEN,
                vector = vectorOf(startX = 0.0, startY = length, finishX = 0.0, finishY = -length, relative),
                lineWidth = 1f
            )
            canvas.drawLine(
                color = Color.GREEN,
                vector = vectorOf(startX = -length, startY = 0.0, finishX = length, finishY = 0.0, relative),
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
            player.direction.expected = angleOf(offset).radians()
            val dirDiff = player.direction.diff()
            if (!dirDiff.absoluteValue.eq(0.0, points = 4)) {
                val alpha = player.directionSpeed.length(timeDiff)
                if (alpha > dirDiff.absoluteValue) {
                    player.direction.commit()
                } else {
                    val k = dirDiff.absoluteValue.whc().ifNaN(1.0)
                    val m = dirDiff.dby()
                    val actual = player.direction.actual + alpha * m * k
                    player.direction.actual = actual.radians()
                }
            }
            player.point.move(
                length = player.speed.length(timeDiff),
                angle = player.direction.expected,
            )
        }
        canvas.drawLine(
            color = Color.WHITE,
            vector = vectorOf(center, length = measure.transform(player.radius), angle = player.direction.actual),
            lineWidth = 1f
        )
        canvas.drawLine(
            color = Color.YELLOW,
            vector = vectorOf(center, length = measure.transform(player.radius), angle = player.direction.expected),
            lineWidth = 1f,
        )
        canvas.drawCircle(
            color = Color.WHITE,
            pointCenter = center,
            radius = measure.transform(player.radius),
            edgeCount = 16,
            lineWidth = 1f
        )
        onRenderBarriers(
            canvas = canvas,
            center = center,
            barriers = barriers,
            measure = measure,
        ) // todo
        onRenderTriangles(
            canvas = canvas,
            player = player,
            center = center,
            barriers = barriers,
            measure = measure,
        ) // todo
        debug(canvas)
        // todo
    }
}
