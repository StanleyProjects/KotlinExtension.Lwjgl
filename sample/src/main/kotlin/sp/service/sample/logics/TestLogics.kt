package sp.service.sample.logics

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.MutableDoubleMeasure
import sp.kx.math.MutableOffset
import sp.kx.math.MutableVertex
import sp.kx.math.Offset
import sp.kx.math.Vertex
import sp.kx.math.diff
import sp.kx.math.frequency
import sp.service.sample.MutableRotation
import sp.service.sample.angleOf
import sp.service.sample.center
import sp.service.sample.div
import sp.service.sample.isEmpty
import sp.service.sample.length
import sp.service.sample.mut
import sp.service.sample.plus
import sp.service.sample.rotated
import sp.service.sample.rotatedX
import sp.service.sample.rotatedY
import sp.service.sample.rotatedZ
import sp.service.sample.times
import java.util.concurrent.TimeUnit

internal class TestLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                KeyboardButton.C -> {
                    val ps = engine.property.pictureSize
                    val psu = ps / measure
                    offset.dX = psu.width / 2
                    offset.dY = psu.height / 2
                    offset.dZ = 0.0
                    rotation.aX = 0.0
                    rotation.aY = 0.0
                    rotation.aZ = 0.0
                }
                else -> Unit
            }
        }
    }
    private val measure = MutableDoubleMeasure(24.0)
    private val offset = engine.property.pictureSize.div(measure).center(dZ = 0.0).mut()
    private val rotation = MutableRotation(0.0, 0.0, 0.0)

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private fun getOffset(keyboard: Keyboard): Offset {
        val offset = MutableOffset(dX = 0.0, dY = 0.0, dZ = 0.0)
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
        val diff = engine.property.time.diff()
        val offset = getOffset(keyboard = engine.input.keyboard)
        if (!offset.isEmpty()) {
            val length = length(8.0, TimeUnit.SECONDS, diff)
            val radians = angleOf(x = offset.dX, y = offset.dY)
            this.offset.dX -= length * kotlin.math.cos(radians)
            this.offset.dY -= length * kotlin.math.sin(radians)
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Up)) {
            if (rotation.aX < kotlin.math.PI / 4) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aX = kotlin.math.min(kotlin.math.PI / 4, rotation.aX + radians)
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Down)) {
            if (rotation.aX > - kotlin.math.PI / 4) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aX = kotlin.math.max(- kotlin.math.PI / 4, rotation.aX - radians)
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Left)) {
            if (rotation.aY < kotlin.math.PI / 4) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aY = kotlin.math.min(kotlin.math.PI / 4, rotation.aY + radians)
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Right)) {
            if (rotation.aY > - kotlin.math.PI / 4) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aY = kotlin.math.max(- kotlin.math.PI / 4, rotation.aY - radians)
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Z)) {
            if (rotation.aZ < kotlin.math.PI / 4) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aZ = kotlin.math.min(kotlin.math.PI / 4, rotation.aZ + radians)
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.X)) {
            if (rotation.aZ > - kotlin.math.PI / 4) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aZ = kotlin.math.max(- kotlin.math.PI / 4, rotation.aZ - radians)
            }
        }
    }

    private val v00: Vertex = MutableVertex(-4.0, -4.0, 0.0)
    private val v01: Vertex = MutableVertex(4.0, -4.0, 0.0)
    private val v10: Vertex = MutableVertex(-4.0, 4.0, 0.0)
    private val v11: Vertex = MutableVertex(4.0, 4.0, 0.0)

    private fun onRenderVertex(
        canvas: Canvas,
        vertex: Vertex,
        color: Color,
        text: CharSequence,
    ) {
//        val x = measure.transform(vertex.x + offset.dX)
//        val y = measure.transform(vertex.y + offset.dY)
//        val z = measure.transform(vertex.z + offset.dZ)
        val x1 = vertex.x// + offset.dX
        val y1 = vertex.y// + offset.dY
        val z1 = vertex.z// + offset.dZ
        //
//        val c = kotlin.math.cos(aX)
//        val s = kotlin.math.sin(aX)
//        val x2 = x1
//        val y2 = y1 * c - z1 * s
//        val z2 = y1 * s + z1 * c
        //
//        val c = kotlin.math.cos(aY)
//        val s = kotlin.math.sin(aY)
//        val x2 = x1 * c - z1 * s
//        val y2 = y1
//        val z2 = x1 * s + z1 * c
        //
//        val c = kotlin.math.cos(aZ)
//        val s = kotlin.math.sin(aZ)
//        val x2 = x1 * c - y1 * s
//        val y2 = x1 * s + y1 * c
//        val z2 = z1
        //
//        val x3 = measure.transform(x2 + offset.dX)
//        val y3 = measure.transform(y2 + offset.dY)
//        val z3 = measure.transform(z2 + offset.dZ)
        /*
        val radius = measure.transform(0.25)
        canvas.polygons.drawCircle(
            color = color,
            center = vertex
//                .rotatedX(rotation.aX)
//                .rotatedY(rotation.aY)
//                .rotatedZ(rotation.aZ)
                .rotated(rotation)
                .plus(offset)
                .times(measure),
            radius = radius,
            edgeCount = 4,
//            offset = offset,
//            measure = measure,
        )
        */
        canvas.polygons.drawCircle(
            color = color,
            center = vertex,
            radius = 2.0,
            edgeCount = 16,
            aX = rotation.aX,
            aY = rotation.aY,
            aZ = rotation.aZ,
            offset = offset,
            measure = measure,
        )
//        canvas.texts.draw(
//            color = color,
//            fontHeight = 1.0,
//            text = text,
//            topLeft = vertex,
//            offset = offset,
//            measure = measure,
//        )
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        val psu = ps / measure
        onPreRender()
        //
        onRenderVertex(
            canvas = canvas,
            vertex = v00,
            color = Color.Red,
            text = "00",
        )
        onRenderVertex(
            canvas = canvas,
            vertex = v01,
            color = Color.Green,
            text = "01",
        )
        onRenderVertex(
            canvas = canvas,
            vertex = v10,
            color = Color.Blue,
            text = "10",
        )
        onRenderVertex(
            canvas = canvas,
            vertex = v11,
            color = Color.Yellow,
            text = "11",
        )
//        canvas.polygons.drawCircle(
//            color = Color.Yellow,
//            center = MutableVertex(0.0, 0.0, 0.0),
//            radius = 0.25,
//            edgeCount = 4,
//            offset = psu.center(dZ = 0.0),
//            measure = measure,
//        )
        canvas.vectors.draw(
            color = Color.Gray,
            start = MutableVertex(x = 0.0, y = ps.height / 2, z = 0.0),
            finish = MutableVertex(x = ps.width, y = ps.height / 2, z = 0.0),
        )
        canvas.vectors.draw(
            color = Color.Gray,
            start = MutableVertex(x = ps.width / 2, y = 0.0, z = 0.0),
            finish = MutableVertex(x = ps.width / 2, y = ps.height, z = 0.0),
        )
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            topLeft = MutableVertex(x = ps.width - 96.0, y = ps.height - fontHeight * 2, z = 0.0),
        )
        listOf(
            String.format("dX: %+6.2f", offset.dX - psu.width / 2),
            String.format("dY: %+6.2f", offset.dY - psu.height / 2),
            String.format("aX: %+6.2f", rotation.aX),
            String.format("aY: %+6.2f", rotation.aY),
            String.format("aZ: %+6.2f", rotation.aZ),
        ).forEachIndexed { index, text ->
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = fontHeight,
                text = text,
                topLeft = MutableVertex(x = fontHeight, y = fontHeight * (1 + index), z = 0.0),
            )
        }
    }
}
