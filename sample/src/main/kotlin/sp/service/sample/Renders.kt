package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.progress
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.Vector
import sp.kx.math.center
import sp.kx.math.centerPoint
import sp.kx.math.copy
import sp.kx.math.div
import sp.kx.math.measure.Measure
import sp.kx.math.minus
import sp.kx.math.offsetOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.sizeOf
import sp.kx.math.times
import sp.kx.math.vectorOf
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Item
import sp.service.sample.entity.Player
import kotlin.time.Duration

internal class Renders(
    private val engine: Engine,
    private val env: Environment,
    private val holder: InteractiveHolder,
) {
    private fun onRenderItems(
        canvas: Canvas,
        items: List<Item>,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val size = sizeOf(1.0, 0.75)
        val fontHeight = 0.75
        for (index in items.indices) {
            val item = items[index]
            if (item.owner != null) continue
            canvas.polygons.drawRectangle(
                color = Color.Green,
                pointTopLeft = item.point,
                size = size,
                offset = offset.plus(size = size, multiplier = -0.5),
                measure = measure,
            )
            val text = "i${index % 10}"
            val textWidth = canvas.texts.getTextUnits(fontHeight, text, measure)
            canvas.texts.draw(
                color = Color.Black,
                fontHeight = fontHeight,
                pointTopLeft = item.point,
                text = text,
                offset = offset + offsetOf(dX = textWidth / 2, dY = fontHeight / 2) * -1.0,
                measure = measure,
            )
        }
    }

    private fun onRenderPlayer(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
        player: Player,
    ) {
        canvas.polygons.drawCircle(
            borderColor = Color.Blue,
            fillColor = Color.Blue.copy(alpha = 0.5f),
            pointCenter = player.moving.point,
            radius = 1.0,
            edgeCount = 16,
            lineWidth = 0.1,
            offset = offset,
            measure = measure,
        )
        canvas.vectors.draw(
            color = Color.Yellow,
            vector = vectorOf(player.moving.point, length = 1.0, angle = player.turning.direction.expected),
            offset = offset,
            measure = measure,
            lineWidth = 0.1,
        )
        canvas.vectors.draw(
            color = Color.White,
            vector = vectorOf(player.moving.point, length = 1.0, angle = player.turning.direction.actual),
            offset = offset,
            measure = measure,
            lineWidth = 0.1,
        )
    }

    private fun onRenderWalls(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
        walls: List<Vector>,
    ) {
        canvas.vectors.draw(
            color = Color.Gray,
            vectors = walls,
            offset = offset,
            measure = measure,
            lineWidth = 0.1,
        )
    }

    private fun onRenderItems(
        canvas: Canvas,
        size: Size,
        items: List<Item>,
        fontHeight: Double,
        selected: Int?,
        title: String,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        canvas.polygons.drawRectangle(
            borderColor = Color.Green,
            fillColor = Color.Black.copy(alpha = 0.9f),
            pointTopLeft = Point.Center,
            size = size,
            lineWidth = 0.1,
            measure = measure,
            offset = offset,
        )
        if (title.isNotBlank()) {
            canvas.texts.draw(
                color = if (selected == null) Color.Green else Color.Yellow,
                fontHeight = fontHeight,
                pointTopLeft = pointOf(x = 1.0, y = -1.0),
                text = title,
                offset = offset,
                measure = measure,
            )
        }
        if (items.isEmpty()) {
            canvas.texts.draw(
                color = Color.Green.copy(alpha = 0.5f),
                fontHeight = fontHeight,
                pointTopLeft = pointOf(x = 0.75, y = 0.5),
                text = "no items",
                offset = offset,
                measure = measure,
            )
            return
        }
        for (index in items.indices) {
            val item = items[index]
            val isSelected = selected == index
            val color = if (isSelected) Color.Yellow else Color.Green
            val text = "#${env.items.indexOf(item)} ${item.id.toString().substring(0, 4)}"
            val prefix = if (isSelected) "> " else "  "
            canvas.texts.draw(
                color = color,
                fontHeight = fontHeight,
                pointTopLeft = pointOf(x = 0.75, y = 0.5 + index * fontHeight),
                text = prefix + text, // todo
                offset = offset,
                measure = measure,
            )
        }
    }

    private fun onRenderInteractiveCrate(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
        crate: Crate,
        isCurrent: Boolean,
    ) {
        val isPressed = engine.input.keyboard.isPressed(KeyboardButton.F)
        val point = crate.point
        val color = if (isCurrent) Color.Green else Color.Green.copy(alpha = 0.5f)
        canvas.polygons.drawRectangle(
            borderColor = color,
            fillColor = Color.Green.copy(alpha = if (isPressed && isCurrent) 0.5f else 0f),
            pointTopLeft = point,
            size = sizeOf(1.0, 1.0),
            lineWidth = 0.1,
            offset = offset + offsetOf(dX = 1.0, dY = -1.5),
            measure = measure,
        )
        canvas.texts.draw(
            color = color,
            fontHeight = 1.0,
            pointTopLeft = point,
            offset = offset + offsetOf(dX = 1.25, dY = -1.5),
            measure = measure,
            text = "F",
        )
    }

    private fun onRenderInteractiveItem(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
        item: Item,
        time: Duration?,
    ) {
        val point = item.point
        val color = if (time != null) Color.Green else Color.Green.copy(alpha = 0.5f)
        val size = sizeOf(1.0, 1.0) // todo Size.Reference
        val dX = 1.0
        val dY = -1.5
        if (time != null) {
            canvas.polygons.drawRectangle(
                color = Color.Green.copy(alpha = 0.75f),
                pointTopLeft = point.plus(dX = dX, dY = dY),
                size = size.copy(
                    width = 1.0 * engine.progress(
                        button = KeyboardButton.F,
                        min = time,
                    ),
                ),
                offset = offset,
                measure = measure,
            )
        }
        canvas.polygons.drawRectangle(
            color = color,
            pointTopLeft = point.plus(dX = dX, dY = dY),
            size = size,
            lineWidth = 0.1,
            offset = offset,
            measure = measure,
        )
        val fontHeight = 1.0
        val text = "F"
        val textWidth = canvas.texts.getTextUnits(fontHeight, text, measure)
        canvas.texts.draw(
            color = color,
            fontHeight = fontHeight,
            pointTopLeft = point.plus(dX = dX, dY = dY),
            text = text,
            offset = offset + offsetOf(
                dX = size.width / 2 - textWidth / 2,
                dY = size.height / 2 - fontHeight / 2,
            ),
            measure = measure,
        )
    }

    private fun onRenderInteractive(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val interactive = holder.current ?: return
        holder.map.forEach { (type, ids) ->
            when {
                type.isAssignableFrom(Item::class.java) -> {
                    ids.forEach { id ->
                        val item = env.items.firstOrNull { it.id == id } ?: TODO()
                        onRenderInteractiveItem(
                            canvas = canvas,
                            offset = offset,
                            measure = measure,
                            item = item,
                            time = interactive.getCurrentTime(type = type, id = id),
                        )
                    }
                }
                type.isAssignableFrom(Crate::class.java) -> {
                    ids.forEach { id ->
                        val crate = env.crates.firstOrNull { it.id == id } ?: TODO()
                        onRenderInteractiveCrate(
                            canvas = canvas,
                            offset = offset,
                            measure = measure,
                            crate = crate,
                            isCurrent = interactive.isCurrent(type = type, id = id),
                        )
                    }
                }
            }
        }
    }

    private fun onRenderSwap(
        canvas: Canvas,
        state: Environment.State.Swap,
        measure: Measure<Double, Double>,
    ) {
        val size = sizeOf(8, 8)
        onRenderItems(
            canvas = canvas,
            size = size,
            items = env.items.filter { it.owner == state.src },
            fontHeight = 1.0,
            selected = state.index.takeIf { state.side },
            title = "",
            offset = offsetOf(2, 2),
            measure = measure,
        )
        onRenderItems(
            canvas = canvas,
            size = size,
            items = env.items.filter { it.owner == state.dst },
            fontHeight = 1.0,
            selected = state.index.takeIf { !state.side },
            title = state.dst.toString().substring(0, 4),
            offset = offsetOf(2.0 + size.width + 2.0, 2.0),
            measure = measure,
        )
    }

    private fun onRenderInventory(
        canvas: Canvas,
        state: Environment.State.Inventory,
        measure: Measure<Double, Double>,
    ) {
        onRenderItems(
            canvas = canvas,
            size = sizeOf(8, 8),
            items = env.items.filter { it.owner == env.player.id },
            fontHeight = 1.0,
            selected = state.index,
            title = "",
            offset = offsetOf(2, 2),
            measure = measure,
        )
    }

    private fun onRenderCrates(
        canvas: Canvas,
        crates: List<Crate>,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val size = sizeOf(1.0, 1.0)
        val fontHeight = 0.75
        for (index in crates.indices) {
            val crate = crates[index]
            canvas.polygons.drawRectangle(
                color = Color.Yellow,
                pointTopLeft = crate.point,
                size = size,
                offset = offset.plus(size = size, multiplier = -0.5),
                measure = measure,
                lineWidth = 0.1,
            )
            val text = "c${index % 10}"
            val textWidth = canvas.texts.getTextUnits(fontHeight, text, measure)
            canvas.texts.draw(
                color = Color.Yellow,
                fontHeight = fontHeight,
                pointTopLeft = crate.point,
                offset = offset + offsetOf(dX = textWidth / 2, dY = fontHeight / 2) * -1.0,
                measure = measure,
                text = text,
            )
        }
    }

    fun onRender(canvas: Canvas, measure: Measure<Double, Double>) {
        val centerPoint = engine.property.pictureSize.centerPoint() / measure
        val centerOffset = engine.property.pictureSize.center() / measure
        val point = env.player.moving.point
        val offset = centerPoint - point
        //
        onRenderItems(
            canvas = canvas,
            items = env.items,
            offset = offset,
            measure = measure,
        )
        onRenderCrates(
            canvas = canvas,
            crates = env.crates,
            offset = offset,
            measure = measure,
        )
        onRenderPlayer(
            canvas = canvas,
            offset = offset,
            measure = measure,
            player = env.player,
        )
        onRenderWalls(
            canvas = canvas,
            walls = env.walls,
            offset = offset,
            measure = measure,
        )
        //
        when (val state = env.state) {
            Environment.State.Walking -> {
                onRenderInteractive(
                    canvas = canvas,
                    offset = offset,
                    measure = measure,
                )
            }
            is Environment.State.Inventory -> {
                onRenderInventory(
                    canvas = canvas,
                    state = state,
                    measure = measure,
                )
            }
            is Environment.State.Swap -> {
                onRenderSwap(
                    canvas = canvas,
                    state = state,
                    measure = measure,
                )
            }
        }
    }
}
