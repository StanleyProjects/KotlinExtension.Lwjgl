package sp.service.sample.logic

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.colorOf
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.frequency
import sp.kx.math.offsetOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.sizeOf
import sp.kx.math.vectorOf
import sp.service.sample.util.FontInfoUtil.getFontInfo
import kotlin.random.Random

internal class PolygonsEngineLogics(private val engine: Engine) : EngineLogics {
    private lateinit var shouldEngineStopUnit: Unit

    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            when (button) {
                KeyboardButton.ESCAPE -> {
                    if (!isPressed) {
                        shouldEngineStopUnit = Unit
                    }
                }
                KeyboardButton.P -> {
                    if (!isPressed) {
                        when (measure.magnitude) {
                            16.0 -> measure.magnitude = 24.0
                            24.0 -> measure.magnitude = 32.0
                            32.0 -> measure.magnitude = 40.0
                            else -> measure.magnitude = 16.0
                        }
                    }
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

    private val measure = MutableDoubleMeasure(32.0)

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
//        canvas.texts.draw(
//            info = getFontInfo(height = 16f),
//            pointTopLeft = Point.Center,
//            color = Color.Green,
//            text = String.format("%.2f", fps),
//        )
        val vectors = canvas.vectors
        val texts = canvas.texts
        for(dX in 1..24) {
            val x = measure.magnitude * dX
            vectors.draw(
                color = Color.Green.copy(alpha = 0.75f),
                vector = vectorOf(startX = x, startY = 0.0, finishX = x, finishY = engine.property.pictureSize.height),
            )
            vectors.draw(
                color = Color.Yellow.copy(alpha = 0.75f),
                vector = vectorOf(
                    startX = x + measure.magnitude / 2,
                    startY = 0.0,
                    finishX = x + measure.magnitude / 2,
                    finishY = engine.property.pictureSize.height,
                ),
            )
            texts.draw(
                info = getFontInfo(height = 0.5, measure = measure),
                pointTopLeft = pointOf(x = dX, y = 0),
                color = Color.Green,
                text = "$dX",
                measure = measure,
            )
        }
        for(dY in 1..24) {
            val y = measure.magnitude * dY
            vectors.draw(
                color = Color.Green.copy(alpha = 0.75f),
                vector = vectorOf(startX = 0.0, startY = y, finishX = engine.property.pictureSize.width, finishY = y),
            )
            vectors.draw(
                color = Color.Yellow.copy(alpha = 0.75f),
                vector = vectorOf(
                    startX = 0.0,
                    startY = y + measure.magnitude / 2,
                    finishX = engine.property.pictureSize.width,
                    finishY = y + measure.magnitude / 2,
                ),
            )
            texts.draw(
                info = getFontInfo(height = 0.5, measure = measure),
                pointTopLeft = pointOf(x = 0, y = dY),
                color = Color.Green,
                text = "$dY",
                measure = measure,
            )
        }
        val polygons = canvas.polygons
        var color = 0

        //

        polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = pointOf(1, 1) + measure,
            size = sizeOf(1, 1) + measure,
        )
        polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = pointOf(1, 1) + measure,
            size = sizeOf(1, 1) + measure,
            offset = offsetOf(dX = 2, dY = 0) + measure,
        )
        polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = pointOf(5, 1),
            size = sizeOf(1, 1),
            measure = measure,
        )
        polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = pointOf(1, 1),
            size = sizeOf(1, 1),
            offset = offsetOf(dX = 6, dY = 0),
            measure = measure,
        )

        // lineWidth

        polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = pointOf(1, 3) + measure,
            size = sizeOf(1, 1) + measure,
            lineWidth = 6.0,
        )
        polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = pointOf(1, 3) + measure,
            size = sizeOf(1, 1) + measure,
            lineWidth = measure.magnitude / 4,
            offset = offsetOf(dX = 2, dY = 0) + measure,
        )
        polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = pointOf(5, 3),
            size = sizeOf(1, 1),
            lineWidth = 0.25,
            measure = measure,
        )
        polygons.drawRectangle(
            color = colors[color++],
            pointTopLeft = pointOf(1, 3),
            size = sizeOf(1, 1),
            lineWidth = 0.25,
            offset = offsetOf(dX = 6, dY = 0),
            measure = measure,
        )

        // border

        polygons.drawRectangle(
            fillColor = colors[color++],
            borderColor = colors[color++],
            pointTopLeft = pointOf(1, 5) + measure,
            size = sizeOf(1, 1) + measure,
            lineWidth = measure.magnitude / 4,
        )
        polygons.drawRectangle(
            fillColor = colors[color++],
            borderColor = colors[color++],
            pointTopLeft = pointOf(1, 5) + measure,
            size = sizeOf(1, 1) + measure,
            lineWidth = measure.magnitude / 4,
            offset = offsetOf(dX = 2, dY = 0) + measure,
        )
        polygons.drawRectangle(
            fillColor = colors[color++],
            borderColor = colors[color++],
            pointTopLeft = pointOf(5, 5),
            size = sizeOf(1, 1),
            lineWidth = 0.25,
            measure = measure,
        )
        polygons.drawRectangle(
            fillColor = colors[color++],
            borderColor = colors[color++],
            pointTopLeft = pointOf(1, 5),
            size = sizeOf(1, 1),
            lineWidth = 0.25,
            offset = offsetOf(dX = 6, dY = 0),
            measure = measure,
        )

        // direction

        pointOf(1, 7).plus(measure).also { pointTopLeft ->
            val size = sizeOf(1, 2) + measure
            polygons.drawRectangle(
                color = colors[color++],
                pointTopLeft = pointTopLeft,
                size = size,
                direction = kotlin.math.PI / 4,
                pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2),
            )
        }
        pointOf(1, 7).plus(measure).also { pointTopLeft ->
            val size = sizeOf(1, 1) + measure
            polygons.drawRectangle(
                color = colors[color++],
                pointTopLeft = pointTopLeft,
                size = size,
                direction = kotlin.math.PI / 4,
                pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2),
                offset = offsetOf(dX = 2, dY = 0) + measure,
            )
        }
        pointOf(5, 7).also { pointTopLeft ->
            val size = sizeOf(1, 1)
            polygons.drawRectangle(
                color = colors[color++],
                pointTopLeft = pointTopLeft,
                size = size,
                direction = kotlin.math.PI / 4,
                pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2),
                measure = measure,
            )
        }
        pointOf(1, 7).also { pointTopLeft ->
            val size = sizeOf(1, 1)
            polygons.drawRectangle(
                color = colors[color++],
                pointTopLeft = pointTopLeft,
                size = size,
                direction = kotlin.math.PI / 4,
                pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2),
                offset = offsetOf(dX = 6, dY = 0),
                measure = measure,
            )
        }

        // direction + lineWidth

        pointOf(1, 9).plus(measure).also { pointTopLeft ->
            val size = sizeOf(1, 2) + measure
            polygons.drawRectangle(
                color = colors[color++],
                pointTopLeft = pointTopLeft,
                size = size,
                direction = kotlin.math.PI / 4,
                pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2),
                lineWidth = measure.magnitude / 4,
            )
        }
        pointOf(1, 9).plus(measure).also { pointTopLeft ->
            val size = sizeOf(1, 1) + measure
            polygons.drawRectangle(
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
            val size = sizeOf(1, 1)
            polygons.drawRectangle(
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
            val size = sizeOf(1, 1)
            polygons.drawRectangle(
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

        polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(9, 2) + measure,
            radius = measure.transform(0.5),
            edgeCount = 32,
        )
        polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(9, 2) + measure,
            radius = measure.transform(0.5),
            edgeCount = 32,
            offset = offsetOf(dX = 2, dY = 0) + measure,
        )
        polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(13, 2),
            radius = 0.5,
            edgeCount = 32,
            measure = measure,
        )
        polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(13, 2),
            radius = 0.5,
            edgeCount = 32,
            offset = offsetOf(dX = 2, dY = 0),
            measure = measure,
        )

        // circle + lineWidth

        polygons.drawCircle(
            color = colors[color++],
            pointCenter = pointOf(9, 4) + measure,
            radius = measure.transform(0.5),
            edgeCount = 32,
            lineWidth = measure.magnitude / 4,
        )
        // todo

        // circle + border

        // todo
        polygons.drawCircle(
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
        private val colors = (0..128).map {
            colorOf(0xff000000L + Random.nextLong(16777215))
        }
    }
}
