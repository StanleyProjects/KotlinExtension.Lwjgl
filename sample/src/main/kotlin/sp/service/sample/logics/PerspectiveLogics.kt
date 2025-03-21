package sp.service.sample.logics

import org.lwjgl.opengl.GL11
import sp.kx.calculations.algebra.MutableMatrix
import sp.kx.calculations.algebra.copy
import sp.kx.calculations.algebra.identity
import sp.kx.calculations.algebra.translate
import sp.kx.calculations.comparisons.isEmpty
import sp.kx.calculations.geometry.MutableOffset
import sp.kx.calculations.geometry.MutableVertex
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.operators.times
import sp.kx.calculations.operators.timesAssign
import sp.kx.calculations.physics.diff
import sp.kx.calculations.physics.frequency
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.input.Keyboard
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.opengl.GLUtil
import sp.service.sample.angleOf
import sp.service.sample.length
import sp.service.sample.ortho
import sp.service.sample.perspective
import sp.service.sample.transpose
import sp.service.sample.transposed
import java.util.concurrent.TimeUnit

internal class PerspectiveLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                else -> Unit
            }
        }
    }
    private val camera = MutableVertex(0.0, 0.0, 0.0)

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
        if (!engine.input.keyboard.isPressed(KeyboardButton.Shift)) {
            val offset = getOffset(keyboard = engine.input.keyboard)
            if (!offset.isEmpty()) {
                val length = length(8.0, TimeUnit.SECONDS, diff)
                val radians = angleOf(x = offset.dX, y = offset.dY)
                camera.x -= length * kotlin.math.cos(radians)
                camera.y -= length * kotlin.math.sin(radians)
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Q)) {
            camera.z -= length(8.0, TimeUnit.SECONDS, diff)
        } else if (engine.input.keyboard.isPressed(KeyboardButton.E)) {
            camera.z += length(8.0, TimeUnit.SECONDS, diff)
        }
    }

    private fun onRenderV3(canvas: Canvas) {
        val pic = engine.property.picture
        matrix.identity()
        val id = matrix.copy()
        matrix.perspective(fov = kotlin.math.PI / 4, n = -1.0, f = 1.0)
        val pm = matrix.copy()
        matrix.identity()
        matrix.translate(dX = camera.x, dY = camera.y, dZ = camera.z)
        val mv = matrix.copy()
        GLUtil.onMatrix(pm = pm.transposed(), mv = mv.transposed()) {
            canvas.vectors.draw(
                color = Color.Red,
                start = MutableVertex(-1.0, 0.0, 12.0),
                finish = MutableVertex(1.0, 0.0, 12.0),
            )
            canvas.vectors.draw(
                color = Color.Blue,
                start = MutableVertex(0.0, -1.0, 24.0),
                finish = MutableVertex(0.0, 1.0, 24.0),
            )
        }
        matrix.identity()
        matrix.ortho(0.0, 0.0, pic.size.width, pic.size.height, -1_024.0, 1_024.0)
        GLUtil.onMatrix(pm = id, mv = id) {
            val vertex = MutableVertex(0.0, 1.0, 24.0)
            val vm = vertex * mv
            val text = String.format("%.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
            canvas.texts.draw(
                color = Color.Blue,
                fontHeight = 24.0,
                text = text,
                topLeft = vm,
            )
        }
    }

    private fun onRenderV2(canvas: Canvas) {
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPushMatrix()
        GL11.glLoadIdentity()
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPushMatrix()
        GL11.glLoadIdentity()
        canvas.vectors.draw(
            color = Color.Green,
            start = MutableVertex(-0.25, 0.0, 0.0),
            finish = MutableVertex(0.5, 0.0, 0.0),
        )
        GL11.glPopMatrix()
        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPopMatrix()
    }

    private fun onRenderV1(canvas: Canvas) {
        matrix.identity()
        val id = matrix.copy()
//        matrix.perspective(fov = kotlin.math.PI / 4, n = -1.0, f = 1.0)
        matrix.perspective(fov = kotlin.math.PI / 4, n = -1.0, f = 1.0)
        val pm = matrix.copy()
        matrix.identity()
        matrix.translate(dX = camera.x, dY = camera.y, dZ = camera.z)
        val mv = matrix.copy()
        GLUtil.onMatrix(pm = pm.transposed(), mv = mv.transposed()) {
            canvas.vectors.draw(
                color = Color.Red,
                start = MutableVertex(-1.0, 0.0, 12.0),
                finish = MutableVertex(1.0, 0.0, 12.0),
            )
            canvas.vectors.draw(
                color = Color.Blue,
                start = MutableVertex(0.0, -1.0, 24.0),
                finish = MutableVertex(0.0, 1.0, 24.0),
            )
        }
        GLUtil.onMatrix(pm = pm.transposed()) {
            canvas.vectors.draw(
                color = Color.Yellow,
                start = MutableVertex(-0.1, -1.0, 24.0) * mv,
                finish = MutableVertex(0.1, -1.0, 24.0) * mv,
            )
        }
        GLUtil.onMatrix(pm = id, mv = id) {
            matrix.identity()
            matrix *= pm
            matrix *= mv
            val vm = MutableVertex(-0.1, 1.0, 24.0) * matrix
            val text = String.format("%.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
            println("[Test]:vertex: $text")
//            canvas.vectors.draw(
//                color = Color.Green,
//                start = MutableVertex(-0.1, 1.0, 24.0) * matrix,
//                finish = MutableVertex(0.1, 1.0, 24.0) * matrix,
//            )
            GLUtil.colorOf(Color.Green)
            GLUtil.transaction(GL11.GL_LINES) {
                val v0 = MutableVertex(-0.1, 1.0, 24.0)
                GL11.glVertex4d(
                    matrix.m00 * v0.x + matrix.m01 * v0.y + matrix.m02 * v0.z + matrix.m03,
                    matrix.m10 * v0.x + matrix.m11 * v0.y + matrix.m12 * v0.z + matrix.m13,
                    matrix.m20 * v0.x + matrix.m21 * v0.y + matrix.m22 * v0.z + matrix.m23,
                    matrix.m30 * v0.x + matrix.m31 * v0.y + matrix.m32 * v0.z + matrix.m33,
                )
                val v1 = MutableVertex(0.1, 1.0, 24.0)
                GL11.glVertex4d(
                    matrix.m00 * v1.x + matrix.m01 * v1.y + matrix.m02 * v1.z + matrix.m03,
                    matrix.m10 * v1.x + matrix.m11 * v1.y + matrix.m12 * v1.z + matrix.m13,
                    matrix.m20 * v1.x + matrix.m21 * v1.y + matrix.m22 * v1.z + matrix.m23,
                    matrix.m30 * v1.x + matrix.m31 * v1.y + matrix.m32 * v1.z + matrix.m33,
                )
            }
        }
        GLUtil.onMatrix(pm = pm.transposed(), mv = id) {
            val vertex = MutableVertex(0.0, 1.0, 24.0)
            val vm = vertex * mv
            val text = String.format("%.1f:%.1f:%.1f", vm.x, vm.y, vm.z)
            canvas.texts.draw(
                color = Color.Blue,
                fontHeight = 1.0,
                atlasHeight = 32,
                text = text,
                x = vm.x,
                y = vm.y,
                z = vm.z,
            )
        }
    }

    private val matrix = MutableMatrix()
    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val pic = engine.property.picture
        onPreRender()
        //
        onRenderV1(canvas = canvas)
//        onRenderV2(canvas = canvas)
//        onRenderV3(canvas = canvas)
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            topLeft = MutableVertex(x = pic.size.width - 96.0, y = pic.size.height - fontHeight * 2, z = 0.0),
        )
        listOf(
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
