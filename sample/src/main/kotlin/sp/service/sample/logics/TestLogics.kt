package sp.service.sample.logics

import sp.kx.calculations.MutableCell
import sp.kx.calculations.MutableSize
import sp.kx.calculations.algebra.Matrix
import sp.kx.calculations.algebra.MutableMatrix
import sp.kx.calculations.algebra.copy
import sp.kx.calculations.algebra.identity
import sp.kx.calculations.algebra.scale
import sp.kx.calculations.algebra.translate
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
import sp.kx.calculations.operators.timesAssign
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
import sp.kx.lwjgl.opengl.GLUtil
import sp.service.sample.angleOf
import sp.service.sample.div
import sp.service.sample.length
import sp.service.sample.minus
import sp.service.sample.ortho
import sp.service.sample.plus
import sp.service.sample.times
import sp.service.sample.translate
import sp.service.sample.transpose
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
                    camera.dX = 0.0
                    camera.dY = 0.0
                    camera.dZ = 0.0
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
    private val camera = MutableOffset(0.0, 0.0, 0.0)
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
        val pic = engine.property.picture
        val offset = MutableOffset(
            dX = camera.dX + pic.center.dX / scale,
            dY = camera.dY + pic.center.dY / scale,
            dZ = camera.dZ,
        )
        val dw = pic.size.width / 2 / scale - offset.dX
        val dh = pic.size.height / 2 / scale - offset.dY
        scale = value
        camera.dX = pic.size.width / 2 / value - dw - pic.center.dX / value
        camera.dY = pic.size.height / 2 / value - dh - pic.center.dY / value
    }

    private var zTest = -1.0
    private fun onPreRender() {
        val diff = engine.property.time.diff()
        if (!engine.input.keyboard.isPressed(KeyboardButton.Shift)) {
            val offset = getOffset(keyboard = engine.input.keyboard)
            if (!offset.isEmpty()) {
                val length = length(8.0, TimeUnit.SECONDS, diff)
                val radians = angleOf(x = offset.dX, y = offset.dY)
                this.camera.dX -= length * kotlin.math.cos(radians)
                this.camera.dY -= length * kotlin.math.sin(radians)
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
        rotation: Rotation,
        about: Vertex,
        offset: Offset,
        scale: Double,
    ) {
        val x = - width * rows / 2
        val y = - width * columns / 2
        val vm = rxyz(MutableVertex(x, y, z), rotation, about = about)
            .plus(offset)
            .times(scale)
        val text = String.format("%.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
        canvas.texts.draw(
            color = colorOf(0xff8888ff),
            fontHeight = 16.0,
            topLeft = vm,
            text = text,
        )
        for (row in 0..rows) {
            canvas.vectors.draw(
                color = colorOf(0xff8888ff),
                start = MutableVertex(x + row * width, y, z),
                finish = MutableVertex(x + row * width, y + columns * width, z),
                rotation = rotation,
                about = about,
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
                about = about,
                offset = offset,
                scale = scale,
            )
        }
    }

    private fun onRenderGrid(
        canvas: Canvas,
        z: Double,
        rows: Int,
        columns: Int,
        width: Double,
        matrix: Matrix,
    ) {
        val x = - width * rows / 2
        val y = - width * columns / 2
        val vm = MutableVertex(x, y, z) * matrix
        val text = String.format("%.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
        canvas.texts.draw(
            color = colorOf(0xff8888ff),
            fontHeight = 16.0,
            topLeft = vm,
            text = text,
        )
        for (row in 0..rows) {
            canvas.vectors.draw(
                color = colorOf(0xff8888ff),
                start = MutableVertex(x + row * width, y, z),
                finish = MutableVertex(x + row * width, y + columns * width, z),
                matrix = matrix,
            )
        }
        for (column in 0..columns) {
            canvas.vectors.draw(
                color = Color.Gray,
                start = MutableVertex(x, y + column * width, z),
                finish = MutableVertex(x + columns * width, y + column * width, z),
                matrix = matrix,
            )
        }
    }

    private fun onRenderGrid(
        canvas: Canvas,
        z: Double,
        rows: Int,
        columns: Int,
        width: Double,
    ) {
        val x = - width * rows / 2
        val y = - width * columns / 2
        for (row in 0..rows) {
            canvas.vectors.draw(
                color = colorOf(0xff8888ff),
                start = MutableVertex(x + row * width, y, z),
                finish = MutableVertex(x + row * width, y + columns * width, z),
            )
        }
        for (column in 0..columns) {
            canvas.vectors.draw(
                color = Color.Gray,
                start = MutableVertex(x, y + column * width, z),
                finish = MutableVertex(x + columns * width, y + column * width, z),
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
        val vm = rxyz(vertex, rotation, about = about)
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

    private fun onRenderAxis(
        canvas: Canvas,
        color: Color,
        vertex: Vertex,
        prefix: CharSequence,
        matrix: Matrix,
    ) {
        canvas.vectors.draw(
            color = color,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = vertex,
            matrix = matrix,
        )
        val vm = vertex * matrix
        val text = String.format("$prefix: %.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
        canvas.texts.draw(
            color = color,
            fontHeight = 24.0,
            topLeft = vm,
            text = text,
        )
    }

    private fun onRenderAxis(
        canvas: Canvas,
        color: Color,
        vertex: Vertex,
        prefix: CharSequence,
    ) {
        canvas.vectors.draw(
            color = color,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = vertex,
        )
        val vm = vertex
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

    private fun onRenderAxes(
        canvas: Canvas,
        matrix: Matrix,
    ) {
        onRenderAxis(
            canvas = canvas,
            color = Color.Red,
            vertex = MutableVertex(4.0, 0.0, 0.0),
            prefix = "x",
            matrix = matrix,
        )
        onRenderAxis(
            canvas = canvas,
            color = Color.Green,
            vertex = MutableVertex(0.0, 4.0, 0.0),
            prefix = "y",
            matrix = matrix,
        )
        onRenderAxis(
            canvas = canvas,
            color = Color.Blue,
            vertex = MutableVertex(0.0, 0.0, 4.0),
            prefix = "z",
            matrix = matrix,
        )
    }

    private fun onRenderAxes(
        canvas: Canvas,
    ) {
        onRenderAxis(
            canvas = canvas,
            color = Color.Red,
            vertex = MutableVertex(4.0, 0.0, 0.0),
            prefix = "x",
        )
        onRenderAxis(
            canvas = canvas,
            color = Color.Green,
            vertex = MutableVertex(0.0, 4.0, 0.0),
            prefix = "y",
        )
        onRenderAxis(
            canvas = canvas,
            color = Color.Blue,
            vertex = MutableVertex(0.0, 0.0, 4.0),
            prefix = "z",
        )
    }

    private val matrix = MutableMatrix()
    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val pic = engine.property.picture
        onPreRender()
        //
        val offset = MutableOffset(
            dX = camera.dX + pic.center.dX / scale,
            dY = camera.dY + pic.center.dY / scale,
            dZ = camera.dZ,
        )
        matrix.identity()
        matrix.scale(scale)
        matrix.translate(offset)
        matrix.rxyz(rotation, MutableVertex(-camera.dX, -camera.dY, -camera.dZ))
        val mm = matrix.copy()
        matrix.identity()
        matrix.ortho(l = 0.0, t = 0.0, r = pic.size.width, b = pic.size.height, zNear = -1024.0, zFar = 1024.0)
        // https://en.wikipedia.org/wiki/Row-_and_column-major_order
        matrix.transpose()
        matrix *= mm
        matrix.transpose()
        val rows = 8
        val columns = 8
        val width = 2.0
        val axis = listOf(
            MutableVertex(4.0, 0.0, 0.0) to Color.Red,
            MutableVertex(0.0, 4.0, 0.0) to Color.Green,
            MutableVertex(0.0, 0.0, 4.0) to Color.Blue,
        )
        GLUtil.onMatrix(matrix = matrix) {
            onRenderGrid(
                canvas = canvas,
                z = -0.5,
                rows = rows,
                columns = columns,
                width = width,
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
            )
            axis.forEach { (vertex, color) ->
                canvas.vectors.draw(
                    color = color,
                    start = MutableVertex(0.0, 0.0, 0.0),
                    finish = vertex,
                )
            }
        }
        axis.forEach { (vertex, color) ->
            val vm = vertex * mm
            val text = String.format("%.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
            canvas.texts.draw(
                color = color,
                fontHeight = 24.0,
                topLeft = vm,
                text = text,
            )
        }
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            topLeft = MutableVertex(x = pic.size.width - 96.0, y = pic.size.height - fontHeight * 2, z = 0.0),
        )
    }

    private fun onRenderOld(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val pic = engine.property.picture
        val scale = scale
//        val offset = camera + pic.center / scale
        val offset = MutableOffset(
            dX = camera.dX + pic.center.dX / scale,
            dY = camera.dY + pic.center.dY / scale,
            dZ = camera.dZ,
        )
//        val offset = MutableOffset(0.0, 0.0, 0.0)
        val psu = pic.size / scale
        onPreRender()
        //
        val rows = 8
        val columns = 8
        val width = 2.0
//        val about = MutableVertex(0.0, 0.0, 0.0)
        val about = MutableVertex(-camera.dX, -camera.dY, -camera.dZ)
        matrix.identity()
        matrix.scale(scale)
        matrix.translate(offset)
        matrix.rxyz(rotation, about)
        onRenderGrid(
            canvas = canvas,
            z = -0.5,
            rows = rows,
            columns = columns,
            width = width,
//            rotation = rotation,
//            about = about,
//            offset = offset,
//            scale = scale,
            matrix = matrix,
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
//            rotation = rotation,
//            about = about,
//            offset = offset,
//            scale = scale,
            matrix = matrix,
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
//            rotation = rotation,
//            about = about,
//            offset = offset,
//            scale = scale,
            matrix = matrix,
        )
        onRenderAxes(
            canvas = canvas,
//            rotation = rotation,
//            about = about,
//            offset = offset,
//            scale = scale,
            matrix = matrix,
        )
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            topLeft = MutableVertex(x = pic.size.width - 96.0, y = pic.size.height - fontHeight * 2, z = 0.0),
        )
        listOf(
            String.format("cX: %+6.2f", camera.dX),
            String.format("cY: %+6.2f", camera.dY),
            String.format("cZ: %+6.2f", camera.dZ),
            String.format("oX: %+6.2f", offset.dX),
            String.format("oY: %+6.2f", offset.dY),
            String.format("oZ: %+6.2f", offset.dZ),
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
