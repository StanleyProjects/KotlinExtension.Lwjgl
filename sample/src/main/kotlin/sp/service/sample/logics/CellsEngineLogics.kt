package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.copy
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableOffset
import sp.kx.math.MutablePoint
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.angleOf
import sp.kx.math.centerPoint
import sp.kx.math.copy
import sp.kx.math.distanceOf
import sp.kx.math.div
import sp.kx.math.isEmpty
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.speedOf
import sp.kx.math.pointOf
import sp.kx.math.times
import sp.kx.math.vectorOf

internal class CellsEngineLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit

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
//    private val step = 10.0
    private val measure = MutableDoubleMeasure(step * 3)

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private val offset = engine.property.let {
        val ps = engine.property.pictureSize / measure
        MutableOffset(ps.width / 2, ps.height / 2)
    }
    private val p1 = MutablePoint(8.0, 0.0)

    private fun getOffset(keyboard: Keyboard): Offset {
        val offset = MutableOffset(0.0, 0.0)
        val left = keyboard.isPressed(KeyboardButton.A)
        if (keyboard.isPressed(KeyboardButton.D)) {
            if (!left) offset.dX = 1.0
        } else if (left) {
            offset.dX = -1.0
        }
        val top = keyboard.isPressed(KeyboardButton.W)
        if (keyboard.isPressed(KeyboardButton.S)) {
            if (!top) offset.dY = 1.0
        } else if (top) {
            offset.dY = -1.0
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

    private fun onRenderOffset(canvas: Canvas, offset: Offset) {
        val ps = engine.property.pictureSize / measure
        val color = Color.Gray
        for (it in 2..ps.width.toInt()) {
            val dX = it - offset.dX
            val value = java.lang.Math.floor(dX).toInt()
            val x = offset.dX + value
//            val color = if (value % 2  == 0) Color.Gray else Color.Gray.copy(alpha = 0.5f)
            canvas.texts.draw(
                color = color,
                fontHeight = 0.75,
                pointTopLeft = Point.Center.copy(x = x),
                text = "$value",
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = vectorOf(x, 0.0, x, ps.height),
                lineWidth = 0.05,
                measure = measure,
//                vector = vectorOf(x, 0.0, x, ps.height) * measure,
            )
        }
        for (it in 2..ps.height.toInt()) {
            val dY = it - offset.dY
            val value = java.lang.Math.floor(dY).toInt()
            val y = offset.dY + value
//            val color = if (value % 2  == 0) Color.Gray else Color.Gray.copy(alpha = 0.5f)
            canvas.texts.draw(
                color = color,
                fontHeight = 0.75,
                pointTopLeft = Point.Center.copy(y = y),
                text = "$value",
                measure = measure,
            )
            canvas.vectors.draw(
                color = color,
                vector = vectorOf(0.0, y, ps.width, y),
                lineWidth = 0.05,
                measure = measure,
//                vector = vectorOf(0.0, y, ps.width, y) * measure,
            )
        }
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        onPreRender()
        onRenderOffset(canvas = canvas, offset = offset)
        val length = distanceOf(Point.Center, p1)
        val angle = angleOf(Point.Center, p1) + speedOf(1.0).length(engine.property.time.diff())
        p1.set(
            x = length * kotlin.math.cos(angle),
            y = length * kotlin.math.sin(angle),
        )
        canvas.polygons.drawCircle(
            color = Color.Green,
            pointCenter = p1,
            radius = 0.25,
            edgeCount = 4,
            offset = offset,
            measure = measure,
        )
        val ps = engine.property.pictureSize
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = ps.width - 128.0, y = ps.height - fontHeight * 2),
        )
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", measure.magnitude),
            pointTopLeft = pointOf(x = fontHeight * 2, y = ps.height - fontHeight * 2),
        )
        //
//        canvas.vectors.draw(
//            color = Color.White,
//            vector = vectorOf(2.0, 0.0, 8.0, 2.0),
//            lineWidth = 0.1,
//            offset = offset,
//            measure = measure,
//        )
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
            pointCenter = ps.div(measure).centerPoint(),
            radius = 0.25,
            edgeCount = 4,
            measure = measure,
        )
    }
}
