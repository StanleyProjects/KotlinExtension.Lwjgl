package sp.service.sample.logic

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.colorOf
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.util.drawCircle
import sp.kx.math.MutableOffset
import sp.kx.math.MutablePoint
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.Vector
import sp.kx.math.angleOf
import sp.kx.math.center
import sp.kx.math.dby
import sp.kx.math.distanceOf
import sp.kx.math.eq
import sp.kx.math.getIntersection
import sp.kx.math.getPerpendicular
import sp.kx.math.getShortestDistance
import sp.kx.math.getShortestPoint
import sp.kx.math.ifNaN
import sp.kx.math.isEmpty
import sp.kx.math.isParallel
import sp.kx.math.length
import sp.kx.math.lt
import sp.kx.math.measure.Measure
import sp.kx.math.measure.MutableDeviation
import sp.kx.math.measure.MutableSpeed
import sp.kx.math.measure.Speed
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.measureOf
import sp.kx.math.measure.speedOf
import sp.kx.math.minus
import sp.kx.math.moved
import sp.kx.math.offsetOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.radians
import sp.kx.math.sizeOf
import sp.kx.math.times
import sp.kx.math.toString
import sp.kx.math.toVector
import sp.kx.math.vectorOf
import sp.kx.math.whc
import sp.lwjgl.joysticks.Joystick
import sp.lwjgl.joysticks.JoystickAxis
import sp.lwjgl.joysticks.JoysticksStorage
import sp.service.sample.util.FontInfoUtil
import sp.service.sample.util.JsonJoystickMapping
import sp.service.sample.util.ResourceUtil
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

internal class TestEngineLogic(private val engine: Engine) : EngineLogic {
    class Player(
        val point: MutablePoint = MutablePoint(x = 0.0, y = 0.0),
        val speed: MutableSpeed = MutableSpeed(5.0, TimeUnit.SECONDS),
        val direction: MutableDeviation<Double> = MutableDeviation(0.0, 0.0),
        val directionSpeed: Speed = speedOf(kotlin.math.PI * 2),
    ) {
//        private val width = 2.0
        private val width = 4.0 // todo
//        private val width = 6.0 // todo
        private val size = sizeOf(width = width, height = width)
        val radius: Double = kotlin.math.sqrt(2.0) * size.width / 2

        fun copy(): Player {
            return Player(
                point = MutablePoint(x = point.x, y = point.y),
                speed = MutableSpeed(magnitude = speed.per(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS),
                direction = MutableDeviation(actual = direction.actual, expected = direction.expected),
                directionSpeed = MutableSpeed(magnitude = directionSpeed.per(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS),
            )
        }
    }

    private val player = Player()
    private val measure = measureOf(16.0)

    private fun List<Point>.toVectors(): List<Vector> {
        if (isEmpty()) return emptyList()
        if (size == 1) TODO()
        val list = mutableListOf<Vector>()
        for (index in 1 until size) {
            list += get(index - 1) + get(index)
        }
        return list
    }

    private val barriers = listOf(
        Barrier(
            color = Color.GREEN,
            vector = pointOf(-5, 7) + pointOf(5, 7),
            passable = Barrier.Passable.Not,
        ),
        Barrier(
            color = Color.BLUE,
            vector = pointOf(-5, -7) + pointOf(5, -7),
            passable = Barrier.Passable.Full,
        ),
    )

    /*
    private val barriers = listOf(
        pointOf(x = 7.0 + 0 * 2, y = 7.0 + 0 * 2) + pointOf(x = 7.0 + 3 * 2, y = 7.0 - 5 * 2),
        pointOf(x = 7.0 + 3 * 2, y = 7.0 - 5 * 2) + pointOf(x = 7.0 + 5 * 2, y = 7.0 - 5 * 2),
        pointOf(x = 7.0 + 5 * 2, y = 7.0 - 5 * 2) + pointOf(x = 7.0 + 5 * 2, y = 7.0 + 6 * 2)
    ) + (-12).let { x ->
        listOf(
            pointOf(x = x + 0, y = x + 0),
            pointOf(x = x + 4, y = x + 4),
            pointOf(x = x + 4 + 4, y = x + 4 + 2),
            pointOf(x = x + 4 + 4 + 4, y = x + 4),
            pointOf(x = x + 4 + 4 + 4 + 4, y = x + 4 - 4),
        ).toVectors()
    } + listOf(
        pointOf(x = 0 - 32, y = 0 - 8),
        pointOf(x = 0 - 32, y = 0 - 8 + 16),
    ).toVectors() + listOf(
        pointOf(x = 0 - 32 - 8, y = 0),
        pointOf(x = 0 - 32 + 8, y = 0),
    ).toVectors() + (-8 to 8).let { (dX, dY) ->
        listOf(
            pointOf(x = dX, y = dY),
            pointOf(x = dX, y = dY + 16),
        ).toVectors() + listOf(
            pointOf(x = dX + 4, y = dY),
            pointOf(x = dX + 4, y = dY + 4),
        ).toVectors() + listOf(
            pointOf(x = dX - 2, y = dY),
            pointOf(x = dX - 2, y = dY + 4),
        ).toVectors()
    } + (0 to 0).let { (dX, dY) ->
        listOf(
            pointOf(x = dX - 16 + 0, y = dY + 2),
            pointOf(x = dX - 16 - 4, y = dY + 2),
        ).toVectors() + listOf(
            pointOf(x = dX - 16 + 0, y = dY - 2),
            pointOf(x = dX - 16 - 4, y = dY - 2),
        ).toVectors() + listOf(
            pointOf(x = dX - 16 + 0, y = dY + 4),
            pointOf(x = dX - 16 - 4, y = dY + 4),
        ).toVectors()
    }
    */

    private lateinit var shouldEngineStopUnit: Unit
    private val ds4Mapping = JsonJoystickMapping(
        ResourceUtil.requireResourceAsStream("dualshock4.json").reader().readText(),
    )
    private val joystickStorage = JoysticksStorage(
        mappings = mapOf(
            "030000004c050000cc09000000010000" to ds4Mapping,
        ),
        onPressButton = { metaData, button, isPressed ->
            println("Joystick #${metaData.number} $button pressed: $isPressed")
        },
    )

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
    }

    override fun shouldEngineStop(): Boolean {
        return ::shouldEngineStopUnit.isInitialized
    }

    private fun debug(previous: Player, canvas: Canvas) {
        val padding = measure.transform(1.0)
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val x = padding
//        val (bi, barrier, shortest) = barriers
//            .mapIndexed { index, it -> Triple(index, it, it.getShortestDistance(player.point)) }
//            .minBy { (_, _, shortest) ->
//                shortest
//            } // todo
        val currentSpeed = speedOf(magnitude = distanceOf(previous.point, player.point), engine.property.time.diff())
        val values = listOf(
//            "x: ${point.x.toString(5, 1)}",
//            "y: ${point.y.toString(5, 1)}",
//            String.format("x: %+05.1f", point.x),
//            String.format("y: %+05.1f", point.y),
            String.format("x: %8s", String.format("%+.4f", player.point.x)),
            String.format("y: %8s", String.format("%+.4f", player.point.y)),
            String.format("max speed: %s/s", player.speed.per(TimeUnit.SECONDS).toString(points = 2)),
            String.format("cur speed: %s/s", currentSpeed.per(TimeUnit.SECONDS).toString(points = 2)),
            String.format("a: %03.2f - %05.1f", player.direction.actual, Math.toDegrees(player.direction.actual)),
            String.format("e: %03.2f - %05.1f", player.direction.expected, Math.toDegrees(player.direction.expected)),
            String.format("time: %sms", engine.property.time.diff().inWholeNanoseconds.toDouble().div(1_000_000).toString(total = 6, points = 3)),
//            String.format("direction diff: %05.1f", Math.toDegrees(player.direction.diff())),
//            String.format("whc: %02.1f", player.direction.diff().absoluteValue.whc().ifNaN(1.0)),
//            String.format("barrier: %s", barrier.toString()),
//            String.format("barrier: $bi] ${shortest.toString(points = 4)}"),
//            String.format("player:radius: ${player.radius.toString(points = 4)}"),
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

    private fun Joystick.getPlayerOffset(): Offset {
        val min = 0.1
        val dX = getValue(JoystickAxis.LEFT_X).toDouble()
        val dY = getValue(JoystickAxis.LEFT_Y).toDouble()
        if (dX.absoluteValue < min && dY.absoluteValue < min) return Offset.Empty
        return offsetOf(
            dX = dX,
            dY = dY,
        )
    }

    private fun onRenderIntersections(
        canvas: Canvas,
        actual: Point,
        target: Point,
        offset: Offset,
        barriers: List<Vector>,
        measure: Measure<Double, Double>,
    ) {
        val info = FontInfoUtil.getFontInfo(height = 16f)
        barriers.forEachIndexed { index, barrier ->
//            val isCollinear = isCollinear(v1 = barrier, v2 = actual + target)
//            if (isCollinear) {
//                println("$index] collinear")
//            }
            val intersection = barrier.getIntersection(
                c = actual,
                d = target,
            )
            if (intersection != null) {
                canvas.vectors.draw(
                    color = Color.YELLOW,
                    vector = intersection + intersection.moved(length = 0.1),
                    offset = offset,
                    measure = measure,
                    lineWidth = 3f,
                )
                canvas.texts.draw(
                    color = Color.YELLOW,
                    info = info,
                    pointTopLeft = intersection.moved(length = 0.2),
                    offset = offset,
                    measure = measure,
                    text = "$index]",
                )
            }
        }
    }

    @Deprecated(message = "sp.kx.math.plus")
    private operator fun Size.plus(
        measure: Measure<Double, Double>,
    ): Size {
        return sizeOf(
            width = measure.transform(width),
            height = measure.transform(height),
        )
    }

    private fun onRenderBarriers(
        canvas: Canvas,
        offset: Offset,
        barriers: List<Barrier>,
        measure: Measure<Double, Double>,
    ) {
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val dotSize = sizeOf(width = 0.25, height = 0.25)
        val dotOffset = offsetOf(dX = dotSize.width / 2, dY = dotSize.height / 2) * -1.0
        barriers.forEach { barrier ->
            val vector = barrier.vector
            canvas.vectors.draw(
                color = barrier.color,
                vector = vector,
                offset = offset,
                measure = measure,
                lineWidth = 4f,
            )
            canvas.drawRectangle(
                color = Color.YELLOW,
                pointTopLeft = vector.start + offset + dotOffset + measure,
                size = dotSize + measure,
                lineWidth = 2f,
            )
            canvas.drawRectangle(
                color = Color.YELLOW,
                pointTopLeft = vector.finish + offset + dotOffset + measure,
                size = dotSize + measure,
                lineWidth = 2f,
            )
            when (barrier.passable) {
                Barrier.Passable.Full -> {
                    canvas.texts.draw(
                        color = Color.GREEN,
                        info = info,
                        pointTopLeft = vector.start,
                        offset = offset,
                        measure = measure,
                        text = "f",
                    )
                }
                Barrier.Passable.Not -> {
                    canvas.texts.draw(
                        color = Color.RED,
                        info = info,
                        pointTopLeft = vector.start,
                        offset = offset,
                        measure = measure,
                        text = "n",
                    )
                }
            }
        }
    }

    private fun onRenderTriangles(
        canvas: Canvas,
        offset: Offset,
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
        barriers.forEachIndexed { index, barrier ->
            val color = colors[index % colors.size]
//            val ab = vectorOf(
//                startX = player.point.x + offset.dX,
//                startY = player.point.y + offset.dY,
//                finishX = barrier.start.x + offset.dX,
//                finishY = barrier.start.y + offset.dY,
//            )
            val ab = player.point + barrier.start
            canvas.vectors.draw(
                color = color,
                vector = ab,
                offset = offset,
                measure = measure,
                lineWidth = 1f,
            )
            canvas.texts.draw(
                color = color,
                info = info,
                pointTopLeft = ab.center(),
                offset = offset,
                measure = measure,
                text = distanceOf(a = player.point, b = barrier.start).toString(total = 4, points = 2),
            )
            val ac = player.point + barrier.finish
            canvas.vectors.draw(
                color = color,
                vector = ac,
                offset = offset,
                measure = measure,
                lineWidth = 1f,
            )
            canvas.texts.draw(
                color = color,
                info = info,
                pointTopLeft = ac.center(),
                offset = offset,
                measure = measure,
                text = distanceOf(a = player.point, b = barrier.finish).toString(total = 4, points = 2),
            )
            val bc = ab.finish + ac.finish
            val perpendicular = barrier.getPerpendicular(target = player.point)
            val aH = player.point + perpendicular
            canvas.vectors.draw(
                color = color,
                vector = aH,
                offset = offset,
                measure = measure,
                lineWidth = 1f,
            )
            val tPoint = bc.center()
            canvas.texts.draw(
                color = color,
                info = info,
                pointTopLeft = tPoint,
                offset = offset,
                measure = measure,
                text = aH.length().toString(total = 4, points = 2),
            )
            val shortest = barrier.getShortestDistance(target = player.point)
            canvas.texts.draw(
                color = color,
                info = info,
                pointTopLeft = tPoint.plus(dX = 0.0, dY = 1.0),
                offset = offset,
                measure = measure,
                text = shortest.toString(total = 4, points = 2),
            )
        }
    }

    private fun <K : Any, V : Any> Iterable<K>.associateWithNotNull(valueSelector: (K) -> V?): Map<K, V> {
        val result = mutableMapOf<K, V>()
        for (key in this) {
            val value = valueSelector(key)
            if (value != null) result[key] = value
        }
        return result
    }

    private fun getCorrectedPoint(
        minLength: Double,
        target: Point,
        vector: Vector,
    ): Point {
        val shortestPoint = vector.getShortestPoint(target = target)
        val angle = angleOf(a = shortestPoint, b = target)
        return shortestPoint.moved(length = minLength, angle = angle)
    }

    private fun angleOf(p1: Point, p2: Point, p3: Point): Double {
        val a = distanceOf(p1, p3)
        val b = distanceOf(p2, p3)
        val c = distanceOf(p1, p2)
        val cosA = (a * a + c * c - b * b) / 2 * a * c
        return kotlin.math.acos(cosA)
    }

    private fun getCorrectedPoint(
        minLength: Double,
        target: Point,
        barrier: Barrier,
    ): Point {
        return when (barrier.passable) {
            Barrier.Passable.Full -> target
            Barrier.Passable.Not -> getCorrectedPoint(minLength = minLength, target = target, vector = barrier.vector)
        }
    }

    private fun <T : Any> Iterable<T>.print(
        title: String,
        transform: (T) -> String = { it.toString() },
    ) {
        val message = """
            |
            |$title:
            ${mapIndexed { index, it -> index to it }.joinToString(separator = "\n") { (index, it) -> "| $index] " + transform(it) }}
        """.trimMargin()
        println(message)
    }

    private fun Vector.closerThan(point: Point, minDistance: Double): Boolean {
        return getShortestDistance(point).lt(other = minDistance, points = 12)
    }

    private fun Barrier.isAllowed(point: Point, minDistance: Double): Boolean {
        return when (passable) {
            Barrier.Passable.Full -> true
            Barrier.Passable.Not -> !vector.closerThan(point = point, minDistance = minDistance)
        }
    }

    private fun getFinalPoint(
        player: Player,
        minLength: Double,
        target: Point,
        barriers: List<Barrier>,
    ): Point? {
        val targetDistance = distanceOf(player.point, target)
        val nearest = barriers.filter { barrier ->
            barrier.vector.closerThan(point = player.point, minDistance = targetDistance + minLength)
        }
        val filtered = nearest.filter { barrier ->
            barrier.vector.closerThan(point = target, minDistance = minLength)
        }
        if (filtered.isEmpty()) return target
        val correctedPoints = nearest.map { barrier ->
            getCorrectedPoint(
                minLength = minLength,
                target = target,
                barrier = barrier,
            )
        }
        val allowedPoints = correctedPoints.filter { point ->
            nearest.all { barrier ->
                barrier.isAllowed(point = point, minDistance = minLength)
            }
        }
        if (allowedPoints.isEmpty()) {
            println("No allowed point!")
            return null // todo
        }
        val finalPoint = allowedPoints.maxBy {
            distanceOf(player.point, it)
        }
        return finalPoint
    }

    override fun onRender(canvas: Canvas) {
        joystickStorage.update()
        val previous = player.copy()
        val padding = measure.transform(1.0)
        val timeDiff = engine.property.time.diff()
        val fps = engine.property.time.frequency()
        canvas.drawText(
            info = FontInfoUtil.getFontInfo(height = 16f),
            pointTopLeft = pointOf(x = padding, y = padding),
            color = Color.GREEN,
            text = fps.toString(6, 2)
        )
//        val center = engine.property.pictureSize.centerPoint()
        val center = pointOf(
            x = measure.units(engine.property.pictureSize.width / 2),
            y = measure.units(engine.property.pictureSize.height / 2),
        )
//        val relative = center - (player.point + measure)
        val offset = center - player.point
        2.0.also { length ->
            canvas.vectors.draw(
                color = Color.GREEN,
                vector = vectorOf(startX = 0.0, startY = length, finishX = 0.0, finishY = -length),
                offset = offset,
                measure = measure,
                lineWidth = 1f,
            )
            canvas.vectors.draw(
                color = Color.GREEN,
                vector = vectorOf(startX = -length, startY = 0.0, finishX = length, finishY = 0.0),
                offset = offset,
                measure = measure,
                lineWidth = 1f,
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
//        val playerOffset = engine.input.keyboard.getPlayerOffset()
        val joystick = joystickStorage.getJoysticks()[0]
        val playerOffset = if (joystick == null) {
            engine.input.keyboard.getPlayerOffset()
        } else {
            joystick.getPlayerOffset()
        }
//        onRenderIntersections(
//            canvas = canvas,
//            actual = player.point,
//            target = player.point.moved(
//                length = player.speed.length(timeDiff),
//                angle = player.direction.expected,
//            ),
//            offset = offset,
//            barriers = barriers,
//            measure = measure,
//        ) // todo
        if (!playerOffset.isEmpty()) {
            player.direction.expected = angleOf(playerOffset).radians()
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
            val length = player.speed.length(timeDiff)
//            val multiplier = distanceOf(playerOffset) / distanceOf(offsetOf(1, 1))
            val multiplier = kotlin.math.min(1.0, distanceOf(playerOffset))
//            val multiplier = kotlin.math.sqrt(playerOffset.dX * playerOffset.dX + playerOffset.dY * playerOffset.dY)
            val target = player.point.moved(
                length = length * multiplier,
                angle = player.direction.expected,
            )
//            onRenderIntersections(
//                canvas = canvas,
//                actual = player.point,
//                target = target,
//                offset = offset,
//                barriers = barriers,
//                measure = measure,
//            ) // todo
            val finalPoint = getFinalPoint(
                player = player,
                minLength = player.radius,
                target = target,
                barriers = barriers.filter { it.passable != Barrier.Passable.Full },
            )
            if (finalPoint != null) {
                player.point.set(finalPoint)
            }
//            val allowed = allowed(
//                player = player,
//                target = target,
//                barriers = barriers,
//            )
//            if (allowed) {
//                player.point.set(target)
//            } // todo
//            player.point.move(
//                length = player.speed.length(timeDiff),
//                angle = player.direction.expected,
//            ) // todo
        }
        canvas.vectors.draw(
            color = Color.YELLOW,
            vector = vectorOf(center, length = player.radius, angle = player.direction.expected),
            measure = measure,
            lineWidth = 1f,
        )
        canvas.drawLine(
            color = Color.WHITE,
            vector = vectorOf(center, length = player.radius, angle = player.direction.actual) + measure,
            lineWidth = 1f
        )
        val currentSpeed = speedOf(magnitude = distanceOf(previous.point, player.point), engine.property.time.diff())
        canvas.vectors.draw(
            color = Color.GREEN,
            vector = vectorOf(center, length = player.radius * currentSpeed.per(TimeUnit.SECONDS) / player.speed.per(TimeUnit.SECONDS), angle = player.direction.expected),
            measure = measure,
            lineWidth = 4f,
        )
        // todo
        canvas.drawCircle(
            color = Color.WHITE,
            pointCenter = center + measure,
            radius = measure.transform(player.radius),
            edgeCount = 16,
            lineWidth = 1f
        )
        onRenderBarriers(
            canvas = canvas,
            offset = offset,
            barriers = barriers,
            measure = measure,
        ) // todo
//        onRenderTriangles(
//            canvas = canvas,
//            player = player,
//            offset = offset,
//            barriers = barriers,
//            measure = measure,
//        ) // todo
        debug(
            previous = previous,
            canvas = canvas,
        )
        // todo
    }
}
