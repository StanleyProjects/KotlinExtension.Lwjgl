package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.colorOf
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableOffset
import sp.kx.math.MutablePoint
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.center
import sp.kx.math.copy
import sp.kx.math.measure.Measure
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.frequency
import sp.kx.math.minus
import sp.kx.math.offsetOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.sizeOf
import sp.kx.math.toOffset
import sp.kx.math.vectorOf
import sp.service.sample.util.FontInfoUtil.getFontInfo
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

internal class PolygonsEngineLogics(private val engine: Engine) : EngineLogics {
    private lateinit var shouldEngineStopUnit: Unit
    private val camera = MutableOffset(0.0, 0.0)
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.ESCAPE -> {
                    shouldEngineStopUnit = Unit
                }
                KeyboardButton.P -> {
                    if (measure.magnitude < 56.0) {
                        measure.magnitude += 8
                    }
                }
                KeyboardButton.M -> {
                    if (measure.magnitude > 16.0) {
                        measure.magnitude -= 8
                    }
                }
                KeyboardButton.DOWN, KeyboardButton.S -> {
                    camera.dY -= 2.0
                }
                KeyboardButton.UP, KeyboardButton.W -> {
                    camera.dY += 2.0
                }
                KeyboardButton.RIGHT, KeyboardButton.D -> {
                    camera.dX -= 2.0
                }
                KeyboardButton.LEFT, KeyboardButton.A -> {
                    camera.dX += 2.0
                }
                else -> {
                    println("$tag: on button: $button $isPressed")
                }
            }
        }
    }

    override fun shouldEngineStop(): Boolean {
        return ::shouldEngineStopUnit.isInitialized
    }

    private val measure = MutableDoubleMeasure(56.0)

    private fun drawRectangle(
        canvas: Canvas,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
    ) {
        val pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2)
        canvas.polygons.drawRectangle(
            color = Color.Yellow,
            pointTopLeft = pointTopLeft,
            size = size,
            lineWidth = 0.05,
            offset = offset + camera,
            measure = measure,
        )
        canvas.polygons.drawCircle(
            color = Color.Yellow,
            pointCenter = pointOfRotation,
            edgeCount = 4,
            radius = 0.1,
            offset = offset + camera,
            measure = measure,
        )
    }

    private fun onRenderBorders(
        canvas: Canvas,
        padding: Offset,
        index: Int,
        size: Size,
        tl: Point,
    ) {
        var color = 10 * index
        val offset = MutableOffset(
            dX = 0.0,
            dY = padding.dY * index,
        )
        canvas.polygons.drawRectangle(
            fillColor = colors[color++],
            borderColor = colors[color++],
            pointTopLeft = tl + offset + camera + measure,
            size = size + measure,
            lineWidth = measure.magnitude / 4,
        )
        offset.dX = padding.dX * 1
        canvas.polygons.drawRectangle(
            fillColor = colors[color++],
            borderColor = colors[color++],
            pointTopLeft = tl + measure,
            size = size + measure,
            lineWidth = measure.magnitude / 4,
            offset = offset + camera + measure,
        )
        offset.dX = padding.dX * 2
        canvas.polygons.drawRectangle(
            fillColor = colors[color++],
            borderColor = colors[color++],
            pointTopLeft = tl + offset + camera,
            size = size,
            lineWidth = 0.25,
            measure = measure,
        )
        offset.dX = padding.dX * 3
        canvas.polygons.drawRectangle(
            fillColor = colors[color++],
            borderColor = colors[color++],
            pointTopLeft = tl,
            size = size,
            lineWidth = 0.25,
            offset = offset + camera,
            measure = measure,
        )
    }

    private fun onRenderLineWidth(
        canvas: Canvas,
        padding: Offset,
        index: Int,
        size: Size,
        tl: Point,
    ) {
        var color = 10 * index
        val offset = MutableOffset(
            dX = 0.0,
            dY = padding.dY * index,
        )
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl + offset + camera + measure,
            size = size + measure,
            lineWidth = 6.0,
        )
        offset.dX = padding.dX * 1
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl + measure,
            size = size + measure,
            lineWidth = measure.magnitude / 4,
            offset = offset + camera + measure,
        )
        offset.dX = padding.dX * 2
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl + offset + camera,
            size = size,
            lineWidth = 0.25,
            measure = measure,
        )
        offset.dX = padding.dX * 3
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl,
            size = size,
            lineWidth = 0.25,
            offset = offset + camera,
            measure = measure,
        )
    }

    private fun onRenderRectangles(
        canvas: Canvas,
        padding: Offset,
        index: Int,
        size: Size,
        tl: Point,
    ) {
        var color = 10 * index
        val offset = MutableOffset(
            dX = 0.0,
            dY = padding.dY * index,
        )
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl + offset + camera + measure,
            size = size + measure,
        )
        offset.dX = padding.dX * 1
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl + measure,
            size = size + measure,
            offset = offset + camera + measure,
        )
        offset.dX = padding.dX * 2
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl + offset + camera,
            size = size,
            measure = measure,
        )
        offset.dX = padding.dX * 3
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl,
            size = size,
            offset = offset + camera,
            measure = measure,
        )
    }

    private fun onRenderDirections(
        canvas: Canvas,
        padding: Offset,
        index: Int,
        size: Size,
        tl: Point,
    ) {
        var color = 10 * index
        val offset = MutableOffset(
            dX = 0.0,
            dY = padding.dY * index,
        )
        drawRectangle(
            canvas = canvas,
            pointTopLeft = tl,
            size = size,
            offset = offset,
        )
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl + offset + camera + measure,
            size = size + measure,
            direction = kotlin.math.PI / 4,
            pointOfRotation = tl + offset + camera + size.center() + measure,
        )
        offset.dX = padding.dX * 1
        drawRectangle(
            canvas = canvas,
            pointTopLeft = tl,
            size = size,
            offset = offset,
        )
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl + measure,
            size = size + measure,
            direction = kotlin.math.PI / 4,
            pointOfRotation = tl + size.center() + measure,
            offset = offset + camera + measure,
        )
        offset.dX = padding.dX * 2
        drawRectangle(
            canvas = canvas,
            pointTopLeft = tl,
            size = size,
            offset = offset,
        )
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl + offset + camera,
            size = size,
            direction = kotlin.math.PI / 4,
            pointOfRotation = tl + offset + camera + size.center(),
            measure = measure,
        )
        offset.dX = padding.dX * 3
        drawRectangle(
            canvas = canvas,
            pointTopLeft = tl,
            size = size,
            offset = offset,
        )
        canvas.polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = tl,
            size = size,
            direction = kotlin.math.PI / 4,
            pointOfRotation = tl + size.center(),
            offset = offset + camera,
            measure = measure,
        )
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
//        canvas.texts.draw(
//            info = getFontInfo(height = 16f),
//            pointTopLeft = Point.Center,
//            color = Color.Green,
//            text = String.format("%.2f", fps),
//        )
        for(dX in 1..32) {
            canvas.vectors.draw(
                color = Color.Green.copy(alpha = 0.75f),
                vector = vectorOf(startX = dX, startY = 0, finishX = dX, finishY = 32),
                offset = camera,
                measure = measure
            )
            canvas.vectors.draw(
                color = Color.Yellow.copy(alpha = 0.75f),
                vector = vectorOf(
                    startX = dX + 0.5,
                    startY = 0.0,
                    finishX = dX + 0.5,
                    finishY = 32.0,
                ),
                offset = camera,
                measure = measure,
            )
            canvas.texts.draw(
                info = getFontInfo(height = 0.5, measure = measure),
                pointTopLeft = pointOf(x = dX, y = 0),
                color = Color.Green,
                text = "$dX",
                offset = camera.copy(dY = 0.0),
                measure = measure,
            )
        }
        for(dY in 1..32) {
            canvas.vectors.draw(
                color = Color.Green.copy(alpha = 0.75f),
                vector = vectorOf(startX = 0, startY = dY, finishX = 32, finishY = dY),
                offset = camera,
                measure = measure,
            )
            canvas.vectors.draw(
                color = Color.Yellow.copy(alpha = 0.75f),
                vector = vectorOf(
                    startX = 0.0,
                    startY = dY + 0.5,
                    finishX = 32.0,
                    finishY = dY + 0.5,
                ),
                offset = camera,
                measure = measure,
            )
            canvas.texts.draw(
                info = getFontInfo(height = 0.5, measure = measure),
                pointTopLeft = pointOf(x = 0, y = dY),
                color = Color.Green,
                text = "$dY",
                offset = camera.copy(dX = 0.0),
                measure = measure,
            )
        }
        var color = 0

        //

        val size = sizeOf(4, 4)
        val tl = pointOf(4, 4)
        val padding = offsetOf(dX = 2.0, dY = 2.0)

        //

        val offset = MutableOffset(
            dX = (size.width + padding.dX) * 0,
            dY = (size.height + padding.dY) * 0,
        )
        onRenderRectangles(
            canvas = canvas,
            padding = padding + size.toOffset(),
            index = 0,
            size = size,
            tl = tl,
        )
        onRenderLineWidth(
            canvas = canvas,
            padding = padding + size.toOffset(),
            index = 1,
            size = size,
            tl = tl,
        )
        onRenderBorders(
            canvas = canvas,
            padding = padding + size.toOffset(),
            index = 2,
            size = size,
            tl = tl,
        )
        onRenderDirections(
            canvas = canvas,
            padding = padding + size.toOffset(),
            index = 3,
            size = sizeOf(2, 4),
            tl = tl,
        )

        return
        // direction + lineWidth

        pointOf(1, 9).plus(measure).also { pointTopLeft ->
            val size = sizeOf(1, 2) + measure
            canvas.polygons.drawRectangle(
                color = colors[color++],
                pointTopLeft = pointTopLeft,
                size = size,
                direction = kotlin.math.PI / 4,
                pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2),
                lineWidth = measure.magnitude / 4,
            )
        }
        pointOf(1, 9).plus(measure).also { pointTopLeft ->
            val size = size + measure
            canvas.polygons.drawRectangle(
                color = colors[color++],
                pointTopLeft = pointTopLeft,
                size = size,
                direction = kotlin.math.PI / 4,
                pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2),
                lineWidth = measure.magnitude / 4,
                offset = offsetOf(dX = 2, dY = 0) + measure,
            )
        }
        pointOf(5, 9).also { pointTopLeft ->
            canvas.polygons.drawRectangle(
                color = colors[color++],
                pointTopLeft = pointTopLeft,
                size = size,
                direction = kotlin.math.PI / 4,
                pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2),
                lineWidth = 0.25,
                measure = measure,
            )
        }
        pointOf(1, 9).also { pointTopLeft ->
            canvas.polygons.drawRectangle(
                color = colors[color++],
                pointTopLeft = pointTopLeft,
                size = size,
                direction = kotlin.math.PI / 4,
                pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2),
                lineWidth = 0.25,
                offset = offsetOf(dX = 6, dY = 0),
                measure = measure,
            )
        }

        // circle

        canvas.polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(9, 2) + measure,
            radius = measure.transform(0.5),
            edgeCount = 32,
        )
        canvas.polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(9, 2) + measure,
            radius = measure.transform(0.5),
            edgeCount = 32,
            offset = offsetOf(dX = 2, dY = 0) + measure,
        )
        canvas.polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(13, 2),
            radius = 0.5,
            edgeCount = 32,
            measure = measure,
        )
        canvas.polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(13, 2),
            radius = 0.5,
            edgeCount = 32,
            offset = offsetOf(dX = 2, dY = 0),
            measure = measure,
        )

        // circle + lineWidth

        canvas.polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(9, 4) + measure,
            radius = measure.transform(0.5),
            edgeCount = 32,
            lineWidth = measure.magnitude / 4,
        )
        // todo

        // circle + border

        // todo
        canvas.polygons.drawCircle(
            fillColor = Color.Blue,
            borderColor = Color.Red,
            pointCenter = pointOf(13, 6),
            radius = 0.5,
            edgeCount = 32,
            lineWidth = 0.25,
            offset = offsetOf(dX = 2, dY = 0),
            measure = measure,
        )
    }

    companion object {
        private const val tag = "[Polygons]"
        private val index = AtomicInteger(0)
        private val colors = (0..128).map {
            colorOf(0xff000000L + Random.nextLong(16777215)).copy(alpha = 0.75f)
        }
    }
}
