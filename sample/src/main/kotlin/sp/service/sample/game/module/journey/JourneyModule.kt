package sp.service.sample.game.module.journey

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.util.drawCircle
import sp.kx.math.MutableOffset
import sp.kx.math.MutablePoint
import sp.kx.math.Point
import sp.kx.math.Vector
import sp.kx.math.minus
import sp.kx.math.moved
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.toVector
import sp.kx.math.isEmpty
import sp.kx.math.length
import sp.kx.math.distanceOf
import sp.kx.math.angleOf
import sp.kx.math.angle
import sp.kx.math.radians
import sp.kx.math.measure.diff
import sp.kx.math.sizeOf
import sp.kx.math.vectorOf
import sp.service.sample.util.FontInfoUtil
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

private fun Double.isLessThan(that: Double, epsilon: Double): Boolean {
    check(epsilon < 1.0)
    val d = this - that
    return d.absoluteValue > epsilon && d < 0
}

private fun Double.isSame(that: Double, epsilon: Double): Boolean {
    check(epsilon < 1.0)
    return (this - that).absoluteValue < epsilon
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

private fun getPerpendicular(
    a: Point,
    b: Point,
    c: Point
): Point {
    return getPerpendicular(
        aX = a.x,
        aY = a.y,
        bX = b.x,
        bY = b.y,
        cX = c.x,
        cY = c.y
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

private fun getShortest(
    start: Point,
    finish: Point,
    target: Point
): Double {
    return getShortest(
        xStart = start.x,
        yStart = start.y,
        xFinish = finish.x,
        yFinish = finish.y,
        xTarget = target.x,
        yTarget = target.y
    )
}

private fun Vector.getShortest(target: Point): Double {
    return getShortest(
        start = start,
        finish = finish,
        target = target
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
    dY: Double
): Point? {
    val iX: Double
    val a1 = (aY - bY) / (aX - bX)
    val b1 = aY - a1 * aX
    val a2 = (cY - dY) / (cX - dX)
    val b2 = cY - a2 * cX
    if (aX == bX) {
        if (cX == dX) {
            if (aY == cY) return pointOf(x = aX, y = aY)
            return null
        }
        iX = aX
    } else if (cX == dX) {
        iX = cX
    } else {
        iX = (b2 - b1) / (a1 - a2)
    }
    return pointOf(
        x = iX,
        y = a2 * aX + b2
    )
}

private fun getIntersectionPointOrNull(
    a: Point,
    b: Point,
    c: Point,
    d: Point
): Point? {
    return getIntersectionPointOrNull(
        aX = a.x,
        aY = a.y,
        bX = b.x,
        bY = b.y,
        cX = c.x,
        cY = c.y,
        dX = d.x,
        dY = d.y
    )
}

private fun Vector.getIntersectionPointOrNull(that: Vector): Point? {
    return getIntersectionPointOrNull(
        a = this.start,
        b = this.finish,
        c = that.start,
        d = that.finish
    )
}

private fun Vector.getPerpendicular(target: Point): Point {
    return getPerpendicular(
        a = target,
        b = start,
        c = finish
    )
}

private fun Vector.getCenter(): Point {
    return start.plus(
        dX = (finish.x - start.x) * 0.5,
        dY = (finish.y - start.y) * 0.5
    )
}

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
    private val barriers = listOf(
        pointOf(x = 7.0 + 0 * 2, y = 7.0 + 0 * 2) + pointOf(x = 7.0 + 3 * 2, y = 7.0 - 5 * 2),
        pointOf(x = 7.0 + 3 * 2, y = 7.0 - 5 * 2) + pointOf(x = 7.0 + 5 * 2, y = 7.0 - 5 * 2),
        pointOf(x = 7.0 + 5 * 2, y = 7.0 - 5 * 2) + pointOf(x = 7.0 + 5 * 2, y = 7.0 + 6 * 2)
    ).map { vector ->
        pointOf(x = vector.start.x * pixelsPerUnit, y = vector.start.y * pixelsPerUnit) +
                pointOf(x = vector.finish.x * pixelsPerUnit, y = vector.finish.y * pixelsPerUnit)
    }

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

    private fun onRenderPlayer(canvas: Canvas, point: Point) {
        canvas.drawLine(
            color = Color.WHITE,
            vector = vectorOf(point, length = radius, angle = direction.actual),
            lineWidth = 1f
        )
        val size = sizeOf(width = width, height = width)
        canvas.drawRectangle(
            color = Color.YELLOW,
            pointTopLeft = point.plus(dX = - size.width / 2, dY = - size.height / 2),
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

    private fun onRenderBarriers(canvas: Canvas, center: Point, point: Point, barriers: List<Vector>) {
        val offset = center - point
        barriers.forEach { barrier ->
            canvas.drawLine(
                color = Color.GREEN,
                vector = barrier.start.toVector(
                    finish = barrier.finish,
                    offset = offset,
                ),
                lineWidth = 1f
            )
        }
    }

    private fun onRenderCenter(canvas: Canvas, center: Point) {
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
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val text = "0/0"
        canvas.drawText(
            color = Color.GREEN,
            info = info,
            pointTopLeft = relative.plus(
                dX = (info.height / 2).toDouble(),
                dY = (-info.height).toDouble()
            ),
            text = text
        )
    }

    private fun onRenderTriangles(canvas: Canvas, center: Point, barriers: List<Vector>) {
        val colors = listOf(
            Color.YELLOW,
            Color.RED,
            Color.BLUE,
            Color.GREEN,
        )
        val offset = center - point
        val info = FontInfoUtil.getFontInfo(height = 16f)
        barriers.forEachIndexed { index, barrier ->
            val color = colors[index % colors.size]
            val ab = point.toVector(finish = barrier.start, offset = offset)
            canvas.drawLine(
                color = color,
                vector = ab,
                lineWidth = 1f
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = ab.getCenter(),
                text = String.format("%05.2f", distanceOf(a = point, b = barrier.start))
            )
            val ac = point.toVector(finish = barrier.finish, offset = offset)
            canvas.drawLine(
                color = color,
                vector = ac,
                lineWidth = 1f
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = ac.getCenter(),
                text = String.format("%05.2f", distanceOf(a = point, b = barrier.finish))
            )
            val bc = ab.finish + ac.finish
            val tPoint = bc.getCenter()
            val aH = point.toVector(
                finish = barrier.getPerpendicular(target = point),
                offset = offset
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
                text = String.format("%05.2f", aH.length())
            )
            canvas.drawText(
                color = color,
                info = info,
                pointTopLeft = tPoint.plus(dX = 0.0, dY = info.height.toDouble()),
                text = String.format("%05.2f", barrier.getShortest(target = point))
            )
        }
    }

    fun onRender(canvas: Canvas) {
        val center = pointOf(x = engine.property.pictureSize.width / 2, y = engine.property.pictureSize.height / 2)
        onRenderCenter(canvas, center)
        val keyboard = engine.input.keyboard
        val mOffset = MutableOffset(dX = 0.0, dY = 0.0)
//        var dX = 0.0
//        var dY = 0.0
        if (keyboard.isPressed(KeyboardButton.W)) {
            if (!keyboard.isPressed(KeyboardButton.S)) {
                mOffset.dY = -1.0
            }
        } else {
            if (keyboard.isPressed(KeyboardButton.S)) {
                mOffset.dY = 1.0
            }
        }
        if (keyboard.isPressed(KeyboardButton.A)) {
            if (!keyboard.isPressed(KeyboardButton.D)) {
                mOffset.dX = -1.0
            }
        } else {
            if (keyboard.isPressed(KeyboardButton.D)) {
                mOffset.dX = 1.0
            }
        }
        val dVector = point.toVector(mOffset)
        if (!dVector.isEmpty(points = 4)) {
//            val dTime = engine.property.timeNow - engine.property.timeLast
            val diff = engine.property.time.diff()
            direction.expected = dVector.angle().radians()
            if (!direction.expected.isSame(direction.actual, epsilon = 0.0001)) {
                val difference = direction.actual - direction.expected
                val d = direction.velocity * diff.inWholeNanoseconds
                if (d > difference.absoluteValue) {
                    direction.actual = direction.expected
                } else {
                    // todo
                    val actual: Double = if (difference.absoluteValue > kotlin.math.PI) {
                        direction.actual + d * difference / difference.absoluteValue
                    } else {
                        direction.actual + d * difference / difference.absoluteValue * -1
                    }
                    direction.actual = actual.radians()
                }
            }
            val vector = vectorOf(
                point,
                length = velocity * diff.inWholeNanoseconds * pixelsPerUnit,
                angle = direction.expected,
            )
            val filtered = barriers.filter {
                it.getShortest(vector.finish).isLessThan(radius, epsilon = 0.0001)
            }
            if (filtered.isEmpty()) {
                point.set(vector.finish)
            } else {
                val iPoints = filtered.mapNotNull { that ->
                    vector.getIntersectionPointOrNull(that)?.let { that to it }
                }.filter { (_, iPoint) ->
                    val sDistance = distanceOf(a = vector.start, b = iPoint)
                    val fDistance = distanceOf(a = vector.finish, b = iPoint)
                    val distance = vector.length()
                    fDistance < sDistance && distance < sDistance
                }
                if (iPoints.isEmpty()) {
                    println("Intersection points are empty!")
                    // todo
                } else if (iPoints.size == 1) {
                    val (that, iPoint) = iPoints.single()
                    val fShortest = that.getShortest(vector.finish)
                    if (!fShortest.isLessThan(radius, epsilon = 0.0001)) {
                        point.set(vector.finish)
                    } else {
                        val sDistance = distanceOf(a = vector.start, b = iPoint)
                        val vDistance = vector.length()
                        if (vDistance < sDistance) {
                            val fPerpendicular = that.getPerpendicular(vector.finish)
                            val fAngle = angleOf(a = fPerpendicular, b = vector.finish)
                            val m = fPerpendicular.moved(length = radius, angle = fAngle)
                            point.set(m)
                        } else {
                            println("fShortest: $fShortest radius: $radius")
                            // todo
                        }
                    }
                } else {
                    println("size: " + iPoints.size)
                    // todo size == 2
                }
            }
        }
        onRenderTriangles(canvas, center = center, barriers = barriers)
        onRenderPlayer(canvas, point = center)
        onRenderBarriers(canvas, center = center, point = point, barriers = barriers)
    }

    fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
        when (button) {
            KeyboardButton.ESCAPE -> {
                if (isPressed) {
                    broadcast(Broadcast.Exit)
                }
            }
            else -> {
                println("on button: $button $isPressed")
            }
        }
    }
}
