package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.colorOf
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableOffset
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.angleOf
import sp.kx.math.centerPoint
import sp.kx.math.copy
import sp.kx.math.div
import sp.kx.math.isEmpty
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.diff
import sp.kx.math.measure.speedOf
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.kx.math.sizeOf
import sp.kx.math.times
import sp.kx.math.vectorOf
import kotlin.random.Random

internal class VectorsEngineLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                KeyboardButton.Minus -> {
                    if (measure.magnitude > step) {
                        setMagnitude(magnitude = measure.magnitude - step)
                    }
                }
                KeyboardButton.Equal -> {
                    if (measure.magnitude < step * 8.0) {
                        setMagnitude(magnitude = measure.magnitude + step)
                    }
                }
                else -> Unit
            }
        }
    }
    private val step = 8.0
    private val measure = MutableDoubleMeasure(step * 3)
    private val offset = MutableOffset(0.0, 0.0)

    private fun setMagnitude(magnitude: Double) {
        val ps = engine.property.pictureSize
        val op = ps / measure
        val dw = op.width / 2 - offset.dX
        val dh = op.height / 2 - offset.dY
        measure.magnitude = magnitude
        val np = ps / measure
        offset.set(
            dX = np.width / 2 - dw,
            dY = np.height / 2 - dh,
        )
    }

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private fun getOffset(keyboard: Keyboard): Offset {
        val offset = MutableOffset(0.0, 0.0)
        val left = keyboard.isPressed(KeyboardButton.A)
        if (keyboard.isPressed(KeyboardButton.D)) {
            if (!left) offset.dX = -1.0
        } else if (left) {
            offset.dX = 1.0
        }
        val top = keyboard.isPressed(KeyboardButton.W)
        if (keyboard.isPressed(KeyboardButton.S)) {
            if (!top) offset.dY = -1.0
        } else if (top) {
            offset.dY = 1.0
        }
        return offset
    }

    private fun onPreRender() {
        val offset = getOffset(keyboard = engine.input.keyboard)
        if (offset.isEmpty()) return
        val length = speedOf(8.0).length(engine.property.time.diff())
        val angle = angleOf(offset)
        this.offset.add(
            dX = length * kotlin.math.cos(angle),
            dY = length * kotlin.math.sin(angle),
        )
    }

    private fun onRenderOffset(canvas: Canvas, offset: Offset, pictureSize: Size) {
        for (it in 2..pictureSize.width.toInt()) {
            val dX = it - offset.dX
            val value = java.lang.Math.floor(dX).toInt()
            val x = offset.dX + value
            val color = when {
                value == 0 -> Color.Yellow.copy(alpha = 0.5f)
                value % 2 == 0 -> Color.Gray
                else -> Color.Gray.copy(alpha = 0.5f)
            }
            canvas.texts.draw(
                color = color,
                fontHeight = 0.75,
                pointTopLeft = Point.Center.copy(x = x),
                text = "$value",
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = vectorOf(x, 0.0, x, pictureSize.height) * measure,
            )
        }
        for (it in 2..pictureSize.height.toInt()) {
            val dY = it - offset.dY
            val value = java.lang.Math.floor(dY).toInt()
            val y = offset.dY + value
            val color = when {
                value == 0 -> Color.Yellow.copy(alpha = 0.5f)
                value % 2 == 0 -> Color.Gray.copy(alpha = 0.75f)
                else -> Color.Gray.copy(alpha = 0.5f)
            }
            canvas.texts.draw(
                color = color,
                fontHeight = 0.75,
                pointTopLeft = Point.Center.copy(y = y),
                text = "$value",
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = vectorOf(0.0, y, pictureSize.width, y) * measure,
            )
        }
    }

    private fun onRenderVectors(canvas: Canvas, dY: Double) {
        var color = (dY * 10).toInt()
        val padding = 2.0
        val width = 4.0
        var index = 0
        canvas.vectors.draw(
            color = colors[color++],
            vector = vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            ).plus(offset).times(measure),
        )
        index++
        canvas.vectors.draw(
            color = colors[color++],
            vector = vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            ) * measure,
            offset = offset * measure,
        )
        index++
        canvas.vectors.draw(
            color = colors[color++],
            vector = vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            ) + offset,
            measure = measure,
        )
        index++
        canvas.vectors.draw(
            color = colors[color++],
            vector = vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            ),
            offset = offset,
            measure = measure,
        )
    }

    private fun onRenderLineWidth(canvas: Canvas, dY: Double, lineWidth: Double) {
        var color = (dY * 10).toInt()
        val padding = 2.0
        val width = 4.0
        var index = 0
        canvas.vectors.draw(
            color = colors[color++],
            vector = vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            ).plus(offset).times(measure),
            lineWidth = measure.transform(lineWidth),
        )
        index++
        canvas.vectors.draw(
            color = colors[color++],
            vector = vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            ) * measure,
            offset = offset * measure,
            lineWidth = measure.transform(lineWidth),
        )
        index++
        canvas.vectors.draw(
            color = colors[color++],
            vector = vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            ) + offset,
            measure = measure,
            lineWidth = lineWidth,
        )
        index++
        canvas.vectors.draw(
            color = colors[color++],
            vector = vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            ),
            offset = offset,
            measure = measure,
            lineWidth = lineWidth,
        )
    }

    private fun onRenderListVectors(canvas: Canvas, dY: Double) {
        var color = (dY * 10).toInt()
        val padding = 2.0
        val width = 4.0
        val vectors = (0 until 4).map { index ->
            vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            )
        }
        canvas.vectors.draw(
            color = colors[color++],
            vectors = vectors,
            offset = offset,
            measure = measure,
        )
    }

    private fun onRenderLVLW(canvas: Canvas, dY: Double, lineWidth: Double) {
        var color = (dY * 10).toInt()
        val padding = 2.0
        val width = 4.0
        val vectors = (0 until 4).map { index ->
            vectorOf(
                startX = width * index + padding + padding * index,
                startY = dY,
                finishX = width + width * index + padding + padding * index,
                finishY = dY + 2.0,
            )
        }
        canvas.vectors.draw(
            color = colors[color++],
            vectors = vectors,
            offset = offset,
            measure = measure,
            lineWidth = lineWidth,
        )
    }

    override fun onRender(canvas: Canvas) {
        onPreRender()
        onRenderVectors(canvas = canvas, dY = 2.0)
        onRenderLineWidth(canvas = canvas, dY = 6.0, lineWidth = 0.5)
        onRenderLineWidth(canvas = canvas, dY = 10.0, lineWidth = 0.1)
        onRenderLineWidth(canvas = canvas, dY = 14.0, lineWidth = 0.05)
        onRenderListVectors(canvas = canvas, dY = 18.0)
        onRenderLVLW(canvas = canvas, dY = 22.0, lineWidth = 0.5)
        onRenderLVLW(canvas = canvas, dY = 26.0, lineWidth = 0.1)
        onRenderLVLW(canvas = canvas, dY = 30.0, lineWidth = 0.05)
        val pictureSize = engine.property.pictureSize
        onRenderOffset(canvas = canvas, offset = offset, pictureSize = pictureSize)
        //
        canvas.polygons.drawCircle(
            color = Color.Red,
            pointCenter = Point.Center,
            radius = 0.25,
            edgeCount = 4,
            offset = offset,
            measure = measure,
        )
        canvas.polygons.drawCircle(
            color = Color.Yellow,
            pointCenter = pictureSize.div(measure).centerPoint(),
            radius = 0.25,
            edgeCount = 4,
            measure = measure,
        )
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", measure.magnitude),
            pointTopLeft = pointOf(x = fontHeight * 2, y = pictureSize.height - fontHeight * 2),
        )
    }

    companion object {
        private val colors = (0 until 512).map {
            colorOf(0xff000000L + Random.nextLong(16777215)).copy(alpha = 0.85f)
        }
    }
}
