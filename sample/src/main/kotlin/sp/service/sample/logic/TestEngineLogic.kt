package sp.service.sample.logic

import org.json.JSONObject
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.colorOf
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableOffset
import sp.kx.math.MutablePoint
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.Vector
import sp.kx.math.angleOf
import sp.kx.math.center
import sp.kx.math.centerPoint
import sp.kx.math.copy
import sp.kx.math.dby
import sp.kx.math.distanceOf
import sp.kx.math.eq
import sp.kx.math.getPerpendicular
import sp.kx.math.getShortestDistance
import sp.kx.math.getShortestPoint
import sp.kx.math.gt
import sp.kx.math.ifNaN
import sp.kx.math.isEmpty
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
import sp.lwjgl.joysticks.JoystickButton
import sp.lwjgl.joysticks.JoysticksStorage
import sp.service.sample.entity.Barrier
import sp.service.sample.entity.Condition
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Item
import sp.service.sample.entity.ItemPosition
import sp.service.sample.entity.Relay
import sp.service.sample.util.FontInfoUtil
import sp.service.sample.util.JsonJoystickMapping
import sp.service.sample.util.ResourceUtil
import sp.service.sample.util.objects
import sp.service.sample.util.strings
import sp.service.sample.util.toBarrier
import sp.service.sample.util.toCondition
import sp.service.sample.util.toCrate
import sp.service.sample.util.toItem
import sp.service.sample.util.toItemPosition
import sp.service.sample.util.toMap
import sp.service.sample.util.toMapStrings
import sp.service.sample.util.toPoint
import sp.service.sample.util.toRelay
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

internal class TestEngineLogic(private val engine: Engine) : EngineLogic {
    class Player private constructor(
        val id: UUID,
        val point: MutablePoint,
        val speed: MutableSpeed,
        val direction: MutableDeviation<Double>,
        val directionSpeed: Speed,
    ) {
        constructor(
            id: UUID,
            point: Point,
        ) : this(
            id = id,
            point = MutablePoint(x = point.x, y = point.y),
            speed = MutableSpeed(7.5, TimeUnit.SECONDS),
            direction = MutableDeviation(0.0, 0.0),
            directionSpeed = speedOf(kotlin.math.PI * 2),
        )

        private val width = 2.0
//        private val width = 4.0 // todo
//        private val width = 6.0 // todo
        val size = sizeOf(width = width, height = width)
        val radius: Double = kotlin.math.sqrt(2.0) * size.width / 2

        fun copy(): Player {
            return Player(
                id = id,
                point = MutablePoint(x = point.x, y = point.y),
                speed = MutableSpeed(magnitude = speed.per(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS),
                direction = MutableDeviation(actual = direction.actual, expected = direction.expected),
                directionSpeed = MutableSpeed(magnitude = directionSpeed.per(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS),
            )
        }
    }

    private sealed interface PlayerState {
        data object Walking : PlayerState
        class Inventory(var index: Int = 0) : PlayerState {
            override fun toString(): String {
                return "Inventory(index: $index)"
            }
        }
        class ItemsSwap(
            var index: Int = 0,
            var issuer: Boolean = true,
            val crateId: UUID,
        ) : PlayerState {
            fun copy(index: Int, issuer: Boolean = this.issuer): ItemsSwap {
                return ItemsSwap(
                    index = index,
                    issuer = issuer,
                    crateId = crateId,
                )
            }
        }
    }

    private data class Environment(
        val player: Player,
        val conditions: List<Condition>,
        val relays: List<Relay>,
        val barriers: List<Barrier>,
        val items: List<Item>,
        val crates: List<Crate>,
        val itemsPositions: MutableList<ItemPosition>,
        val ownership: MutableMap<UUID, UUID>,
        val barriersToConditions: Map<UUID, Set<UUID>>,
        val conditionsToRelays: Map<UUID, Set<UUID>>,
    ) {
        var state: PlayerState = PlayerState.Walking
    }

//    private val measure = measureOf(16.0)
    private val measure = measureOf(24.0)
//    private val measure = measureOf(32.0)

    private val relaySize = sizeOf(2, 1)
    private val itemSize = sizeOf(1, 1)
    private val crateSize = sizeOf(1.5, 1.5)

    private fun List<Point>.toVectors(): List<Vector> {
        if (isEmpty()) return emptyList()
        if (size == 1) TODO()
        val list = mutableListOf<Vector>()
        for (index in 1 until size) {
            list += get(index - 1) + get(index)
        }
        return list
    }

    /*
    private val walls = listOf(
        pointOf(x = -13, y = 3),
        pointOf(x = -7, y = 3),
        pointOf(x = -7, y = 6),
        pointOf(x = 7, y = 6),
        pointOf(x = 7, y = 3),
        pointOf(x = 13, y = 3),
        pointOf(x = 13, y = -3),
        pointOf(x = 7, y = -3),
        pointOf(x = 7, y = -6),
        pointOf(x = -7, y = -6),
        pointOf(x = -7, y = -3),
        pointOf(x = -13, y = -3),
        pointOf(x = -13, y = 3),
    ).toVectors()
    */

    /*
    private val walls = listOf(
        pointOf(x = -12, y = 12),
        //
        pointOf(x = -10, y = 12),
        pointOf(x = -10, y = 12 + 6),
        pointOf(x = -2, y = 12 + 6),
        pointOf(x = -2, y = 12),
        //
        pointOf(x = 2, y = 12),
        pointOf(x = 2, y = 12 + 6),
        pointOf(x = 14, y = 12 + 6),
        pointOf(x = 14, y = 12),
        //
        pointOf(x = 12, y = 12),
        //
        pointOf(x = 12, y = 14),
        pointOf(x = 12 + 6, y = 14),
        pointOf(x = 12 + 6, y = 2),
        pointOf(x = 12, y = 2),
        //
        pointOf(x = 12, y = -2),
        pointOf(x = 12 + 6, y = -2),
        pointOf(x = 12 + 6, y = -10),
        pointOf(x = 12, y = -10),
        //
        pointOf(x = 12, y = -12),
        //
        pointOf(x = 14, y = -12),
        pointOf(x = 14, y = -12 - 6),
        pointOf(x = 2, y = -12 - 6),
        pointOf(x = 2, y = -12),
        //
        pointOf(x = -2, y = -12),
        pointOf(x = -2, y = -12 - 6),
        pointOf(x = -10, y = -12 - 6),
        pointOf(x = -10, y = -12),
        //
        pointOf(x = -12, y = -12),
        //
        pointOf(x = -12, y = -10),
        pointOf(x = -12 - 6, y = -10),
        pointOf(x = -12 - 6, y = -2),
        pointOf(x = -12, y = -2),
        //
        pointOf(x = -12, y = 2),
        pointOf(x = -12 - 6, y = 2),
        pointOf(x = -12 - 6, y = 14),
        pointOf(x = -12, y = 14),
        //
        pointOf(x = -12, y = 12),
    ).toVectors()
    */

    private val walls = listOf(
        pointOf(x = -1, y = 8),
        pointOf(x = 3, y = 8),
        pointOf(x = 3, y = 9),
        pointOf(x = 1, y = 9),
        pointOf(x = 1, y = 14),
        pointOf(x = 9, y = 14),
        pointOf(x = 9, y = 9),
        pointOf(x = 8, y = 9),
        pointOf(x = 8, y = 8),
        pointOf(x = 9, y = 8),
        //
        pointOf(x = 9, y = 0),
        //
        pointOf(x = 9, y = -9),
        pointOf(x = -9, y = -9),
        pointOf(x = -9, y = 5),
        pointOf(x = -10, y = 5),
        //
        pointOf(x = -10, y = 8),
        pointOf(x = -6, y = 8),
        pointOf(x = -6, y = 9),
        pointOf(x = -8, y = 9),
        pointOf(x = -8, y = 14),
        pointOf(x = 0, y = 14),
        pointOf(x = 0, y = 9),
        pointOf(x = -1, y = 9),
        pointOf(x = -1, y = 8),
    ).toVectors()

    private fun JSONObject.toEnvironment(): Environment {
        val barriersToConditions = getJSONObject("barriersToConditions").toMap(
            keys = UUID::fromString,
            values = { name, obj ->
                obj.strings(name, UUID::fromString).toSet()
            },
        )
        val conditionsToRelays = getJSONObject("conditionsToRelays").toMap(
            keys = UUID::fromString,
            values = { name, obj ->
                obj.strings(name, UUID::fromString).toSet()
            },
        )
        val player = getJSONObject("player").let {
            Player(
                id = UUID.fromString(it.getString("id")),
                point = it.getJSONObject("point").toPoint(),
            )
        }
        return Environment(
            player = player,
            conditions = objects("conditions") { it.toCondition() },
            relays = objects("relays") { it.toRelay() },
            barriers = objects("barriers") { it.toBarrier() },
            items = objects("items") { it.toItem() },
            crates = objects("crates") { it.toCrate() },
            itemsPositions = objects("itemsPositions") { it.toItemPosition() }.toMutableList(),
            ownership = getJSONObject("ownership").toMapStrings(
                keys = UUID::fromString,
                values = UUID::fromString,
            ).toMutableMap(),
            barriersToConditions = barriersToConditions,
            conditionsToRelays = conditionsToRelays,
        )
    }

    private val env = ResourceUtil.requireResourceAsStream("environment.json")
        .reader()
        .readText()
        .let(::JSONObject)
        .toEnvironment()

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
            when (button) {
                JoystickButton.A -> {
                    if (isPressed) {
                        onInteraction()
                    }
                }
                else -> {
                    // todo
                }
            }
        },
    )

    private fun Item.setOrCreatePosition(point: Point): ItemPosition {
        val position = env.itemsPositions.firstOrNull { it.itemId == id }
        if (position != null) {
            position.point = point
            return position
        }
        val newPosition = ItemPosition(
            id = UUID.randomUUID(),
            itemId = id,
            point = point,
        )
        env.itemsPositions += newPosition
        return newPosition
    }

    private fun onPressInventory(button: KeyboardButton, state: PlayerState.Inventory) {
        when (button) {
            KeyboardButton.I, KeyboardButton.ESCAPE, KeyboardButton.TAB -> {
                env.state = PlayerState.Walking
            }
            else -> {
                // noop
            }
        }
        val items = env.items.filter { item ->
            env.ownership[item.id] == env.player.id
        }
        if (items.isEmpty()) return
        when (button) {
            KeyboardButton.W, KeyboardButton.UP -> {
                env.state = PlayerState.Inventory(index = (items.size + state.index - 1) % items.size)
            }
            KeyboardButton.S, KeyboardButton.DOWN -> {
                env.state = PlayerState.Inventory(index = (state.index + 1) % items.size)
            }
            KeyboardButton.X -> {
                val item = items[state.index]
                val itemPosition = item.setOrCreatePosition(point = env.player.point.copy())
                if (state.index == items.lastIndex) {
                    env.state = PlayerState.Inventory(index = (items.size + state.index - 1) % items.size)
                }
                env.ownership[item.id] = itemPosition.id
            }
            else -> {
                // noop
            }
        }
    }

    private fun onPressWalking(button: KeyboardButton) {
        when (button) {
            KeyboardButton.ESCAPE -> {
                shouldEngineStopUnit = Unit
            }
            KeyboardButton.F -> {
                onInteraction()
            }
            KeyboardButton.I, KeyboardButton.TAB -> {
                env.state = PlayerState.Inventory()
            }
            else -> {
                // noop
            }
        }
    }

    private fun onPressItemsSwap(button: KeyboardButton, state: PlayerState.ItemsSwap) {
        when (button) {
            KeyboardButton.ESCAPE -> {
                env.state = PlayerState.Walking
            }
            else -> {
                // noop
            }
        }
        val items = env.items.filter { item ->
            env.ownership[item.id] == if (state.issuer) env.player.id else state.crateId
        }
        if (items.isNotEmpty()) {
            when (button) {
                KeyboardButton.W, KeyboardButton.UP -> {
                    env.state = state.copy(index = (items.size + state.index - 1) % items.size)
                }
                KeyboardButton.S, KeyboardButton.DOWN -> {
                    env.state = state.copy(index = (state.index + 1) % items.size)
                }
                KeyboardButton.F -> {
                    val item = items[state.index]
                    if (state.index == items.lastIndex) {
                        env.state = state.copy(index = (items.size + state.index - 1) % items.size)
                    }
                    env.ownership[item.id] = if (state.issuer) state.crateId else env.player.id
                }
                else -> {
                    // noop
                }
            }
        }
        when (button) {
            KeyboardButton.A, KeyboardButton.LEFT,
            KeyboardButton.D, KeyboardButton.RIGHT, -> {
                env.state = state.copy(index = 0, issuer = !state.issuer)
            }
            else -> {
                // noop
            }
        }
    }

    private fun onPressPlayerState(button: KeyboardButton, state: PlayerState) {
        when (state) {
            is PlayerState.Inventory -> onPressInventory(button, state)
            PlayerState.Walking -> onPressWalking(button)
            is PlayerState.ItemsSwap -> onPressItemsSwap(button, state)
        }
    }

    private fun onPress(button: KeyboardButton) {
        onPressPlayerState(button, env.state)
    }

    override val inputCallback: EngineInputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) {
                onPress(button)
            }
        }
    }

    @Deprecated("sp.kx.math")
    private fun Size.diagonal(): Double {
        return kotlin.math.sqrt(width * width + height * height)
    }

    private fun onInteraction() {
        val relay = getNearest(env.relays) { it.getPolygon(size = relaySize) }
        if (relay != null) {
            relay.toggle()
            return
        }
        val itemPosition = getNearest(env.itemsPositions.filter { pos -> env.ownership.values.any { it == pos.id } }) { it.getPolygon(size = itemSize) }
        if (itemPosition != null) {
            env.ownership[itemPosition.itemId] = env.player.id
            return
        }
        val crate = getNearest(env.crates) { it.getPolygon(size = crateSize) }
        if (crate != null) {
            env.state = PlayerState.ItemsSwap(crateId = crate.id)
            return
        }
    }

    override fun shouldEngineStop(): Boolean {
        return ::shouldEngineStopUnit.isInitialized
    }

    private fun debug(previous: Player, canvas: Canvas) {
        val info = FontInfoUtil.getFontInfo(height = 16f)
        val padding = measure.transform(1.0)
        val fps = engine.property.time.frequency()
        canvas.texts.draw(
            info = info,
            pointTopLeft = pointOf(x = 1, y = 1),
            measure = measure,
            color = Color.GREEN,
            text = fps.toString(6, 2)
        )
        val lineWidth = 0.1
        2.0.also { length ->
            val center = engine.property.pictureSize.centerPoint().let {
                pointOf(
                    x = measure.units(it.x),
                    y = measure.units(it.y),
                )
            }
            val offset = center - env.player.point
            canvas.vectors.draw(
                color = Color.GREEN,
                vector = vectorOf(startX = 0.0, startY = length, finishX = 0.0, finishY = -length),
                offset = offset,
                measure = measure,
                lineWidth = lineWidth,
            )
            canvas.vectors.draw(
                color = Color.GREEN,
                vector = vectorOf(startX = -length, startY = 0.0, finishX = length, finishY = 0.0),
                offset = offset,
                measure = measure,
                lineWidth = lineWidth,
            )
        }
        val x = padding
//        val (bi, barrier, shortest) = barriers
//            .mapIndexed { index, it -> Triple(index, it, it.getShortestDistance(player.point)) }
//            .minBy { (_, _, shortest) ->
//                shortest
//            } // todo
        val currentSpeed = speedOf(magnitude = distanceOf(previous.point, env.player.point), engine.property.time.diff())
        val values = listOf(
//            "x: ${point.x.toString(5, 1)}",
//            "y: ${point.y.toString(5, 1)}",
//            String.format("x: %+05.1f", point.x),
//            String.format("y: %+05.1f", point.y),
            String.format("x: %8s", String.format("%+.4f", env.player.point.x)),
            String.format("y: %8s", String.format("%+.4f", env.player.point.y)),
            String.format("max speed: %s/s", env.player.speed.per(TimeUnit.SECONDS).toString(points = 2)),
            String.format("cur speed: %s/s", currentSpeed.per(TimeUnit.SECONDS).toString(points = 2)),
            String.format("a: %03.2f - %05.1f", env.player.direction.actual, Math.toDegrees(env.player.direction.actual)),
            String.format("e: %03.2f - %05.1f", env.player.direction.expected, Math.toDegrees(env.player.direction.expected)),
            String.format("time: %sms", engine.property.time.diff().inWholeNanoseconds.toDouble().div(1_000_000).toString(total = 6, points = 3)),
//            String.format("direction diff: %05.1f", Math.toDegrees(player.direction.diff())),
//            String.format("whc: %02.1f", env.player.direction.diff().absoluteValue.whc().ifNaN(1.0)),
//            String.format("barrier: %s", barrier.toString()),
//            String.format("barrier: $bi] ${shortest.toString(points = 4)}"),
//            String.format("player:radius: ${player.radius.toString(points = 4)}"),
        )
        values.forEachIndexed { index, text ->
            val dY = info.height * values.size - info.height * index
            canvas.texts.draw(
                color = Color.GREEN,
                info = info,
                pointTopLeft = pointOf(x = x, y = engine.property.pictureSize.height - dY - padding),
                text = text,
            )
        }
    }

    private fun Keyboard.getPlayerOffset(): Offset {
        val result = MutableOffset(dX = 0.0, dY = 0.0)
        val up = isPressed(KeyboardButton.W) || isPressed(KeyboardButton.UP)
        val down = isPressed(KeyboardButton.S) || isPressed(KeyboardButton.DOWN)
        if (up) {
            if (!down) result.dY = -1.0
        } else if (down) {
            result.dY = 1.0
        }
        val left = isPressed(KeyboardButton.A) || isPressed(KeyboardButton.LEFT)
        val right = isPressed(KeyboardButton.D) || isPressed(KeyboardButton.RIGHT)
        if (left) {
            if (!right) result.dX = -1.0
        } else if (right) {
            result.dX = 1.0
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

    @Deprecated(message = "sp.kx.math.plus")
    private operator fun Size.plus(
        measure: Measure<Double, Double>,
    ): Size {
        return sizeOf(
            width = measure.transform(width),
            height = measure.transform(height),
        )
    }

    @Deprecated(message = "sp.kx.math.minus")
    private operator fun Size.minus(
        measure: Measure<Double, Double>,
    ): Size {
        return sizeOf(
            width = measure.units(width),
            height = measure.units(height),
        )
    }

    private fun onRenderBarriers(
        canvas: Canvas,
        offset: Offset,
        barriers: List<Barrier>,
        measure: Measure<Double, Double>,
    ) {
        val dotSize = sizeOf(width = 0.3, height = 0.3)
        val dotOffset = dotSize.center() * -1.0
        barriers.forEach { barrier ->
            val vector = barrier.vector
            if (!isPassable(barrier)) {
                canvas.vectors.draw(
                    color = Color.RED,
                    vector = vector,
                    offset = offset,
                    measure = measure,
                    lineWidth = 0.2,
                )
            }
            canvas.polygons.drawRectangle(
                color = Color.YELLOW,
                pointTopLeft = vector.start + dotOffset,
                size = dotSize,
                offset = offset,
                measure = measure,
            )
            canvas.polygons.drawRectangle(
                color = Color.YELLOW,
                pointTopLeft = vector.finish + dotOffset,
                size = dotSize,
                offset = offset,
                measure = measure,
            )
        }
    }

    private fun onRenderVectors(
        canvas: Canvas,
        color: Color,
        offset: Offset,
        vectors: List<Vector>,
        measure: Measure<Double, Double>,
    ) {
        canvas.vectors.draw(
            color = color,
            vectors = vectors,
            offset = offset,
            measure = measure,
        )
    }

    private fun onRenderInteraction(
        canvas: Canvas,
        offset: Offset,
        point: Point,
        measure: Measure<Double, Double>,
    ) {
        val info = FontInfoUtil.getFontInfo(height = 0.75, measure = measure)
        val itemOffset = offsetOf(1.5, -1.5)
        val width = 1.0
        if (joystickStorage.getJoysticks().isEmpty()) {
            val isPressed = engine.input.keyboard.isPressed(KeyboardButton.F)
            canvas.polygons.drawRectangle(
                borderColor = Color.GREEN,
                fillColor = Color.GREEN.copy(alpha = if (isPressed) 0.5f else 0f),
                pointTopLeft = point + itemOffset,
                size = sizeOf(width, width),
                lineWidth = 0.1,
                offset = offset,
                measure = measure,
            )
        } else {
            TODO()
        }
        val text = if (joystickStorage.getJoysticks().isEmpty()) "F" else "A"
        val textWidth = engine.fontAgent.getTextWidth(info, text)
        val textOffset = offsetOf(
            dX = measure.units(-textWidth / 2) + width / 2,
            dY = width / 2 - measure.units(info.height.toDouble() / 2),
        )
        canvas.texts.draw(
            color = Color.GREEN,
            info = info,
            pointTopLeft = point + itemOffset + textOffset,
            offset = offset,
            measure = measure,
            text = text,
        )
    }

    private fun Relay.getPolygon(size: Size): List<Point> {
        val pointTopLeft = point + size.center() * -1.0
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height,
        )
        return listOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
            pointTopLeft,
        )
    }

    private fun Crate.getPolygon(size: Size): List<Point> {
        val pointTopLeft = point + size.center() * -1.0
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height,
        )
        return listOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
            pointTopLeft,
        )
    }

    private fun onRenderRelays(
        canvas: Canvas,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val info = FontInfoUtil.getFontInfo(height = 1.0, measure = measure)
//        val info = FontInfoUtil.getFontInfo(height = 16f)
        val itemOffset = size.center() * -1.0
        for (relay in env.relays) {
            val point = relay.point
            val color = if (relay.enabled) Color.GREEN else Color.RED
            canvas.polygons.drawRectangle(
                color = color,
                pointTopLeft = point + itemOffset,
                size = size,
                offset = offset,
                measure = measure,
            )
            val text = if (relay.enabled) "on" else "off"
            val textWidth = engine.fontAgent.getTextWidth(info, text)
            val textOffset = offsetOf(
                dX = measure.units(-textWidth / 2),
                dY = measure.units(-info.height.toDouble() / 2),
            )
            canvas.texts.draw(
                color = Color.BLACK,
                info = info,
                pointTopLeft = point + textOffset,
                offset = offset,
                measure = measure,
                text = text,
            )
        }
    }

    private fun onRenderCrates(
        canvas: Canvas,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val itemOffset = size.center() * - 1.0
        val color = Color.YELLOW
        val info = FontInfoUtil.getFontInfo(height = 0.9, measure = measure)
        for (index in env.crates.indices) {
            val crate = env.crates[index]
            val point = crate.point
            canvas.polygons.drawRectangle(
                color = color,
                pointTopLeft = point + itemOffset,
                size = size,
                offset = offset,
                measure = measure,
                lineWidth = 0.1,
            )
            val text = "c#${index % 10}"
            val textWidth = engine.fontAgent.getTextWidth(info, text)
            val textOffset = offsetOf(
                dX = measure.units(-textWidth / 2),
                dY = measure.units(-info.height.toDouble() / 2),
            )
            canvas.texts.draw(
                color = color,
                info = info,
                pointTopLeft = point + textOffset,
                offset = offset,
                measure = measure,
                text = text,
            )
        }
    }

    private fun ItemPosition.getPolygon(size: Size): List<Point> {
        val pointTopLeft = point + size.center() * -1.0
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height,
        )
        return listOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
        )
    }

    private fun onRenderItems(
        canvas: Canvas,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val itemOffset = size.center() * - 1.0
        val info = FontInfoUtil.getFontInfo(height = 0.7, measure = measure)
        for (itemPosition in env.itemsPositions) {
            val (itemId, _) = env.ownership.entries.firstOrNull { (_, ownerId) -> ownerId == itemPosition.id } ?: continue
            val (index: Int, _) = env.items.withIndex().firstOrNull { (_, item) -> item.id == itemId } ?: TODO()
            val point = itemPosition.point
            canvas.polygons.drawRectangle(
                color = Color.YELLOW,
                pointTopLeft = point + itemOffset,
                size = size,
                offset = offset,
                measure = measure,
            )
            val text = "i#${index % 10}"
            val textWidth = engine.fontAgent.getTextWidth(info, text)
            val textOffset = offsetOf(
                dX = measure.units(-textWidth / 2),
                dY = measure.units(-info.height.toDouble() / 2),
            )
            canvas.texts.draw(
                color = Color.BLACK,
                info = info,
                pointTopLeft = point + textOffset,
                offset = offset,
                measure = measure,
                text = text,
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
        val lineWidth = 0.1
        barriers.forEachIndexed { index, barrier ->
            val color = colors[index % colors.size]
            val ab = env.player.point + barrier.start
            canvas.vectors.draw(
                color = color,
                vector = ab,
                offset = offset,
                measure = measure,
                lineWidth = lineWidth,
            )
            canvas.texts.draw(
                color = color,
                info = info,
                pointTopLeft = ab.center(),
                offset = offset,
                measure = measure,
                text = distanceOf(a = env.player.point, b = barrier.start).toString(total = 4, points = 2),
            )
            val ac = env.player.point + barrier.finish
            canvas.vectors.draw(
                color = color,
                vector = ac,
                offset = offset,
                measure = measure,
                lineWidth = lineWidth,
            )
            canvas.texts.draw(
                color = color,
                info = info,
                pointTopLeft = ac.center(),
                offset = offset,
                measure = measure,
                text = distanceOf(a = env.player.point, b = barrier.finish).toString(total = 4, points = 2),
            )
            val bc = ab.finish + ac.finish
            val perpendicular = barrier.getPerpendicular(target = env.player.point)
            val aH = env.player.point + perpendicular
            canvas.vectors.draw(
                color = color,
                vector = aH,
                offset = offset,
                measure = measure,
                lineWidth = lineWidth,
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
            val shortest = barrier.getShortestDistance(target = env.player.point)
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
        minDistance: Double,
        target: Point,
        vector: Vector,
    ): Point {
        val shortestPoint = vector.getShortestPoint(target = target)
        val angle = angleOf(a = shortestPoint, b = target)
        return shortestPoint.moved(length = minDistance, angle = angle)
    }

    private fun angleOf(p1: Point, p2: Point, p3: Point): Double {
        val a = distanceOf(p1, p3)
        val b = distanceOf(p2, p3)
        val c = distanceOf(p1, p2)
        val cosA = (a * a + c * c - b * b) / 2 * a * c
        return kotlin.math.acos(cosA)
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

    private fun <K : Any, V : Any> MutableMap<K, V>.change(
        keySupplier: () -> K?,
        valueTransform: (V) -> V,
    ) {
        val key = keySupplier() ?: return
        val value = get(key) ?: return
        put(key, valueTransform(value))
    }

    private fun <T : Any> getNearest(
        list: List<T>,
        getPolygon: (T) -> List<Point>,
    ): T? {
        return getNearest(
            target = env.player.point,
            list = list,
            minDistance = env.player.size.diagonal() / 2,
            points = 12,
            multiplier = 1.1,
            getPolygon = getPolygon,
        )
    }

    private fun <T : Any> getNearest(
        target: Point,
        list: List<T>,
        minDistance: Double,
        points: Int,
        multiplier: Double,
        getPolygon: (T) -> List<Point>,
    ): T? {
        var nearest: Pair<T, Double>? = null
        for (item in list) {
            val polygon = getPolygon(item)
            for (index in 1 until polygon.size) {
                val vector = polygon[index - 1] + polygon[index]
                val distance = vector.getShortestDistance(target)
                if (distance.gt(other = minDistance * multiplier, points = points)) continue
                if (nearest == null) {
                    nearest = item to distance
                } else if (nearest.second > distance) {
                    nearest = item to distance
                }
            }
        }
        return nearest?.first
    }

    private fun getFinalPoint(
        player: Player,
        minDistance: Double,
        target: Point,
        vectors: List<Vector>,
    ): Point? {
        val targetDistance = distanceOf(player.point, target)
        val nearest = vectors.filter { vector ->
            vector.closerThan(point = env.player.point, minDistance = targetDistance + minDistance)
        }
        val filtered = nearest.filter { vector ->
            vector.closerThan(point = target, minDistance = minDistance)
        }
        if (filtered.isEmpty()) return target
        val correctedPoints = nearest.map { vector ->
            getCorrectedPoint(
                minDistance = minDistance,
                target = target,
                vector = vector,
            )
        }
        val allowedPoints = correctedPoints.filter { point ->
            nearest.none { vector ->
                vector.closerThan(point = point, minDistance = minDistance)
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

    private fun isPassable(barrier: Barrier): Boolean {
        val conditions = env.barriersToConditions[barrier.id]
        if (conditions.isNullOrEmpty()) return false
        return conditions.all { conditionId ->
            val ids = env.conditionsToRelays[conditionId]
            if (ids.isNullOrEmpty()) TODO()
            ids.all { relayId ->
                val relay = env.relays.firstOrNull { it.id == relayId } ?: TODO()
                relay.enabled
            }
        }
    }

    private fun onRenderGrid(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val start = pointOf(
            x = measure.transform(1.0),
            y = measure.transform(1.0),
        )
        val xVector = start + pointOf(
            x = engine.property.pictureSize.width - measure.transform(1.0),
            y = measure.transform(1.0),
        )
        canvas.vectors.draw(
            color = Color.WHITE,
            vector = pointOf(
                x = measure.units(engine.property.pictureSize.width / 2),
                y = 0.0,
            ).toVector(Offset.Empty.copy(dY = 2.0)),
            lineWidth = 0.1,
            measure = measure,
        )
        canvas.vectors.draw(
            color = Color.GREEN,
            vector = xVector,
        )
        val yVector = start + pointOf(
            x = measure.transform(1.0),
            y = engine.property.pictureSize.height - measure.transform(1.0),
        )
        canvas.vectors.draw(
            color = Color.WHITE,
            vector = pointOf(
                x = 0.0,
                y = measure.units(engine.property.pictureSize.height / 2),
            ).toVector(Offset.Empty.copy(dX = 2.0)),
            lineWidth = 0.1,
            measure = measure,
        )
        canvas.vectors.draw(
            color = Color.GREEN,
            vector = yVector,
        )
        val point = env.player.point
        val info = FontInfoUtil.getFontInfo(height = 12f)
        val xLen = measure.units(engine.property.pictureSize.width).toInt() - 6
        val xNumbers = (point.x.toInt() - xLen / 2)..(point.x.toInt() + xLen / 2)
        for (x in xNumbers) {
            val textY = if (x % 2 == 0) 1.0 else 0.25
            val xOffset = offset.copy(dY = 0.0)
            canvas.texts.draw(
                color = Color.GREEN,
                info = info,
                pointTopLeft = pointOf(x = x.toDouble(), y = textY),
                offset = xOffset,
                measure = measure,
                text = String.format(" %d", x),
            )
            val lineY = if (x % 2 == 0) 1.5 else 0.5
            canvas.vectors.draw(
                color = Color.GREEN,
                vector = pointOf(x = x.toDouble(), y = 1.0) + pointOf(x = x.toDouble(), y = lineY),
                offset = xOffset,
                measure = measure,
            )
        }
        val yLen = measure.units(engine.property.pictureSize.height).toInt() - 6
        val yNumbers = (point.y.toInt() - yLen / 2)..(point.y.toInt() + yLen / 2)
        for (y in yNumbers) {
            val textX = if (y % 2 == 0) 1.0 else 1.5
            canvas.texts.draw(
                color = Color.GREEN,
                info = info,
                pointTopLeft = pointOf(x = textX, y = y.toDouble()),
                offset = offset.copy(dX = 0.0),
                measure = measure,
                text = String.format(" %d", y),
            )
            val lineX = if (y % 2 == 0) 0.5 else 1.5
            canvas.vectors.draw(
                color = Color.GREEN,
                vector = pointOf(x = 1.0, y = y.toDouble()) + pointOf(x = lineX, y = y.toDouble()),
                offset = offset.copy(dX = 0.0),
                measure = measure,
            )
        }
    }

    private fun onRenderNearest(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val nearest = getNearest(env.relays) {
            it.getPolygon(size = relaySize)
        }?.point ?: getNearest(env.itemsPositions.filter { pos -> env.ownership.values.any { it == pos.id } }) {
            it.getPolygon(size = itemSize)
        }?.point ?: getNearest(env.crates) {
            it.getPolygon(size = crateSize)
        }?.point ?: return
        onRenderInteraction(
            canvas = canvas,
            offset = offset,
            point = nearest,
            measure = measure,
        )
    }

    private fun onRenderInventory(
        canvas: Canvas,
        state: PlayerState.Inventory,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val size = engine.property.pictureSize - measure
        val padding = offsetOf(2.0, 2.0)
        val borderSize = sizeOf(
            width = size.width / 2 - padding.dX * 2,
            height = size.height - padding.dY * 4,
        )
        canvas.polygons.drawRectangle(
            borderColor = Color.GREEN,
            fillColor = Color.BLACK.copy(alpha = 0.75f),
            pointTopLeft = Point.Center + padding,
            size = borderSize,
            lineWidth = 0.1,
            measure = measure,
        )
        val textHeight = 0.75
        val info = FontInfoUtil.getFontInfo(height = textHeight, measure = measure)
        val items = env.items.filter { item ->
            env.ownership[item.id] == env.player.id
        }
        val textPadding = offsetOf(1.0, 1.0)
        if (items.isEmpty()) {
            canvas.texts.draw(
                info = info,
                pointTopLeft = Point.Center + padding + textPadding,
                measure = measure,
                color = Color.GREEN,
                text = "no items"
            )
            return
        }
        // todo joystick
        /*
        val buttonsPadding = 0.5
        val buttonSize = sizeOf(1.0, 1.0)
        val buttons = mapOf(
            "x" to "drop item",
            "i" to "close inventory",
        )
        val buttonsTopLeft = pointOf(
            x = padding.dX,
            y = padding.dY + borderSize.height,
        )
        canvas.polygons.drawRectangle(
            borderColor = Color.GREEN,
            fillColor = Color.BLACK,
            pointTopLeft = buttonsTopLeft,
            size = sizeOf(width = borderSize.width, height = buttons.size * (buttonSize.height + buttonsPadding) + buttonsPadding),
            lineWidth = 0.1,
            measure = measure,
        )
        buttons.entries.forEachIndexed { index, (key, text) ->
            val pointTopLeft = buttonsTopLeft + Offset.Empty.copy(
                dX = buttonsPadding,
                dY = buttonsPadding + (buttonSize.height + buttonsPadding) * index,
            )
            canvas.polygons.drawRectangle(
                color = Color.GREEN,
                pointTopLeft = pointTopLeft,
                size = buttonSize,
                lineWidth = 0.1,
                measure = measure,
            )
            val dY = buttonSize.height / 2 - textHeight / 2
            canvas.texts.draw(
                info = info,
                pointTopLeft = pointTopLeft + offsetOf(
                    dX = buttonSize.width / 2 - measure.units(engine.fontAgent.getTextWidth(info, key)) / 2,
                    dY = dY,
                ),
                measure = measure,
                color = Color.GREEN,
                text = key,
            )
            canvas.texts.draw(
                info = info,
                pointTopLeft = pointTopLeft + offsetOf(
                    dX = buttonSize.width + buttonsPadding,
                    dY = dY,
                ),
                measure = measure,
                color = Color.GREEN,
                text = text,
            )
        }
        */
        //
        for (index in items.indices) {
            val item = items[index]
            val color = if (state.index == index) Color.YELLOW else Color.GREEN
            val text = "#${env.items.indexOf(item)} item " + item.id.toString().substring(0, 4)
            val prefix = if (state.index == index) " > " else "   "
            canvas.texts.draw(
                info = info,
                pointTopLeft = Point.Center + padding + textPadding + Offset.Empty.copy(dY = index * textHeight),
                measure = measure,
                color = color,
                text = prefix + text, // todo
            )
        }
    }

    private fun <T : Any> onRenderItems(
        canvas: Canvas,
        borderSize: Size,
        padding: Offset,
        items: List<T>,
        toText: (T) -> String,
        selected: Int?,
        title: String = "",
        measure: Measure<Double, Double>,
    ) {
        canvas.polygons.drawRectangle(
            borderColor = Color.GREEN,
            fillColor = Color.BLACK.copy(alpha = 0.75f),
            pointTopLeft = Point.Center + padding,
            size = borderSize,
            lineWidth = 0.1,
            measure = measure,
        )
        val textHeight = 0.75
        val info = FontInfoUtil.getFontInfo(height = textHeight, measure = measure)
        val textPadding = offsetOf(1.0, 1.0)
        if (title.isNotBlank()) {
            canvas.texts.draw(
                info = info,
                pointTopLeft = Point.Center + padding + offsetOf(dX = 1.0, dY = -1.0),
                measure = measure,
                color = if (selected == null) Color.GREEN else Color.YELLOW,
                text = title,
            )
        }
        if (items.isEmpty()) {
            canvas.texts.draw(
                info = info,
                pointTopLeft = Point.Center + padding + textPadding,
                measure = measure,
                color = Color.GREEN,
                text = "no items",
            )
            return
        }
        for (index in items.indices) {
            val item = items[index]
            val isSelected = selected == index
            val color = if (isSelected) Color.YELLOW else Color.GREEN
            val text = toText(item)
            val prefix = if (isSelected) "> " else "  "
            canvas.texts.draw(
                info = info,
                pointTopLeft = Point.Center + padding + textPadding + Offset.Empty.copy(dY = index * textHeight),
                measure = measure,
                color = color,
                text = prefix + text, // todo
            )
        }
    }

    private fun onRenderItemsSwap(
        canvas: Canvas,
        state: PlayerState.ItemsSwap,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val size = engine.property.pictureSize - measure
        val padding = offsetOf(2.0, 2.0)
        val borderSize = sizeOf(
            width = 8.0,
            height = size.height - padding.dY * 4,
        )
        onRenderItems(
            canvas = canvas,
            borderSize = borderSize,
            padding = padding,
            items = env.items.filter { item ->
                env.ownership[item.id] == env.player.id
            },
            toText = { item ->
                "#${env.items.indexOf(item)} item " + item.id.toString().substring(0, 4)
            },
            selected = state.index.takeIf { state.issuer },
            measure = measure,
        )
        onRenderItems(
            canvas = canvas,
            borderSize = borderSize,
            padding = offsetOf(
                dX = padding.dX + borderSize.width + padding.dX,
                dY = padding.dY,
            ),
            items = env.items.filter { item ->
                env.ownership[item.id] == state.crateId
            },
            toText = { item ->
                "#${env.items.indexOf(item)} item " + item.id.toString().substring(0, 4)
            },
            selected = state.index.takeIf { !state.issuer },
            title = "#${env.crates.indexOfFirst { it.id == state.crateId }} crate " + state.crateId.toString().substring(0, 4),
            measure = measure,
        )
    }

    private fun onRenderPlayerState(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        when (val state = env.state) {
            PlayerState.Walking -> return
            is PlayerState.Inventory -> onRenderInventory(
                canvas = canvas,
                state = state,
                offset = offset,
                measure = measure,
            )
            is PlayerState.ItemsSwap -> onRenderItemsSwap(
                canvas = canvas,
                state = state,
                offset = offset,
                measure = measure,
            )
        }
    }

    private fun onWalking() {
        val joystick = joystickStorage.getJoysticks()[0]
        val playerOffset = if (joystick == null) {
            engine.input.keyboard.getPlayerOffset()
        } else {
            joystick.getPlayerOffset()
        }
        if (playerOffset.isEmpty()) return
        val timeDiff = engine.property.time.diff()
        env.player.direction.expected = angleOf(playerOffset).radians()
        val dirDiff = env.player.direction.diff()
        if (!dirDiff.absoluteValue.eq(0.0, points = 4)) {
            val alpha = env.player.directionSpeed.length(timeDiff)
            if (alpha > dirDiff.absoluteValue) {
                env.player.direction.commit()
            } else {
                val k = dirDiff.absoluteValue.whc().ifNaN(1.0)
                val m = dirDiff.dby()
                val actual = env.player.direction.actual + alpha * m * k
                env.player.direction.actual = actual.radians()
            }
        }
        val length = env.player.speed.length(timeDiff)
        val multiplier = kotlin.math.min(1.0, distanceOf(playerOffset))
        val target = env.player.point.moved(
            length = length * multiplier,
            angle = env.player.direction.expected,
        )
        val barriers = env.barriers.filter { barrier ->
            !isPassable(barrier)
        }.map { it.vector }
        val relays = env.relays.flatMap {
            it.getPolygon(relaySize).toVectors()
        }
        val crates = env.crates.flatMap {
            it.getPolygon(crateSize).toVectors()
        }
        val finalPoint = getFinalPoint(
            player = env.player,
            minDistance = env.player.radius,
            target = target,
            vectors = walls + barriers + relays + crates,
        ) ?: return
        env.player.point.set(finalPoint)
    }

    override fun onRender(canvas: Canvas) {
        joystickStorage.update()
        val previous = env.player.copy()
        if (env.state == PlayerState.Walking) {
            onWalking()
        }
        val center = pointOf(
            x = measure.units(engine.property.pictureSize.width / 2),
            y = measure.units(engine.property.pictureSize.height / 2),
        )
        val offset = center - env.player.point
        //
        val lineWidth = 0.1
        canvas.vectors.draw(
            color = Color.YELLOW,
            vector = vectorOf(center, length = env.player.radius, angle = env.player.direction.expected),
            measure = measure,
            lineWidth = lineWidth,
        )
        canvas.vectors.draw(
            color = Color.WHITE,
            vector = vectorOf(center, length = env.player.radius, angle = env.player.direction.actual),
            measure = measure,
            lineWidth = lineWidth,
        )
        val currentSpeed = speedOf(magnitude = distanceOf(previous.point, env.player.point), engine.property.time.diff())
        canvas.vectors.draw(
            color = Color.GREEN,
            vector = vectorOf(center, length = env.player.radius * currentSpeed.per(TimeUnit.SECONDS) / env.player.speed.per(TimeUnit.SECONDS), angle = env.player.direction.expected),
            measure = measure,
            lineWidth = lineWidth,
        )
        canvas.polygons.drawRectangle(
            color = Color.BLUE,
            pointTopLeft = center - env.player.size.center(),
            size = env.player.size,
            measure = measure,
            lineWidth = 0.1,
            pointOfRotation = center,
            direction = env.player.direction.actual,
        )
        onRenderVectors(
            canvas = canvas,
            color = Color.GRAY,
            offset = offset,
            vectors = walls,
            measure = measure,
        ) // todo
        onRenderBarriers(
            canvas = canvas,
            offset = offset,
            barriers = env.barriers,
            measure = measure,
        ) // todo
        onRenderRelays(
            canvas = canvas,
            size = relaySize,
            offset = offset,
            measure = measure,
        ) // todo
        onRenderItems(
            canvas = canvas,
            size = itemSize,
            offset = offset,
            measure = measure,
        ) // todo
        onRenderCrates(
            canvas = canvas,
            size = crateSize,
            offset = offset,
            measure = measure,
        ) // todo
        //
        onRenderPlayerState(
            canvas = canvas,
            offset = offset,
            measure = measure,
        )
        //
        if (env.state == PlayerState.Walking) {
            onRenderNearest(
                canvas = canvas,
                offset = offset,
                measure = measure,
            )
        }
        //
//        onRenderGrid(
//            canvas = canvas,
//            offset = offset,
//            measure = measure,
//        )
    }
}
