package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableOffset
import sp.kx.math.MutablePoint
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Vector
import sp.kx.math.angleOf
import sp.kx.math.distanceOf
import sp.kx.math.getShortestDistance
import sp.kx.math.getShortestPoint
import sp.kx.math.isEmpty
import sp.kx.math.lt
import sp.kx.math.measure.MutableDeviation
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.MutableSpeed
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.speedOf
import sp.kx.math.moved
import sp.kx.math.offsetOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.radians
import sp.kx.math.reaches
import sp.service.sample.Calculations
import sp.service.sample.Environment
import sp.service.sample.Interactions
import sp.service.sample.Renders
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Item
import sp.service.sample.entity.MutableMoving
import sp.service.sample.entity.MutableTurning
import sp.service.sample.entity.Player
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

internal class TestEngineLogics(private val engine: Engine) : EngineLogics {
    private lateinit var ses: Unit
    private val env = getEnvironment()
    private val calculations = Calculations(engine = engine, env = env)
    private val renders = Renders(engine = engine, env = env, holder = calculations.getHolder())
    private val interactions = Interactions(env = env, holder = calculations.getHolder())
    private val measure = MutableDoubleMeasure(24.0)

    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (!isPressed) return
            if (button == KeyboardButton.Escape && env.state == Environment.State.Walking) {
                ses = Unit
                return
            }
            when (button) {
                KeyboardButton.Minus -> {
                    if (measure.magnitude > 16.0) measure.magnitude -= 8.0
                }
                KeyboardButton.Equal -> {
                    if (measure.magnitude < 64.0) measure.magnitude += 8.0
                }
                else -> interactions.onPress(button)
            }
        }
    }

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private fun getCorrectedPoint(
        minDistance: Double,
        target: Point,
        point: Point,
    ): Point {
        val angle = angleOf(a = point, b = target)
        return point.moved(length = minDistance, angle = angle)
    }

    private fun getFinalPoint(
        player: Player,
        minDistance: Double,
        target: Point,
        vectors: List<Vector>,
        points: List<Point>,
    ): Point? {
        val targetDistance = distanceOf(player.moving.point, target)
        val nearest = vectors.filter { vector ->
            vector.reaches(target = player.moving.point, minDistance = targetDistance + minDistance, points = 8)
        }
        val anyCloser = nearest.reaches(target = target, minDistance = minDistance, points = 8)
        val conflictPoints = points.filter { point ->
            distanceOf(point, target).lt(other = minDistance, points = 12)
        }
        if (!anyCloser && conflictPoints.isEmpty()) return target
        val correctedPoints = nearest.map { vector ->
            getCorrectedPoint(
                minDistance = minDistance,
                target = target,
                point = vector.getShortestPoint(target = target),
            )
        } + conflictPoints.map { point ->
            getCorrectedPoint(
                minDistance = minDistance,
                target = target,
                point = point,
            )
        }
        val allowedPoints = correctedPoints.filter { point ->
            !nearest.reaches(target = point, minDistance = minDistance, points = 8) &&
                    points.none { distanceOf(it, point).lt(other = minDistance, points = 8) }
        }
        if (allowedPoints.isEmpty()) {
            return null // todo
        }
        return allowedPoints.maxByOrNull { point ->
            distanceOf(player.moving.point, point)
        }
    }

    private fun movePlayer() {
        val offset = engine.input.keyboard.getOffset(
            upKey = KeyboardButton.W,
            downKey = KeyboardButton.S,
            leftKey = KeyboardButton.A,
            rightKey = KeyboardButton.D,
        )
        if (offset.isEmpty()) return
        val timeDiff = engine.property.time.diff()
        env.player.turning.turn(
            radians = angleOf(offset).radians(),
            timeDiff = timeDiff,
        )
        val length = env.player.moving.speed.length(timeDiff)
        val multiplier = kotlin.math.min(1.0, distanceOf(offset))
        val target = env.player.moving.point.moved(
            length = length * multiplier,
            angle = env.player.turning.direction.expected,
        )
        val barriers = env.barriers.filter { barrier ->
            !barrier.opened
        }.map { it.vector }
        val finalPoint = getFinalPoint(
            player = env.player,
            minDistance = 1.0,
            target = target,
            vectors = env.walls + barriers,
            points = env.relays.map { it.point } + env.crates.map { it.point },
        ) ?: return
        env.player.moving.point.set(finalPoint)
    }

    override fun onRender(canvas: Canvas) {
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = 24.0,
            text = String.format("%6.2f", engine.property.time.frequency()),
            pointTopLeft = Point.Center,
        )
        if (env.state == Environment.State.Walking) {
            movePlayer()
        }
        calculations.onRender()
        renders.onRender(canvas = canvas, measure = measure)
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = 24.0,
            text = String.format("%2.2f", measure.magnitude),
            pointTopLeft = Point.Center,
            offset = offsetOf(dX = 0.0, engine.property.pictureSize.height - 24.0),
        )
    }

    companion object {
        private val index = AtomicLong(0)

        private fun getEnvironment(): Environment {
            return Environment(
                state = Environment.State.Walking,
                walls = listOf(
                    pointOf(-6, -4),
                    pointOf(-6, 4),
                ).toVectors(),
                player = Player(
                    id = UUID(0, index.incrementAndGet()),
                    moving = MutableMoving(
                        point = MutablePoint(x = 0.0, y = 0.0),
                        speed = MutableSpeed(magnitude = 8.0, timeUnit = TimeUnit.SECONDS),
                    ),
                    turning = MutableTurning(
                        direction = MutableDeviation(actual = 0.0, expected = 0.0),
                        speed = speedOf(kotlin.math.PI * 2),
                    ),
                ),
                crates = listOf(
                    Crate(
                        id = UUID(0x0000200000000000, 1),
                        point = pointOf(0, -6),
                    ),
                ),
                barriers = emptyList(),
                relays = emptyList(),
                items = listOf(
                    Item(
                        id = UUID(1, index.incrementAndGet()),
                        tags = setOf(UUID(2, 1)),
                        point = MutablePoint(6.0, -2.0),
                        owner = null,
                    ),
                    Item(
                        id = UUID(1, index.incrementAndGet()),
                        tags = setOf(UUID(2, 1)),
                        point = MutablePoint(6.0, 0.0),
                        owner = null,
                    ),
                    Item(
                        id = UUID(1, index.incrementAndGet()),
                        tags = setOf(UUID(2, 1)),
                        point = MutablePoint(8.0, 0.0),
                        owner = null,
                    ),
                    Item(
                        id = UUID(1, index.incrementAndGet()),
                        tags = setOf(UUID(2, 1)),
                        point = MutablePoint(6.0, 2.0),
                        owner = null,
                    ),
                ),
                conditions = emptyList(),
            )
        }

        private fun Keyboard.getOffset(
            upKey: KeyboardButton,
            downKey: KeyboardButton,
            leftKey: KeyboardButton,
            rightKey: KeyboardButton,
        ): Offset {
            val result = MutableOffset(dX = 0.0, dY = 0.0)
            val down = isPressed(downKey)
            if (isPressed(upKey)) {
                if (!down) result.dY = -1.0
            } else if (down) {
                result.dY = 1.0
            }
            val right = isPressed(rightKey)
            if (isPressed(leftKey)) {
                if (!right) result.dX = -1.0
            } else if (right) {
                result.dX = 1.0
            }
            return result
        }

        @Deprecated(message = "polygon")
        private fun List<Point>.toVectors(): List<Vector> {
            if (size < 2) TODO()
            // 0  1  2  3  4
            // *--*--*--*--*
            val list = ArrayList<Vector>(size - 1)
            for (index in 1 until size) {
                list += get(index - 1) + get(index)
            }
            return list
        }
    }
}
