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
import sp.kx.math.dby
import sp.kx.math.distanceOf
import sp.kx.math.eq
import sp.kx.math.getIntersection
import sp.kx.math.getPerpendicular
import sp.kx.math.getShortestDistance
import sp.kx.math.getShortestPoint
import sp.kx.math.ifNaN
import sp.kx.math.isCollinear
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
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.radians
import sp.kx.math.sizeOf
import sp.kx.math.toString
import sp.kx.math.vectorOf
import sp.kx.math.whc
import sp.service.sample.util.FontInfoUtil
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
        private val width = 6.0 // todo
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

    private fun debug(previous: Player, canvas: Canvas) {
        val padding = measure.transform(1.0)
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val x = padding
        val (bi, barrier, shortest) = barriers
            .mapIndexed { index, it -> Triple(index, it, it.getShortestDistance(player.point)) }
            .minBy { (_, _, shortest) ->
                shortest
            }
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
//            String.format("direction diff: %05.1f", Math.toDegrees(player.direction.diff())),
//            String.format("whc: %02.1f", player.direction.diff().absoluteValue.whc().ifNaN(1.0)),
            String.format("barrier: %s", barrier.toString()),
            String.format("barrier: $bi] ${shortest.toString(points = 4)}"),
            String.format("player:radius: ${player.radius.toString(points = 4)}"),
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

    private fun onRenderBarriers(
        canvas: Canvas,
        offset: Offset,
        barriers: List<Vector>,
        measure: Measure<Double, Double>,
    ) {
        barriers.forEach { barrier ->
            canvas.vectors.draw(
                color = Color.GREEN,
                vector = barrier,
                offset = offset,
                measure = measure,
                lineWidth = 1f,
            )
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
        barrier: Vector,
    ): Point {
        val shortestPoint = barrier.getShortestPoint(target = target)
        val angle = angleOf(a = shortestPoint, b = target)
        return shortestPoint.moved(length = minLength, angle = angle)
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

    private fun getFinalPoint(
        player: Player,
        minLength: Double,
        target: Point,
        barriers: List<Vector>,
    ): Point? {
        val targetDistance = distanceOf(player.point, target)
        val nearest = barriers.filter {
            it.getShortestDistance(player.point).lt(other = targetDistance + minLength, points = 12)
        }
        val filtered = nearest.filter {
            it.getShortestDistance(target).lt(other = minLength, points = 12)
        }
        if (filtered.isEmpty()) return target
        filtered.print(title = "filtered")
        val intersections = filtered.filter { vector ->
            vector.getIntersection(
                c = player.point,
                d = target,
            ) != null
        }
        if (intersections.size != 1) {
            println("Intersection size: ${intersections.size}!")
            return null // todo
        }
        val barrier = intersections.single()
        println("barrier: $barrier")
        val correctedPoint = getCorrectedPoint(
            minLength = minLength,
            target = target,
            barrier = barrier,
        )
        println("corrected: $correctedPoint")
        val finals = nearest.filter {
            it.getShortestDistance(correctedPoint).lt(other = minLength, points = 12)
        }
        if (finals.isNotEmpty()) {
            finals.print(title = "finals")
            return null // todo
        }
        return correctedPoint
    }

    @Deprecated(message = "getFinalPoint")
    private fun getFinalPointOld(
        player: Player,
        minLength: Double,
        target: Point,
        barriers: List<Vector>,
    ): Point? {
        // todo Intersection size: 2!
        // todo Intersection size: 0! Filtered size: 1! Barrier is parallel!
        // todo barriers.any { it.getShortestDistance(finalPoint) < minLength }
        val targetDistance = distanceOf(player.point, target)
        val filtered = barriers.filter {
//            it.getShortestDistance(target).lt(other = minLength, points = 12)
            it.getShortestDistance(player.point).lt(other = targetDistance + minLength, points = 12)
        }
        if (filtered.isEmpty()) {
//            println("Filtered points are empty!")
            return target
        }
        val parallels = filtered.filter { vector ->
            vector.isParallel(
                c = player.point,
                d = target,
            )
        }
        val intersections = filtered.associateWithNotNull { vector ->
//            val cd = player.point + target
//            if (vector.isParallel(cd)) println("Vector $vector is parallel to $cd!")
//            if (vector.isCollinear(cd.start)) println("Vector $vector is collinear to ${cd.start}!")
//            if (vector.isCollinear(cd.finish)) println("Vector $vector is collinear to ${cd.finish}!")
            vector.getIntersection(
                c = player.point,
                d = target,
            )
        }
        if (intersections.isEmpty()) {
            if (filtered.size == 1) {
                val barrier = filtered.single()
                val isParallel = barrier.isParallel(
                    c = player.point,
                    d = target,
                )
                println("Intersection points are empty! Filtered: $barrier is parallel($isParallel)")
//                if (isParallel) return target // todo
                return null // todo
            } else {
                val message = """
                    |Intersection points are empty!
                    |point: ${player.point + target}
                    |filtered:
                    ${filtered.joinToString(separator = "\n") { "| - $it" }}
                """.trimMargin()
                println(message)
                return null // todo
            }
        }
        if (intersections.size != 1) {
            println("Intersection size: ${intersections.size}!")
            return null // todo
        }
        if (parallels.isNotEmpty()) {
            println("Parallels size: ${parallels.size}!")
            return null // todo
        }
        val (barrier, intersection) = intersections.entries.single()
        /*
        if (intersection == null) {
            // dis: 1.414213562373095
            // min: 1.4142135623730951
            val shortestBarrier = barriers.firstOrNull {
                it.getShortestDistance(target).lt(other = minLength, points = 12)
            }
            if (shortestBarrier != null) {
                println("No intersection with: $barrier!")
                val distance = shortestBarrier.getShortestDistance(target)
                println("Shortest barrier: $shortestBarrier\ndistance: $distance\nmin: $minLength")
                return null // todo
            }
//            val allowed = !barriers.any { it.getShortest(target) < minLength}
//            if (!allowed) {
//                println("No intersection with $barrier!")
//                println("Couldn't calculate the final point!")
//                return null // todo
//            }
            return target
        }
        */
        // todo check (player.point + target) contains intersection
        //
//        val perpendicular = barrier.getPerpendicular(target = target)
//        val angle = angleOf(a = perpendicular, b = target)
//        val finalPoint = perpendicular.moved(length = minLength, angle = angle)
        //
        val correctedPoint = getCorrectedPoint(
            minLength = minLength,
            target = target,
            barrier = barrier,
        )
        //
//        val barrier = barriers.firstOrNull { it.getShortest(finalPoint) < minLength}
//        if () {
//            println("Couldn't calculate the final point!")
//            return null // todo
//        }
        println("final: $correctedPoint")
        val finals = barriers.filter {
            it.getShortestDistance(correctedPoint).lt(other = minLength, points = 12)
        }
        if (finals.isNotEmpty()) {
            println("Finals:\n${finals.joinToString(separator = "\n")}\n")
            return null // todo
        }
        return correctedPoint
//        return null // todo
    }

    private fun allowed(
        player: Player,
        target: Point,
        barriers: List<Vector>,
    ): Boolean {
        val filtered = barriers.filter {
            it.getShortestDistance(target) < player.radius
        }
        if (filtered.isEmpty()) {
            return true
        }
//        val intersections = filtered.mapNotNull {
//            getIntersectionPointOrNull(
//                v1 = it,
//                v2 = player.point + target,
//            )
//        } // todo
        return false // todo
//        return true // todo
    }

    override fun onRender(canvas: Canvas) {
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
        val playerOffset = engine.input.keyboard.getPlayerOffset()
        onRenderIntersections(
            canvas = canvas,
            actual = player.point,
            target = player.point.moved(
                length = player.speed.length(timeDiff),
                angle = player.direction.expected,
            ),
            offset = offset,
            barriers = barriers,
            measure = measure,
        ) // todo
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
            val target = player.point.moved(
                length = player.speed.length(timeDiff),
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
                barriers = barriers,
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
        canvas.drawCircle(
            color = Color.WHITE,
            pointCenter = center + measure,
            radius = measure.transform(player.radius),
            edgeCount = 16,
            lineWidth = 1f
        )
        canvas.drawCircle(
            color = Color.BLUE,
            pointCenter = center + measure,
            radius = measure.transform(player.radius + player.speed.length(16.milliseconds)),
            edgeCount = 32,
            lineWidth = 1f
        ) // todo
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
