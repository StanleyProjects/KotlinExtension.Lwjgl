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
import sp.kx.math.contains
import sp.kx.math.copy
import sp.kx.math.dby
import sp.kx.math.distanceOf
import sp.kx.math.eq
import sp.kx.math.getPerpendicular
import sp.kx.math.getShortest
import sp.kx.math.ifNaN
import sp.kx.math.isEmpty
import sp.kx.math.length
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
import sp.kx.math.toVector
import sp.kx.math.vectorOf
import sp.kx.math.whc
import sp.service.sample.util.FontInfoUtil
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.pow

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

    private fun debug(previous: Point, canvas: Canvas) {
        val padding = measure.transform(1.0)
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val x = padding
        val (bi, barrier, shortest) = barriers
            .mapIndexed { index, it -> Triple(index, it, it.getShortest(player.point)) }
            .minBy { (_, _, shortest) ->
                shortest
            }
        val currentSpeed = speedOf(magnitude = distanceOf(previous, player.point), engine.property.time.diff())
        val values = listOf(
//            "x: ${point.x.toString(5, 1)}",
//            "y: ${point.y.toString(5, 1)}",
//            String.format("x: %+05.1f", point.x),
//            String.format("y: %+05.1f", point.y),
            String.format("x: %7s", String.format("%+.1f", player.point.x)),
            String.format("y: %7s", String.format("%+.1f", player.point.y)),
            String.format("max speed: %s/s", player.speed.per(TimeUnit.SECONDS).toString(points = 2)),
            String.format("cur speed: %s/s", currentSpeed.per(TimeUnit.SECONDS).toString(points = 2)),
            String.format("a: %03.2f - %05.1f", player.direction.actual, Math.toDegrees(player.direction.actual)),
            String.format("e: %03.2f - %05.1f", player.direction.expected, Math.toDegrees(player.direction.expected)),
//            String.format("direction diff: %05.1f", Math.toDegrees(player.direction.diff())),
//            String.format("whc: %02.1f", player.direction.diff().absoluteValue.whc().ifNaN(1.0)),
            String.format("barrier: %s", barrier.toString()),
            String.format("barrier: $bi] $shortest"),
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

    private fun getSlope(
        xStart: Double,
        yStart: Double,
        xFinish: Double,
        yFinish: Double,
    ): Double {
        return (yFinish - yStart) / (xFinish - xStart)
    }

    private fun isCollinear(
        xStart: Double,
        yStart: Double,
        xFinish: Double,
        yFinish: Double,
        xTarget: Double,
        yTarget: Double,
    ): Boolean {
        return (yFinish - yStart) * (xTarget - xFinish) - (xFinish - xStart) * (yTarget - yFinish) == 0.0
    }

    private fun isCollinear(
        aX: Double,
        aY: Double,
        bX: Double,
        bY: Double,
        cX: Double,
        cY: Double,
        dX: Double,
        dY: Double,
    ): Boolean {
        return isCollinear(
            xStart = aX,
            yStart = aY,
            xFinish = bX,
            yFinish = bY,
            xTarget = cX,
            yTarget = cY,
        ) && isCollinear(
            xStart = aX,
            yStart = aY,
            xFinish = bX,
            yFinish = bY,
            xTarget = dX,
            yTarget = dY,
        )
    }

    private fun isParallel(
        aX: Double,
        aY: Double,
        bX: Double,
        bY: Double,
        cX: Double,
        cY: Double,
        dX: Double,
        dY: Double,
    ): Boolean {
        val slope1 = getSlope(
            xStart = aX,
            yStart = aY,
            xFinish = bX,
            yFinish = bY,
        )
        val slope2 = getSlope(
            xStart = cX,
            yStart = cY,
            xFinish = dX,
            yFinish = dY,
        )
        return slope1 == slope2
    }

    private fun getIntersectionPointOrNull(
        v1: Vector,
        v2: Vector,
    ): Point? {
        return getIntersectionPointOrNull(
            aX = v1.start.x,
            aY = v1.start.y,
            bX = v1.finish.x,
            bY = v1.finish.y,
            cX = v2.start.x,
            cY = v2.start.y,
            dX = v2.finish.x,
            dY = v2.finish.y,
        )
    }

    private fun isCollinear(
        v1: Vector,
        v2: Vector,
    ): Boolean {
        return isCollinear(
            aX = v1.start.x,
            aY = v1.start.y,
            bX = v1.finish.x,
            bY = v1.finish.y,
            cX = v2.start.x,
            cY = v2.start.y,
            dX = v2.finish.x,
            dY = v2.finish.y,
        )
    }

    private fun getIntersectionPointOrNull(
        aX: Double,
        aY: Double,
        bX: Double,
        bY: Double,
        cX: Double,
        cY: Double,
        dX: Double,
        dY: Double,
    ): Point? {
        val isCollinear = isCollinear(
            aX = aX,
            aY = aY,
            bX = bX,
            bY = bY,
            cX = cX,
            cY = cY,
            dX = dX,
            dY = dY,
        )
        if (isCollinear) {
            val cContains = contains(
                xStart = aX,
                yStart = aY,
                xFinish = bX,
                yFinish = bY,
                xTarget = cX,
                yTarget = cY,
            )
            if (cContains) return pointOf(x = cX, y = cY)
            val dContains = contains(
                xStart = aX,
                yStart = aY,
                xFinish = bX,
                yFinish = bY,
                xTarget = dX,
                yTarget = dY,
            )
            if (dContains) return pointOf(x = dX, y = dY)
            TODO("isCollinear: $isCollinear!")
        }
        val isParallel = isParallel(
            aX = aX,
            aY = aY,
            bX = bX,
            bY = bY,
            cX = cX,
            cY = cY,
            dX = dX,
            dY = dY,
        )
        if (isParallel) return null
        val xT = (aX * bY - aY * bX) * (cX - dX) - (aX - bX) * (cX * dY - cY * dX)
        val xB = (aX - bX) * (cY - dY) - (aY - bY) * (cX - dX)
        val x = xT / xB
        val yT = (aX * bY - aY * bX) * (cY - dY) - (aY - bY) * (cX * dY - cY * dX)
        val yB = (aX - bX) * (cY - dY) - (aY - bY) * (cX - dX)
        val y = yT / yB
        return pointOf(x = x, y = y)
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
            val intersection = getIntersectionPointOrNull(
                v1 = barrier,
                v2 = actual + target,
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
            val shortest = barrier.getShortest(target = player.point)
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

    private fun getFinalPoint(
        player: Player,
        minLength: Double,
        target: Point,
        barriers: List<Vector>,
    ): Point? {
        val filtered = barriers.filter {
//            it.getShortest(target) < minLength
            lt(it = it.getShortest(target), other = minLength, points = 12)
        }
        if (filtered.isEmpty()) {
            return target
        }
        val intersections = filtered.map {
            it to getIntersectionPointOrNull(
                v1 = it,
                v2 = player.point + target,
            )
        } // todo
        if (intersections.isEmpty()) {
            println("Intersection points are empty!")
            return null // todo
        }
        if (intersections.size != 1) {
            println("Intersection size: ${intersections.size}!")
            return null // todo
        }
        val (barrier, intersection) = intersections.single()
        if (intersection == null) {
            // dis: 1.414213562373095
            // min: 1.4142135623730951
            val shortestBarrier = barriers.firstOrNull {
//                it.getShortest(target) < minLength
                lt(it = it.getShortest(target), other = minLength, points = 12)
            }
            if (shortestBarrier != null) {
                println("No intersection with: $barrier!")
                val distance = shortestBarrier.getShortest(target)
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
        // todo check (player.point + target) contains intersection
        val perpendicular = barrier.getPerpendicular(target = target)
        val angle = angleOf(a = perpendicular, b = target)
        val finalPoint = perpendicular.moved(length = minLength, angle = angle)
//        val barrier = barriers.firstOrNull { it.getShortest(finalPoint) < minLength}
//        if () {
//            println("Couldn't calculate the final point!")
//            return null // todo
//        }
        return finalPoint
//        return null // todo
    }

    private fun allowed(
        player: Player,
        target: Point,
        barriers: List<Vector>,
    ): Boolean {
        val filtered = barriers.filter {
            it.getShortest(target) < player.radius
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

    private fun lt(it: Double, other: Double, points: Int): Boolean {
        val diff = it - other
        return (diff).absoluteValue > 10.0.pow(-points) && diff < 0
    }

    override fun onRender(canvas: Canvas) {
        val previous = player.point.copy()
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
        canvas.drawLine(
            color = Color.WHITE,
            vector = vectorOf(center, length = player.radius, angle = player.direction.actual) + measure,
            lineWidth = 1f
        )
        canvas.drawLine(
            color = Color.YELLOW,
            vector = vectorOf(center, length = player.radius, angle = player.direction.expected) + measure,
            lineWidth = 1f,
        )
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
