package sp.service.sample.logics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import sp.kx.calculations.algebra.Matrix
import sp.kx.calculations.algebra.MutableMatrix
import sp.kx.calculations.algebra.copy
import sp.kx.calculations.algebra.identity
import sp.kx.calculations.algebra.scale
import sp.kx.calculations.comparisons.isEmpty
import sp.kx.calculations.geometry.MutableOffset
import sp.kx.calculations.geometry.MutableRotation
import sp.kx.calculations.geometry.MutableVertex
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Vertex
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
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.opengl.GLUtil
import sp.service.sample.angleOf
import sp.service.sample.clear
import sp.service.sample.length
import sp.service.sample.ortho
import sp.service.sample.translate
import sp.service.sample.transpose
import sp.service.sample.transposed
import java.nio.DoubleBuffer
import java.util.concurrent.TimeUnit

internal class OrthoLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                KeyboardButton.C -> {
                    camera.set(0.0, 0.0, 0.0)
                    rotation.clear()
                }
                else -> Unit
            }
        }
    }

    private var scale = 24.0
    private val camera = MutableVertex(0.0, 0.0, 0.0)
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
        //
        if (!engine.input.keyboard.isPressed(KeyboardButton.Shift)) {
            val offset = getOffset(keyboard = engine.input.keyboard)
            if (!offset.isEmpty()) {
                val length = length(8.0, TimeUnit.SECONDS, diff)
                val radians = angleOf(x = offset.dX, y = offset.dY)
                camera.x -= length * kotlin.math.cos(radians)
                camera.y -= length * kotlin.math.sin(radians)
            }
        }
        //
        val angle = 1.0
        if (engine.input.keyboard.isPressed(KeyboardButton.Up)) {
            rotation.aX += length(angle, TimeUnit.SECONDS, diff)
            if (rotation.aX > kotlin.math.PI * 2) {
                rotation.aX -= kotlin.math.PI * 2
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Down)) {
            rotation.aX -= length(angle, TimeUnit.SECONDS, diff)
            if (rotation.aX < -kotlin.math.PI * 2) {
                rotation.aX += kotlin.math.PI * 2
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Left)) {
            rotation.aY += length(angle, TimeUnit.SECONDS, diff)
            if (rotation.aY > kotlin.math.PI * 2) {
                rotation.aY -= kotlin.math.PI * 2
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Right)) {
            rotation.aY -= length(angle, TimeUnit.SECONDS, diff)
            if (rotation.aY > kotlin.math.PI * 2) {
                rotation.aY += kotlin.math.PI * 2
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Z)) {
            rotation.aZ += length(angle, TimeUnit.SECONDS, diff)
            if (rotation.aZ > kotlin.math.PI * 2) {
                rotation.aZ -= kotlin.math.PI * 2
            }
        } else if (engine.input.keyboard.isPressed(KeyboardButton.X)) {
            rotation.aZ -= length(angle, TimeUnit.SECONDS, diff)
            if (rotation.aZ > kotlin.math.PI * 2) {
                rotation.aZ += kotlin.math.PI * 2
            }
        }
    }

    private fun onRenderAxes(canvas: Canvas, axes: List<Pair<Vertex, Color>>) {
        for ((vertex, color) in axes) {
            canvas.vectors.draw(
                color = color,
                start = MutableVertex(0.0, 0.0, 0.0),
                finish = vertex,
            )
        }
    }

    private fun onRenderAxes(
        canvas: Canvas,
        axes: List<Pair<Vertex, Color>>,
        matrix: Matrix,
    ) {
        for ((vertex, color) in axes) {
            canvas.vectors.draw(
                color = color,
                start = MutableVertex(0.0, 0.0, 0.0),
                finish = vertex,
                matrix = matrix,
            )
        }
    }

    private fun onRenderAxes(canvas: Canvas) {
//        val length = 1.0
        val length = 4.0
        canvas.vectors.draw(
            color = Color.Red,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = MutableVertex(length, 0.0, 0.0),
        )
        canvas.vectors.draw(
            color = Color.Green,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = MutableVertex(0.0, length, 0.0),
        )
        canvas.vectors.draw(
            color = Color.Blue,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = MutableVertex(0.0, 0.0, length),
        )
    }

    private fun onRenderAxes(canvas: Canvas, matrix: Matrix) {
//        val length = 1.0
        val length = 4.0
        canvas.vectors.draw(
            color = Color.Red,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = MutableVertex(length, 0.0, 0.0),
            matrix = matrix,
        )
        canvas.vectors.draw(
            color = Color.Green,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = MutableVertex(0.0, length, 0.0),
            matrix = matrix,
        )
        canvas.vectors.draw(
            color = Color.Blue,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = MutableVertex(0.0, 0.0, length),
            matrix = matrix,
        )
    }

    private fun onRenderV1(canvas: Canvas) {
        val pic = engine.property.picture
        val scale = scale
        val offset = MutableOffset(
            dX = camera.x + pic.center.dX / scale,
            dY = camera.y + pic.center.dY / scale,
            dZ = camera.z,
        )
        matrix.identity()
        matrix.scale(scale)
        matrix.translate(offset)
        matrix.rxyz(rotation)
        onRenderAxes(
            canvas = canvas,
            matrix = matrix,
        )
    }

    private fun load(buffer: DoubleBuffer, matrix: Matrix) {
        buffer.put(0,  matrix.m00)
            .put(1,  matrix.m01)
            .put(2,  matrix.m02)
            .put(3,  matrix.m03)
            .put(4,  matrix.m10)
            .put(5,  matrix.m11)
            .put(6,  matrix.m12)
            .put(7,  matrix.m13)
            .put(8,  matrix.m20)
            .put(9,  matrix.m21)
            .put(10, matrix.m22)
            .put(11, matrix.m23)
            .put(12, matrix.m30)
            .put(13, matrix.m31)
            .put(14, matrix.m32)
            .put(15, matrix.m33)
    }

    fun orthoTest(
        buffer: DoubleBuffer,
        width: Double,
        height: Double,
        zNear: Double = 0.0,
        zFar: Double = 1.0,
    ) {
        val m00 = 2.0 / width
        val m11 = -2.0 / height
        val m22 = 2.0 / (zNear - zFar)
        val m30 = -1.0
        val m31 = 1.0
        val m32 = (zFar + zNear) / (zNear - zFar)
        buffer.put(0,  m00)
            .put(1,  0.0)
            .put(2,  0.0)
            .put(3,  0.0)
            .put(4,  0.0)
            .put(5,  m11)
            .put(6,  0.0)
            .put(7,  0.0)
            .put(8,  0.0)
            .put(9,  0.0)
            .put(10, m22)
            .put(11, 0.0)
            .put(12, m30)
            .put(13, m31)
            .put(14, m32)
            .put(15, 1.0)
    }

    private val buffer = BufferUtils.createDoubleBuffer(16)

    private fun onRenderV7(canvas: Canvas) {
        val pic = engine.property.picture
        val scale = scale
        val offset = MutableOffset(
            dX = camera.x + pic.center.dX / scale,
            dY = camera.y + pic.center.dY / scale,
            dZ = camera.z,
        )
        matrix.identity()
        val id = matrix.copy()
        matrix.ortho(0.0, 0.0, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
        val pm = matrix.copy()
        matrix.identity()
        matrix.scale(scale)
        matrix.translate(offset)
        matrix.rxyz(rotation)
        val mv = matrix.copy()
        matrix.identity()
        matrix *= pm
        matrix *= mv
        val axes = listOf(
            MutableVertex(4.0, 0.0, 0.0) to Color.Red,
            MutableVertex(0.0, 4.0, 0.0) to Color.Green,
            MutableVertex(0.0, 0.0, 4.0) to Color.Blue,
        )
        GLUtil.onMatrix(pm = id, mv = id) {
            for ((vertex, color) in axes) {
                canvas.vectors.draw(
                    color = color,
                    start = MutableVertex(0.0, 0.0, 0.0) * matrix,
                    finish = vertex * matrix,
                )
            }
        }
        GLUtil.onMatrix(pm = pm.transposed(), mv = id) {
            for ((vertex, color) in axes) {
                val vm = vertex * mv
                val text = String.format("%.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
                canvas.texts.draw(
                    color = color,
                    fontHeight = 24.0,
                    topLeft = vm,
                    text = text,
                )
            }
        }
    }

    private fun onRenderV6(canvas: Canvas) {
        val pic = engine.property.picture
        val scale = scale
        val offset = MutableOffset(
            dX = camera.x + pic.center.dX / scale,
            dY = camera.y + pic.center.dY / scale,
            dZ = camera.z,
        )
        matrix.identity()
        matrix.scale(scale)
        matrix.translate(offset)
        matrix.rxyz(rotation)
        val mv = matrix.copy()
        matrix.identity()
        matrix.ortho(0.0, 0.0, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
        matrix *= mv
        matrix.transpose()
        val axes = listOf(
            MutableVertex(4.0, 0.0, 0.0) to Color.Red,
            MutableVertex(0.0, 4.0, 0.0) to Color.Green,
            MutableVertex(0.0, 0.0, 4.0) to Color.Blue,
        )
        GLUtil.onMatrix(pm = matrix) {
            onRenderAxes(canvas = canvas, axes = axes)
        }
        for ((vertex, color) in axes) {
            val vm = vertex * mv
            val text = String.format("%.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
            canvas.texts.draw(
                color = color,
                fontHeight = 24.0,
                topLeft = vm,
                text = text,
            )
        }
    }

    private fun onRenderV5(canvas: Canvas) {
        val pic = engine.property.picture
        val scale = scale
        val offset = MutableOffset(
            dX = camera.x + pic.center.dX / scale,
            dY = camera.y + pic.center.dY / scale,
            dZ = camera.z,
        )
        matrix.identity()
        matrix.ortho(0.0, 0.0, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
        val pm = matrix.transposed()
        matrix.identity()
        matrix.scale(scale)
        matrix.translate(offset)
        matrix.rxyz(rotation)
        val mv = matrix.transposed()
        GLUtil.onMatrix(pm = pm, mv = mv) {
            canvas.vectors.draw(
                color = Color.Red,
                start = MutableVertex(0.0, 0.0, 0.0),
                finish = MutableVertex(1.0, 0.0, 0.0),
            )
        }
    }

    private fun onRenderV4(canvas: Canvas) {
        val pic = engine.property.picture
        val scale = scale
        val offset = MutableOffset(
            dX = camera.x + pic.center.dX / scale,
            dY = camera.y + pic.center.dY / scale,
            dZ = camera.z,
        )
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPushMatrix()
//        matrix.identity()
//        matrix.ortho(0.0, 0.0, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
//        matrix.transpose()
//        load(buffer = buffer, matrix = matrix)
//        GL11.glLoadMatrixd(buffer)
        GL11.glLoadIdentity()
        matrix.identity()
        matrix.ortho(0.0, 0.0, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
        matrix.scale(scale)
        matrix.translate(offset)
        matrix.rxyz(rotation)
        matrix.transpose()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPushMatrix()
        load(buffer = buffer, matrix = matrix)
        GL11.glLoadMatrixd(buffer)
        //
        canvas.vectors.draw(
            color = Color.Red,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = MutableVertex(1.0, 0.0, 0.0),
        )
        //
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPopMatrix()
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPopMatrix()
    }

    private fun onRenderV3(canvas: Canvas) {
        val pic = engine.property.picture
        val scale = scale
        val offset = MutableOffset(
            dX = camera.x + pic.center.dX / scale,
            dY = camera.y + pic.center.dY / scale,
            dZ = camera.z,
        )
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPushMatrix()
        matrix.identity()
        matrix.ortho(0.0, 0.0, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
        matrix.transpose()
        load(buffer = buffer, matrix = matrix)
        GL11.glLoadMatrixd(buffer)
//        GL11.glLoadIdentity()
        matrix.identity()
//        matrix.ortho(0.0, 0.0, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
        matrix.scale(scale)
        matrix.translate(offset)
        matrix.rxyz(rotation)
        matrix.transpose()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPushMatrix()
        load(buffer = buffer, matrix = matrix)
        GL11.glLoadMatrixd(buffer)
        //
        canvas.vectors.draw(
            color = Color.Red,
            start = MutableVertex(0.0, 0.0, 0.0),
            finish = MutableVertex(1.0, 0.0, 0.0),
        )
        //
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPopMatrix()
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPopMatrix()
    }

    private fun onRenderV2(canvas: Canvas) {
        val pic = engine.property.picture
        val scale = scale
        val offset = MutableOffset(
            dX = camera.x + pic.center.dX / scale,
            dY = camera.y + pic.center.dY / scale,
            dZ = camera.z,
        )
        GL11.glPushMatrix()
//        orthoTest(buffer = buffer, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
        matrix.identity()
        matrix.ortho(0.0, 0.0, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
        matrix.transpose()
        load(buffer = buffer, matrix = matrix)
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glLoadMatrixd(buffer)
        //
        canvas.vectors.draw(
            color = Color.Red,
            start = MutableVertex(0.0, 24.0, 0.0),
            finish = MutableVertex(24.0, 24.0, 0.0),
        )
        //
        GL11.glLoadIdentity()
        GL11.glPopMatrix()
    }

    private val matrix = MutableMatrix()
    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val pic = engine.property.picture
        onPreRender()
        //
//        onRenderV1(canvas = canvas)
//        onRenderV2(canvas = canvas)
//        onRenderV3(canvas = canvas)
//        onRenderV4(canvas = canvas)
//        onRenderV5(canvas = canvas)
//        onRenderV6(canvas = canvas)
        onRenderV7(canvas = canvas)
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            topLeft = MutableVertex(x = pic.size.width - 96.0, y = pic.size.height - fontHeight * 2, z = 0.0),
        )
        listOf(
            String.format("aX: %+6.2f", rotation.aX),
            String.format("aY: %+6.2f", rotation.aY),
            String.format("aZ: %+6.2f", rotation.aZ),
            String.format("cX: %+6.2f", camera.x),
            String.format("cY: %+6.2f", camera.y),
            String.format("cZ: %+6.2f", camera.z),
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
