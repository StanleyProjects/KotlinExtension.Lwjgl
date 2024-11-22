package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.Vector
import sp.kx.math.center
import sp.kx.math.centerPoint
import sp.kx.math.copy
import sp.kx.math.measure.Measure
import sp.kx.math.minus
import sp.kx.math.offsetOf
import sp.kx.math.plus
import sp.kx.math.sizeOf
import sp.kx.math.times
import sp.kx.math.vectorOf
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Entities
import sp.service.sample.entity.Interactive
import sp.service.sample.entity.Item
import sp.service.sample.entity.Player
import sp.service.sample.util.FontInfoUtil
import kotlin.time.Duration.Companion.seconds

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
        val info = FontInfoUtil.getFontInfo(height = 0.75, measure = measure)
        for (index in items.indices) {
            val item = items[index]
            if (item.owner != null) continue
            canvas.polygons.drawRectangle(
                color = Color.Green,
                pointTopLeft = item.point,
                size = size,
                offset = offset + size.center() * -1.0,
                measure = measure,
            )
            val text = "i${index % 10}"
            val textWidth = engine.fontAgent.getTextWidth(info, text)
            canvas.texts.draw(
                color = Color.Black,
                info = info,
                pointTopLeft = item.point,
                offset = offset + offsetOf(dX = measure.units(textWidth) / 2, dY = measure.units(info.height.toDouble()) / 2) * -1.0,
                measure = measure,
                text = text,
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
        info: FontInfo,
        selected: Int?,
        title: String,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        canvas.polygons.drawRectangle(
            borderColor = Color.Green,
            fillColor = Color.Black.copy(alpha = 0.75f),
            pointTopLeft = Point.Center + offset,
            size = size,
            lineWidth = 0.1,
            measure = measure,
        )
        if (title.isNotBlank()) {
            canvas.texts.draw(
                info = info,
                pointTopLeft = Point.Center + offset + offsetOf(1, -1),
                measure = measure,
                color = if (selected == null) Color.Green else Color.Yellow,
                text = title,
            )
        }
        if (items.isEmpty()) {
            canvas.texts.draw(
                info = info,
                pointTopLeft = Point.Center + offset + offsetOf(0.75, 0.5),
                measure = measure,
                color = Color.Green.copy(alpha = 0.5f),
                text = "no items",
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
                info = info,
                pointTopLeft = Point.Center + offset + offsetOf(0.75, 0.5) + Offset.Empty.copy(dY = index * measure.units(info.height.toDouble())),
                measure = measure,
                color = color,
                text = prefix + text, // todo
            )
        }
    }

    private fun onRenderInteractive(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
        interactive: Interactive.Crate,
    ) {
        val isPressed = engine.input.keyboard.isPressed(KeyboardButton.F)
        val point = env.crates.firstOrNull { it.id == interactive.id }?.point ?: TODO()
        canvas.polygons.drawRectangle(
            borderColor = Color.Green,
            fillColor = Color.Green.copy(alpha = if (isPressed) 0.5f else 0f),
            pointTopLeft = point,
            size = sizeOf(1.0, 1.0),
            lineWidth = 0.1,
            offset = offset + offsetOf(dX = 1.0, dY = -1.5),
            measure = measure,
        )
        val info = FontInfoUtil.getFontInfo(height = 1.0, measure = measure)
        canvas.texts.draw(
            color = Color.Green,
            info = info,
            pointTopLeft = point,
            offset = offset + offsetOf(dX = 1.25, dY = -1.5),
            measure = measure,
            text = "F",
        )
    }

    private fun onRenderInteractive(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
        interactive: Interactive.Item,
    ) {
        val whenPressed = engine.input.keyboard.whenPressed(KeyboardButton.F)
        val point = env.items.firstOrNull { it.id == interactive.id }?.point ?: TODO()
        val width = if (whenPressed == null || whenPressed < interactive.time) {
            0.0
        } else {
            val max = 1.seconds
            val diff = engine.property.time.b - whenPressed
            diff.inWholeNanoseconds.toDouble() / max.inWholeNanoseconds
        }
        canvas.polygons.drawRectangle(
            color = Color.Green.copy(alpha = 0.75f),
            pointTopLeft = point,
            size = sizeOf(width = width, height = 1.0),
            offset = offset + offsetOf(dX = 1.0, dY = -1.5),
            measure = measure,
        )
        canvas.polygons.drawRectangle(
            color = Color.Green,
            pointTopLeft = point,
            size = sizeOf(1.0, 1.0),
            lineWidth = 0.1,
            offset = offset + offsetOf(dX = 1.0, dY = -1.5),
            measure = measure,
        )
        val info = FontInfoUtil.getFontInfo(height = 1.0, measure = measure)
        canvas.texts.draw(
            color = Color.Green,
            info = info,
            pointTopLeft = point,
            offset = offset + offsetOf(dX = 1.25, dY = -1.5),
            measure = measure,
            text = "F",
        )
    }

    private fun onRenderInteractive(
        canvas: Canvas,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val interactive = holder.interactive ?: return
        when (interactive) {
            is Interactive.Crate -> {
                onRenderInteractive(
                    canvas = canvas,
                    offset = offset,
                    measure = measure,
                    interactive = interactive,
                )
            }
            is Interactive.Item -> {
                onRenderInteractive(
                    canvas = canvas,
                    offset = offset,
                    measure = measure,
                    interactive = interactive,
                )
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
            info = FontInfoUtil.getFontInfo(height = 1.0, measure = measure),
            selected = state.index.takeIf { state.side },
            title = "",
            offset = offsetOf(2, 2),
            measure = measure,
        )
        onRenderItems(
            canvas = canvas,
            size = size,
            items = env.items.filter { it.owner == state.dst },
            info = FontInfoUtil.getFontInfo(height = 1.0, measure = measure),
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
            info = FontInfoUtil.getFontInfo(height = 1.0, measure = measure),
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
        val info = FontInfoUtil.getFontInfo(height = 0.75, measure = measure)
        for (index in crates.indices) {
            val crate = crates[index]
            canvas.polygons.drawRectangle(
                color = Color.Yellow,
                pointTopLeft = crate.point,
                size = size,
                offset = offset + size.center() * -1.0,
                measure = measure,
                lineWidth = 0.1,
            )
            val text = "c${index % 10}"
            val textWidth = engine.fontAgent.getTextWidth(info, text)
            canvas.texts.draw(
                color = Color.Yellow,
                info = info,
                pointTopLeft = crate.point,
                offset = offset + offsetOf(dX = measure.units(textWidth) / 2, dY = measure.units(info.height.toDouble()) / 2) * -1.0,
                measure = measure,
                text = text,
            )
        }
    }

    fun onRender(canvas: Canvas, measure: Measure<Double, Double>) {
        val centerPoint = engine.property.pictureSize.centerPoint() - measure
        val centerOffset = engine.property.pictureSize.center() - measure
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
