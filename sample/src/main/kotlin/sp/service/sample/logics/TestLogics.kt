package sp.service.sample.logics

import sp.kx.calculations.MutableCell
import sp.kx.calculations.MutableSize
import sp.kx.calculations.comparisons.isEmpty
import sp.kx.calculations.geometry.MutableOffset
import sp.kx.calculations.geometry.MutableRotation
import sp.kx.calculations.geometry.MutableVertex
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Rotation
import sp.kx.calculations.geometry.Vertex
import sp.kx.calculations.geometry.copy
import sp.kx.calculations.operators.div
import sp.kx.calculations.operators.plus
import sp.kx.calculations.operators.times
import sp.kx.calculations.physics.diff
import sp.kx.calculations.physics.frequency
import sp.kx.calculations.rotations.rxyz
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.colorOf
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.service.sample.angleOf
import sp.service.sample.length
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
                    val psu = ps / scale
                    offset.dX = psu.width / 2
                    offset.dY = psu.height / 2
                    offset.dZ = 0.0
                    rotation.aX = 0.0
                    rotation.aY = 0.0
                    rotation.aZ = 0.0
                    setScale(24.0)
                }
                KeyboardButton.A -> {
                    if (engine.input.keyboard.isPressed(KeyboardButton.Shift)) {
                        cell.x -= 1
                    }
                }
                KeyboardButton.D -> {
                    if (engine.input.keyboard.isPressed(KeyboardButton.Shift)) {
                        cell.x += 1
                    }
                }
                KeyboardButton.W -> {
                    if (engine.input.keyboard.isPressed(KeyboardButton.Shift)) {
                        cell.y -= 1
                    }
                }
                KeyboardButton.S -> {
                    if (engine.input.keyboard.isPressed(KeyboardButton.Shift)) {
                        cell.y += 1
                    }
                }
                else -> Unit
            }
        }
    }
    private var scale = 24.0
    private val offset = MutableOffset(
        dX = engine.property.pictureSize.width / 2 / scale,
        dY = engine.property.pictureSize.height / 2 / scale,
        dZ = 0.0,
    )
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

    private fun setScale(value: Double) {
        val ps = engine.property.pictureSize
        val dw = ps.width / 2 / scale - offset.dX
        val dh = ps.height / 2 / scale - offset.dY
        scale = value
        offset.dX = ps.width / 2 / value - dw
        offset.dY = ps.height / 2 / value - dh
    }

    private var zTest = -1.0
    private fun onPreRender() {
        val diff = engine.property.time.diff()
        if (!engine.input.keyboard.isPressed(KeyboardButton.Shift)) {
            val offset = getOffset(keyboard = engine.input.keyboard)
            if (!offset.isEmpty()) {
                val length = length(8.0, TimeUnit.SECONDS, diff)
                val radians = angleOf(x = offset.dX, y = offset.dY)
                this.offset.dX -= length * kotlin.math.cos(radians)
                this.offset.dY -= length * kotlin.math.sin(radians)
//            p1.x += length * kotlin.math.cos(radians)
//            p1.y += length * kotlin.math.sin(radians)
            }
        }
        val pi12 = kotlin.math.PI / 2
        val pi22 = kotlin.math.PI
        val pi32 = kotlin.math.PI / 2 * 3
        val pi14 = kotlin.math.PI / 4
        val max = pi14
//        val max = pi12
        val min = -max
        if (engine.input.keyboard.isPressed(KeyboardButton.Up)) {
            if (rotation.aX < max) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aX = kotlin.math.min(max, rotation.aX + radians)
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Down)) {
            if (rotation.aX > min) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aX = kotlin.math.max(min, rotation.aX - radians)
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Left)) {
            if (rotation.aY < max) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aY = kotlin.math.min(max, rotation.aY + radians)
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Right)) {
            if (rotation.aY > min) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aY = kotlin.math.max(min, rotation.aY - radians)
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Z)) {
            if (rotation.aZ < max) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aZ = kotlin.math.min(max, rotation.aZ + radians)
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.X)) {
            if (rotation.aZ > min) {
                val radians = length(2.0, TimeUnit.SECONDS, diff)
                rotation.aZ = kotlin.math.max(min, rotation.aZ - radians)
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Equal)) {
            if (scale < 64.0) {
                val value = length(24.0, TimeUnit.SECONDS, diff)
                setScale(kotlin.math.min(64.0, scale + value))
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Minus)) {
            if (scale > 8.0) {
                val value = length(24.0, TimeUnit.SECONDS, diff)
                setScale(kotlin.math.max(8.0, scale - value))
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.O)) {
            zTest -= length(8.0, TimeUnit.SECONDS, diff)
        } else if (engine.input.keyboard.isPressed(KeyboardButton.P)) {
            zTest += length(8.0, TimeUnit.SECONDS, diff)
        }
    }

    private val cell = MutableCell(x = 0, y = 0)

    private fun onRenderGrid(
        canvas: Canvas,
        z: Double,
        rows: Int,
        columns: Int,
        width: Double,
        offset: Offset,
        scale: Double,
        rotation: Rotation,
    ) {
        val x = - width * rows / 2
        val y = - width * columns / 2
        for (row in 0..rows) {
            canvas.vectors.draw(
                color = colorOf(0xff8888ff),
                start = MutableVertex(x + row * width, y, z),
                finish = MutableVertex(x + row * width, y + columns * width, z),
                rotation = rotation,
                about = MutableVertex(0.0, 0.0, 0.0),
                offset = offset,
                scale = scale,
            )
        }
        for (column in 0..columns) {
            canvas.vectors.draw(
                color = Color.Gray,
                start = MutableVertex(x, y + column * width, z),
                finish = MutableVertex(x + columns * width, y + column * width, z),
                rotation = rotation,
                about = MutableVertex(0.0, 0.0, 0.0),
                offset = offset,
                scale = scale,
            )
        }
    }

    private fun onRenderAxis(
        canvas: Canvas,
        color: Color,
        vertex: Vertex,
        prefix: CharSequence,
        rotation: Rotation,
        about: Vertex,
        offset: Offset,
        scale: Double,
    ) {
        canvas.vectors.draw(
            color = color,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = vertex,
            rotation = rotation,
            about = about,
            offset = offset,
            scale = scale,
        )
        val vm = rxyz(vertex, rotation)
            .plus(offset)
            .times(scale)
        val text = String.format("$prefix: %.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
        canvas.texts.draw(
            color = color,
            fontHeight = 24.0,
            topLeft = vm,
            text = text,
        )
    }

    private fun onRenderAxes(
        canvas: Canvas,
        rotation: Rotation,
        about: Vertex,
        offset: Offset,
        scale: Double,
    ) {
        onRenderAxis(
            canvas = canvas,
            color = Color.Red,
            vertex = MutableVertex(4.0, 0.0, 0.0),
            prefix = "x",
            rotation = rotation,
            about = about,
            offset = offset,
            scale = scale,
        )
        onRenderAxis(
            canvas = canvas,
            color = Color.Green,
            vertex = MutableVertex(0.0, 4.0, 0.0),
            prefix = "y",
            rotation = rotation,
            about = about,
            offset = offset,
            scale = scale,
        )
        onRenderAxis(
            canvas = canvas,
            color = Color.Blue,
            vertex = MutableVertex(0.0, 0.0, 4.0),
            prefix = "z",
            rotation = rotation,
            about = about,
            offset = offset,
            scale = scale,
        )
    }

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        val scale = scale
        val offset = offset.copy()
        val psu = ps / scale
        onPreRender()
        //
        val rows = 8
        val columns = 8
        val width = 2.0
        onRenderGrid(
            canvas = canvas,
            z = -0.5,
            rows = rows,
            columns = columns,
            width = width,
            offset = offset,
            scale = scale,
            rotation = rotation,
        )
        canvas.polygons.drawRectangle(
            color = Color.Yellow,
            topLeft = MutableVertex(
                x = (cell.x - rows / 2) * width,
                y = (cell.y - columns / 2) * width,
                z = 0.1,
            ),
            size = MutableSize(
                width = 2.0,
                height = 2.0,
            ),
            rotation = rotation,
            about = MutableVertex(0.0, 0.0, 0.0),
            offset = offset,
            scale = scale,
        )
        canvas.polygons.drawRectangle(
            color = Color.White,
            topLeft = MutableVertex(
                x = -1.0,
                y = -1.0,
                z = zTest,
            ),
            size = MutableSize(
                width = 4.0,
                height = 4.0,
            ),
            rotation = rotation,
            about = MutableVertex(0.0, 0.0, 0.0),
            offset = offset,
            scale = scale,
        )
        onRenderAxes(
            canvas = canvas,
            rotation = rotation,
            about = MutableVertex(0.0, 0.0, 0.0),
            offset = offset,
            scale = scale,
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
//            String.format("pX: %+6.2f", p1.x),
//            String.format("pY: %+6.2f", p1.y),
            String.format("aX: %+6.2f", rotation.aX),
            String.format("aY: %+6.2f", rotation.aY),
            String.format("aZ: %+6.2f", rotation.aZ),
            String.format("scale: %+6.2f", scale),
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
