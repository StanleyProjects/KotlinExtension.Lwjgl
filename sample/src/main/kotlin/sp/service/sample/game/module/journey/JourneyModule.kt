package sp.service.sample.game.module.journey

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.util.drawCircle
import sp.kx.math.foundation.entity.geometry.Point
import sp.kx.math.foundation.entity.geometry.Vector
import sp.kx.math.implementation.entity.geometry.getAngle
import sp.kx.math.implementation.entity.geometry.getDistance
import sp.kx.math.implementation.entity.geometry.getIntersectionPointOrNull
import sp.kx.math.implementation.entity.geometry.getPerpendicular
import sp.kx.math.implementation.entity.geometry.getShortest
import sp.kx.math.implementation.entity.geometry.isEmpty
import sp.kx.math.implementation.entity.geometry.isSame
import sp.kx.math.implementation.entity.geometry.pointOf
import sp.kx.math.implementation.entity.geometry.updated
import sp.kx.math.implementation.entity.geometry.vectorOf
import sp.service.sample.game.entity.MutablePoint
import sp.service.sample.util.FontInfoUtil
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.exp

class JourneyModule(private val engine: Engine, private val broadcast: (Broadcast) -> Unit) {
    sealed interface Broadcast {
        object Exit : Broadcast
    }

    companion object {
        private const val pixelsPerUnit = 16.0 // todo
    }

    class Direction(var actual: Double, var expected: Double, val velocity: Double)
//    class Region(val points: List<Point>, val color: Color)
    private val velocity: Double = 5 / TimeUnit.SECONDS.toNanos(1).toDouble()
    private val direction: Direction = Direction(
        actual = 0.0,
        expected = 0.0,
        velocity = kotlin.math.PI * 2 / TimeUnit.SECONDS.toNanos(1).toDouble()
    )
    private val point = MutablePoint(x = 0.0, y = 0.0)
    private val width = pixelsPerUnit * 2
    private val radius = kotlin.math.sqrt(2.0) * width / 2
    private val barriers = listOf(
        vectorOf(
            start = pointOf(x = 7 + 0 * 2, y = 7 + 0 * 2),
            finish = pointOf(x = 7 + 3 * 2, y = 7 - 5 * 2)
        ),
        vectorOf(
            start = pointOf(x = 7 + 3 * 2, y = 7 - 5 * 2),
            finish = pointOf(x = 7 + 5 * 2, y = 7 - 5 * 2)
        ),
        vectorOf(
            start = pointOf(x = 7 + 5 * 2, y = 7 - 5 * 2),
            finish = pointOf(x = 7 + 5 * 2, y = 7 + 6 * 2)
        )
    ).map { vector ->
        vectorOf(
            start = pointOf(x = vector.start.x * pixelsPerUnit, y = vector.start.y * pixelsPerUnit),
            finish = pointOf(x = vector.finish.x * pixelsPerUnit, y = vector.finish.y * pixelsPerUnit)
        )
    }
//    private val regions = listOf(
//        Region(
//            points = listOf(
//                pointOf(x = 7 + 0 * 2, y = 7 + 0 * 2),
//                pointOf(x = 7 + 3 * 2, y = 7 - 5 * 2),
//                pointOf(x = 7 + 5 * 2, y = 7 - 5 * 2),
//                pointOf(x = 7 + 5 * 2, y = 7 + 6 * 2),
//            ).map {
//                pointOf(x = it.x * pixelsPerUnit, y = it.y * pixelsPerUnit)
//            },
//            color = Color.WHITE
//        )
//    )

    fun init() {
//        point.x = engine.property.pictureSize.width / 2
//        point.y = engine.property.pictureSize.height / 2
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
        return ((this % k) + k) % k
    }

//    private fun Region.getVectors(): List<Vector> {
//        return (0 until points.lastIndex).map { index ->
//            vectorOf(start = points[index], finish = points[index + 1])
//        } + vectorOf(start = points.last(), finish = points.first())
//    }

//    private fun Iterable<Region>.getVectors(): List<Vector> {
//        return flatMap {
//            it.getVectors()
//        }
//    }

    class Triangle(val a: Point, val b: Point, val c: Point) {
        class Environment(
            val ab: Double,
            val ac: Double,
            val height: Point,
            val shortest: Double
        )
    }

    /**
     *     a
     *    /|\
     *   / |  \
     *  /  aH   \
     * b---*-----c
     * | x |bc-x |
     */
    private fun Triangle.getEnvironment(): Triangle.Environment {
        val abX = a.x - b.x
        val abY = a.y - b.y
        val ab = kotlin.math.sqrt(abX * abX + abY * abY)
        val bcX = b.x - c.x
        val bcY = b.y - c.y
        val bc = kotlin.math.sqrt(bcX * bcX + bcY * bcY)
        val acX = a.x - c.x
        val acY = a.y - c.y
        val ac = kotlin.math.sqrt(acX * acX + acY * acY)
        val x = ((ac * ac) - (bc * bc) - (ab * ab)) / (2 * bc)
        val aH = kotlin.math.sqrt((ab * ab) - (x * x))
        val shortest = when {
            kotlin.math.sqrt((ab * ab) - (aH * aH)) > bc -> ac
            kotlin.math.sqrt((ac * ac) - (aH * aH)) > bc -> ab
            else -> aH
        }
        val perpendicular = getPerpendicular(
            a = a,
            b = b,
            c = c
        )
        return Triangle.Environment(
            ab = ab,
            ac = ac,
            height = perpendicular,
            shortest = shortest
        )
    }

    private fun Double.isLessThan(that: Double, epsilon: Double): Boolean {
        check(epsilon < 1.0)
        val d = this - that
        return d.absoluteValue > epsilon && d < 0
    }

    private fun Double.isMoreThan(that: Double, epsilon: Double): Boolean {
        check(epsilon < 1.0)
        val d = this - that
        return d.absoluteValue > epsilon && d > 0
    }

    private fun onRenderPlayer(canvas: Canvas, point: Point) {
        canvas.drawLine(
            color = Color.WHITE,
            vector = vectorOf(start = point, length = radius, direction = direction.actual),
            lineWidth = 1f
        )
        val size = size(width = width, height = width)
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

//    private fun onRenderRegions(canvas: Canvas, center: Point, point: Point, regions: List<Region>) {
//        val dX = center.x - point.x
//        val dY = center.y - point.y
//        regions.forEach { region ->
//            canvas.drawLineLoop(
//                color = region.color,
//                points = region.points.map {
//                    it.updated(dX = dX, dY = dY)
//                },
//                lineWidth = 1f
//            )
//        }
//    }

    private fun onRenderBarriers(canvas: Canvas, center: Point, point: Point, barriers: List<Vector>) {
        val dX = center.x - point.x
        val dY = center.y - point.y
        barriers.forEach { barrier ->
            canvas.drawLine(
                color = Color.GREEN,
                vector = vectorOf(
                    start = barrier.start.updated(dX = dX, dY = dY),
                    finish = barrier.finish.updated(dX = dX, dY = dY)
                ),
                lineWidth = 1f
            )
        }
    }

    private fun onRenderCenter(canvas: Canvas, center: Point) {
        val relative = center.updated(dX = -point.x, dY = -point.y)
        val length = pixelsPerUnit * 2
        canvas.drawLine(
            color = Color.GREEN,
            vector = vectorOf(
                start = relative.updated(dX = 0.0, dY = length),
                finish = relative.updated(dX = 0.0, dY = -length)
            ),
            lineWidth = 1f
        )
        canvas.drawLine(
            color = Color.GREEN,
            vector = vectorOf(
                start = relative.updated(dX = -length, dY = 0.0),
                finish = relative.updated(dX = length, dY = 0.0)
            ),
            lineWidth = 1f
        )
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val text = "0/0"
        canvas.drawText(
            color = Color.GREEN,
            info = info,
            pointTopLeft = relative.updated(
                dX = (info.height / 2).toDouble(),
                dY = (-info.height).toDouble()
            ),
            text = text
        )
    }

    private fun onRenderTriangles(canvas: Canvas, center: Point, triangles: Iterable<Triangle>) {
        val colors = listOf(
            Color.YELLOW,
            Color.RED,
            Color.BLUE,
            Color.GREEN,
        )
        val xOffset = center.x - point.x
        val yOffset = center.y - point.y
        val info = FontInfoUtil.getFontInfo(height = 16f)
        triangles.forEachIndexed { index, triangle ->
            val environment = triangle.getEnvironment()
            val start = triangle.a.updated(dX = xOffset, dY = yOffset)
            val color = colors[index % colors.size]
            val ab = vectorOf(start = start, finish = triangle.b.updated(dX = xOffset, dY = yOffset))
            canvas.drawLine(
                color = color,
                vector = ab,
                lineWidth = 1f
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = ab.start.updated(
                    dX = (ab.finish.x - ab.start.x) / 2,
                    dY = (ab.finish.y - ab.start.y) / 2
                ),
                text = String.format("%05.2f", environment.ab)
            )
            val ac = vectorOf(start = start, finish = triangle.c.updated(dX = xOffset, dY = yOffset))
            canvas.drawLine(
                color = color,
                vector = ac,
                lineWidth = 1f
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = ac.start.updated(
                    dX = (ac.finish.x - ac.start.x) / 2,
                    dY = (ac.finish.y - ac.start.y) / 2
                ),
                text = String.format("%05.2f", environment.ac)
            )
            val tPoint = ab.finish.updated(
                dX = (ac.finish.x - ab.finish.x) / 2,
                dY = (ac.finish.y - ab.finish.y) / 2
            )
            val aH = vectorOf(
                start = triangle.a.updated(dX = xOffset, dY = yOffset),
                finish = environment.height.updated(dX = xOffset, dY = yOffset)
            )
            canvas.drawLine(
                color = color,
                vector = aH,
                lineWidth = 1f
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = tPoint,
                text = String.format("%05.2f", aH.getDistance())
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = tPoint.updated(dX = 0.0, dY = info.height.toDouble()),
                text = String.format("%05.2f", environment.shortest)
            )
        }
    }

    fun onRender(canvas: Canvas) {
        val center = pointOf(x = engine.property.pictureSize.width / 2, y = engine.property.pictureSize.height / 2)
        onRenderCenter(canvas, center)
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
        val dVector = vectorOf(start = point, finish = point.updated(dX = dX, dY = dY))
        if (!dVector.isEmpty(epsilon = 0.0001)) {
            val dTime = engine.property.timeNow - engine.property.timeLast
            direction.expected = dVector.getAngle().normalize(kotlin.math.PI * 2)
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
            val vector = vectorOf(start = point, length = units, direction = direction.expected)
            val expected = barriers.map { barrier ->
                Triangle(a = vector.finish, b = barrier.start, c = barrier.finish)
            }
//            onRenderTriangles(canvas, center = center, triangles = expected)
            val filtered = expected.filter {
                it.getEnvironment().shortest.isLessThan(radius, epsilon = 0.0001)
            }
            if (filtered.isEmpty()) {
                point.x = vector.finish.x
                point.y = vector.finish.y
            } else {
//                onRenderTriangles(canvas, center = center, triangles = filtered)
                val triangles = filtered.mapNotNull { triangle ->
                    vector.getIntersectionPointOrNull(
                        vectorOf(start = triangle.b, finish = triangle.c)
                    )?.let {
                        triangle to it
                    }
                }.filter { (_, iPoint) ->
//
//                      (b,c) triangle.b -> triangle.c
//                      (s,f) distance
//                      (f,i) fDistance
//                      (s,i) sDistance
//
//                      b---------i--c
//                               .
//                              .
//                            s*f
//                            /
//                           /
//                          /
//                        f*s
//
                    val sDistance = getDistance(start = vector.start, finish = iPoint)
                    val fDistance = getDistance(start = vector.finish, finish = iPoint)
                    val distance = vector.getDistance()
                    fDistance < sDistance && distance < sDistance
                }
                if (triangles.isEmpty()) {
                    println("triangles are empty")
                    // todo
                } else if (triangles.size == 1) {
                    val (triangle, iPoint) = triangles.single()
                    val fShortest = triangle.getEnvironment().shortest
/*
                    (bc) triangle.b -> triangle.c
                    (f1) fShortest
                    (m1) radius

                    b--1----i--c
                       .   .
                    ...m.......
                       . .
                       ..
                       f
                      /
                     /
                    s
*/
                    if (!fShortest.isLessThan(radius, epsilon = 0.0001)) {
                        point.x = vector.finish.x
                        point.y = vector.finish.y
                    } else {
                        val sDistance = getDistance(start = vector.start, finish = iPoint)
                        val vDistance = vector.getDistance()
                        if (vDistance < sDistance) {
                            val fPerpendicular = getPerpendicular(
                                a = vector.finish,
                                b = triangle.b,
                                c = triangle.c
                            )
                            val fAngle = vectorOf(start = fPerpendicular, finish = vector.finish).getAngle()
                            val m = vectorOf(start = fPerpendicular, length = radius, direction = fAngle).finish
//                            val sPerpendicular = getPerpendicular(
//                                a = vector.start,
//                                b = triangle.b,
//                                c = triangle.c
//                            )
//                            val a = getDistance(start = vector.start, finish = sPerpendicular)
//                            val c = getDistance(start = vector.start, finish = iPoint)
//                            val sAngle = kotlin.math.asin(a / c)
//                            val i2 = radius * kotlin.math.tan(sAngle)
//                            val iDistance = kotlin.math.sqrt(i2 * i2 + radius * radius)
//                            val target = iPoint.updated(
//                                dX = ,
//                                dY = ,
//                            )
//                            val fDistance = getDistance(start = vector.finish, finish = iPoint)
//                            val sShortest = Triangle(a = vector.start, b = triangle.b, c = triangle.c).getEnvironment().shortest
//                            val target = vector.start.updated(
//                                dX = (1 - radius / sShortest) * (iPoint.x - vector.start.x),
//                                dY = (1 - radius / sShortest) * (iPoint.y - vector.start.y)
//                            )
//                            val tShortest = Triangle(a = target, b = triangle.b, c = triangle.c).getEnvironment().shortest
/*
                            (bc) triangle.b -> triangle.c
                            (si) sDistance
                            (fi) sDistance
                            (si) sDistance
                            (s1) sShortest
                            (t2) tShortest tPerpendicular
                            (f3) fPerpendicular

                            b--1--2--3--i--c
                               .  :  ! .
                               .  :  !.
                               .  :  f
                               .  : /!
                               .  :/ !
                            ......t..m......
                               . /
                               ./
                               s
*/
                            point.x = m.x
                            point.y = m.y
//                            if (!tShortest.isLessThan(radius, epsilon = 0.0001)) {
//                                val tPerpendicular = getPerpendicular(
//                                    a = target,
//                                    b = triangle.b,
//                                    c = triangle.c
//                                )
//                                val fPerpendicular = getPerpendicular(
//                                    a = vector.finish,
//                                    b = triangle.b,
//                                    c = triangle.c
//                                )
//                                point.x = target.x + fPerpendicular.x - tPerpendicular.x
//                                point.y = target.y + fPerpendicular.y - tPerpendicular.y
//                                val h = Triangle(a = point, b = triangle.b, c = triangle.c).getEnvironment().height
//                                println("distance:" + getDistance(point, h))
//                            } else {
//                                println("tShortest: $tShortest radius: $radius")
//                            }
                        } else {
                            println("fShortest: $fShortest radius: $radius")
                            // todo
                        }
                    }
                } else {
                    println("size: " + triangles.size)
                    // todo size == 2
                }
            }
        }
        val actual = barriers.map { barrier ->
            Triangle(a = point, b = barrier.start, c = barrier.finish)
        }
        onRenderTriangles(canvas, center = center, triangles = actual)
        onRenderPlayer(canvas, point = center)
        onRenderBarriers(canvas, center = center, point = point, barriers = barriers)
    }

    fun onRenderOld(canvas: Canvas) {
        val center = pointOf(x = engine.property.pictureSize.width / 2, y = engine.property.pictureSize.height / 2)
        onRenderCenter(canvas, center)
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
        //
        val dTime = engine.property.timeNow - engine.property.timeLast
        direction.expected = vector.getAngle().normalize(kotlin.math.PI * 2)
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
        val finish = vectorOf(start = point, length = units, direction = direction.expected).finish
//        val vectors = regions.getVectors()
        val group = barriers.groupBy {
            it.getShortest(target = finish)
        }
        val shortest = group.keys.minOrNull()!!
        val colors = listOf(
            Color.YELLOW,
            Color.RED,
            Color.BLUE,
            Color.GREEN,
        )
//        group[shortest]!!.also {
//        group.forEach { (k, list) ->
        group.toList().forEachIndexed { index, (k, list) ->
            val dX = center.x - point.x
            val dY = center.y - point.y
            val v = list.first()
            val start = point.updated(dX = dX, dY = dY)
            val color = colors[index % colors.size]
            canvas.drawLine(
                color = color,
                vector = vectorOf(start = start, finish = v.start.updated(dX = dX, dY = dY)),
                lineWidth = 1f
            )
            canvas.drawLine(
                color = color,
                vector = vectorOf(start = start, finish = v.finish.updated(dX = dX, dY = dY)),
                lineWidth = 1f
            )
            canvas.drawLine(
                color = color,
                vector = vectorOf(start = v.start.updated(dX = dX, dY = dY), finish = v.finish.updated(dX = dX, dY = dY)),
                lineWidth = 3f
            )
            val info = FontInfoUtil.getFontInfo(height = 16f)
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = v.finish.updated(dX = dX, dY = dY),
                text = String.format("$index) %05.1f", k)
            )
        }
        println("shortest: $shortest min: $radius")
        val allowed = !shortest.isLessThan(radius, epsilon = 0.0001)
        if (allowed) {
            if (!vector.isEmpty(epsilon = 0.0001)) {
                point.x = finish.x
                point.y = finish.y
            } // todo
        }
        //
//        if (!vector.isEmpty(epsilon = 0.0001)) {
//            move(canvas = canvas, center = center, angle = vector.getAngle())
//        }
        onRenderPlayer(canvas, point = center)
//        onRenderRegions(canvas, center = center, point = point, regions = regions)
        onRenderBarriers(canvas, center = center, point = point, barriers = barriers)
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
