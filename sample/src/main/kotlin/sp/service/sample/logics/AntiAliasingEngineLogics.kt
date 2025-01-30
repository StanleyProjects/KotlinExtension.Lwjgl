package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableOffset
import sp.kx.math.MutablePoint
import sp.kx.math.MutableVector
import sp.kx.math.angle
import sp.kx.math.angleOf
import sp.kx.math.center
import sp.kx.math.div
import sp.kx.math.eq
import sp.kx.math.gt
import sp.kx.math.length
import sp.kx.math.lt
import sp.kx.math.measure.MutableDoubleMeasure
import sp.kx.math.measure.MutableSpeed
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.isEmpty
import sp.kx.math.measure.speedOf
import sp.kx.math.moved
import sp.kx.math.pointOf
import sp.kx.math.radians
import sp.kx.math.vectorOf
import java.util.concurrent.TimeUnit

internal class AntiAliasingEngineLogics(
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
                    if (measure.magnitude < step * 16.0) {
                        setMagnitude(magnitude = measure.magnitude + step)
                    }
                }
                KeyboardButton.Z -> {
                    val value = speed.per(TimeUnit.SECONDS)
                    if (value.gt(0.0, 1)) {
                        speed.set(value - 0.2, TimeUnit.SECONDS)
                    }
                }
                KeyboardButton.X -> {
                    val value = speed.per(TimeUnit.SECONDS)
                    if (value.lt(1.0, 1)) {
                        speed.set(value + 0.2, TimeUnit.SECONDS)
                    }
                }
                KeyboardButton.C -> {
                    rotating = !rotating
                }
                KeyboardButton.V -> {
                    vector.start.set(-8.0, 0.0)
                    vector.finish.set(8.0, 0.0)
                }
                KeyboardButton.L -> {
                    lineWidth = when (lineWidth) {
                        0.05 -> 0.1
                        0.1 -> 0.25
                        0.25 -> 0.5
                        0.5 -> 1.0
                        else -> 0.05
                    }
                }
                else -> Unit
            }
        }
    }
    private val step = 8.0
    private val measure = MutableDoubleMeasure(step * 3)
    private val offset = MutableOffset(0.0, 0.0).also {
        val ps = engine.property.pictureSize / measure
        it.set(
            dX = ps.width / 2,
            dY = ps.height / 2,
        )
    }

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

    private val speed = MutableSpeed(1.0, TimeUnit.SECONDS)
    private val vector = MutableVector(MutablePoint(-8.0, 0.0), MutablePoint(8.0, 0.0)).also {
//        val angle = 0.0
        val angle = 3.11
        if (!angle.eq(0.0, 1)) {
            val center = it.center()
            val length = it.length()
            it.start.set(center.moved(length = length / 2, angle = angle))
            it.finish.set(it.start.moved(length = length, angle = angle + kotlin.math.PI))
        }
    }
    private var rotating = false
    private var lineWidth = 0.5

    private fun onPreRender() {
        if (!rotating) return
        if (speed.isEmpty()) return
        val center = vector.center()
        val length = vector.length()
        val angle = angleOf(center, vector.start) + speed.length(engine.property.time.diff())
        vector.start.set(center.moved(length = length / 2, angle = angle))
        vector.finish.set(vector.start.moved(length = length, angle = angle + kotlin.math.PI))
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val pictureSize = engine.property.pictureSize
        //
        onPreRender()
        // 3.11 (178.24)
        // 0.05 (  2.67)
        canvas.vectors.draw(
            color = Color.White,
            vector = vector,
            lineWidth = lineWidth,
            offset = offset,
            measure = measure,
        )
        canvas.polygons.drawCircle(
            color = Color.Red,
            pointCenter = vector.start,
            radius = 0.5,
            edgeCount = 4,
            offset = offset,
            measure = measure,
        )
        canvas.polygons.drawCircle(
            color = Color.Blue,
            pointCenter = vector.finish,
            radius = 0.5,
            edgeCount = 4,
            offset = offset,
            measure = measure,
        )
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = pictureSize.width - 128.0, y = pictureSize.height - fontHeight * 2),
        )
        listOf(
            String.format("lw: %6.2f", lineWidth),
            vector.angle().radians().let { angle ->
                String.format("%6.2f (%6.2f)", angle, java.lang.Math.toDegrees(angle))
            },
            String.format("s: %6.2f", speed.per(TimeUnit.SECONDS)),
            String.format("m: %6.2f", measure.magnitude),
        ).forEachIndexed { index, text ->
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = fontHeight,
                text = text,
                pointTopLeft = pointOf(x = fontHeight * 2, y = pictureSize.height - fontHeight * (index + 2)),
            )
        }
    }
}
