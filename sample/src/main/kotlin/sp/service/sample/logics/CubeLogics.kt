package sp.service.sample.logics

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Offset
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.speedOf
import sp.kx.math.offsetOf
import sp.kx.math.pointOf
import sp.kx.math.radians
import sp.service.sample.Matrix
import sp.service.sample.MutableMatrix
import sp.service.sample.MutablePoint3D
import sp.service.sample.Point3D
import sp.service.sample.identity
import sp.service.sample.mul
import sp.service.sample.mut
import sp.service.sample.perform
import sp.service.sample.rotateX
import sp.service.sample.translate

internal class CubeLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                KeyboardButton.C -> {
                    angle.x = 0.0
                    angle.y = 0.0
                    angle.z = 0.0
                }
                else -> Unit
            }
        }
    }

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private fun onPreRender() {
        val diff = engine.property.time.diff()
        val speed = speedOf(1.0)
        if (engine.input.keyboard.isPressed(KeyboardButton.Left)) {
            angle.y = (angle.y + speed.length(diff)).radians()
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Right)) {
            angle.y = (angle.y - speed.length(diff)).radians()
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Up)) {
            angle.x = (angle.x + speed.length(diff)).radians()
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Down)) {
            angle.x = (angle.x - speed.length(diff)).radians()
        }
    }

    private class Axes(
        val p0: MutablePoint3D,
        val pX: MutablePoint3D,
        val pY: MutablePoint3D,
        val pZ: MutablePoint3D,
    )

    private fun drawLine(
        x0: Double, y0: Double, z0: Double,
        x1: Double, y1: Double, z1: Double,
    ) {
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex3d(x0, y0, z0)
        GL11.glVertex3d(x1, y1, z1)
        GL11.glEnd()
    }

    private fun drawLine(
        point: Point3D,
        x1: Double = point.x, y1: Double = point.y, z1: Double = point.z,
    ) {
        drawLine(
            x0 = point.x, y0 = point.y, z0 = point.z,
            x1 = x1, y1 = y1, z1 = z1,
        )
    }

    private fun drawLine(
        p0: Point3D,
        p1: Point3D,
    ) {
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex3d(p0.x, p0.y, p0.z)
        GL11.glVertex3d(p1.x, p1.y, p1.z)
        GL11.glEnd()
    }

    private fun drawLine(
        p0: Point3D,
        p1: Point3D,
        matrix: Matrix,
    ) {
        GL11.glBegin(GL11.GL_LINES)
        val x0 = matrix.m00 * p0.x + matrix.m01 * p0.y + matrix.m02 * p0.z + matrix.m03
        val y0 = matrix.m10 * p0.x + matrix.m11 * p0.y + matrix.m12 * p0.z + matrix.m13
        val z0 = matrix.m20 * p0.x + matrix.m21 * p0.y + matrix.m22 * p0.z + matrix.m23
        GL11.glVertex3d(x0, y0, z0)
        val x1 = matrix.m00 * p1.x + matrix.m01 * p1.y + matrix.m02 * p1.z + matrix.m03
        val y1 = matrix.m10 * p1.x + matrix.m11 * p1.y + matrix.m12 * p1.z + matrix.m13
        val z1 = matrix.m20 * p1.x + matrix.m21 * p1.y + matrix.m22 * p1.z + matrix.m23
        GL11.glVertex3d(x1, y1, z1)
        GL11.glEnd()
    }

    private fun drawLine(
        p0: Point3D,
        p1: Point3D,
        matrix: Matrix,
        dX: Double,
        dY: Double,
        dZ: Double,
    ) {
        GL11.glBegin(GL11.GL_LINES)
        val x0 = matrix.m00 * p0.x + matrix.m01 * p0.y + matrix.m02 * p0.z + matrix.m03
        val y0 = matrix.m10 * p0.x + matrix.m11 * p0.y + matrix.m12 * p0.z + matrix.m13
        val z0 = matrix.m20 * p0.x + matrix.m21 * p0.y + matrix.m22 * p0.z + matrix.m23
        GL11.glVertex3d(x0 + dX, y0 + dY, z0 + dZ)
        val x1 = matrix.m00 * p1.x + matrix.m01 * p1.y + matrix.m02 * p1.z + matrix.m03
        val y1 = matrix.m10 * p1.x + matrix.m11 * p1.y + matrix.m12 * p1.z + matrix.m13
        val z1 = matrix.m20 * p1.x + matrix.m21 * p1.y + matrix.m22 * p1.z + matrix.m23
        GL11.glVertex3d(x1 + dX, y1 + dY, z1 + dZ)
        GL11.glEnd()
    }

    private fun drawLine(
        p0: Point3D,
        p1: Point3D,
        dX: Double,
        dY: Double,
        dZ: Double,
    ) {
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex3d(p0.x + dX, p0.y + dY, p0.z + dZ)
        GL11.glVertex3d(p1.x + dX, p1.y + dY, p1.z + dZ)
        GL11.glEnd()
    }

    private fun draw(axes: Axes, matrix: Matrix) {
        GLUtil.colorOf(Color.Red)
        drawLine(
            p0 = axes.p0,
            p1 = axes.pX,
            matrix = matrix,
        )
        GLUtil.colorOf(Color.Green)
        drawLine(
            p0 = axes.p0,
            p1 = axes.pY,
            matrix = matrix,
        )
        GLUtil.colorOf(Color.Blue)
        drawLine(
            p0 = axes.p0,
            p1 = axes.pZ,
            matrix = matrix,
        )
    }

    private fun draw(
        axes: Axes,
        matrix: Matrix,
        dX: Double,
        dY: Double,
        dZ: Double,
    ) {
        GLUtil.colorOf(Color.Red)
        drawLine(
            p0 = axes.p0,
            p1 = axes.pX,
            matrix = matrix,
            dX = dX,
            dY = dY,
            dZ = dZ,
        )
        GLUtil.colorOf(Color.Green)
        drawLine(
            p0 = axes.p0,
            p1 = axes.pY,
            matrix = matrix,
            dX = dX,
            dY = dY,
            dZ = dZ,
        )
        GLUtil.colorOf(Color.Blue)
        drawLine(
            p0 = axes.p0,
            p1 = axes.pZ,
            matrix = matrix,
            dX = dX,
            dY = dY,
            dZ = dZ,
        )
    }

    private val axes = engine.property.pictureSize.let { _ ->
        val length = 96.0
        val p0 = MutablePoint3D(
            x = 0.0,
//            x = 64.0,
            y = 0.0,
//            y = 96.0,
            z = 0.0,
        )
        Axes(
            p0 = p0,
            pX = MutablePoint3D(
                x = p0.x + length,
                y = p0.y,
                z = p0.z,
            ),
            pY = MutablePoint3D(
                x = p0.x,
                y = p0.y + length,
                z = p0.z,
            ),
            pZ = MutablePoint3D(
                x = p0.x,
                y = p0.y,
                z = p0.z + length,
            ),
        )
    }
    private val angle = MutablePoint3D(x = 0.0, y = 0.0, z = 0.0)

    private val matrix = MutableMatrix()
    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        onPreRender()
        //
//        val dX = 0.0; val dY = 0.0; val dZ = 0.0
        val dX = ps.width / 2; val dY = ps.height / 2; val dZ = -192.0
        matrix.perform(
            dX = ps.width / 2, dY = ps.height / 2, dZ = -64.0,
            rX = axes.p0.x, rY = axes.p0.y, rZ = axes.p0.z,
            aX = angle.x, aY = angle.y, aZ = angle.z,
        )
//        matrix.identity()
//        val tm = matrix.mut()
//        tm.translate(dX = ps.width / 2, dY = ps.height / 2, dZ = -64.0)
//        val rx = matrix.mut()
//        rx.rotateX(0.0, 0.0, angle.x)
//        val rm = matrix.mut()
//        rm.mul(tm)
//        rm.mul(rx)
//        matrix.rotateX(angle.x)
        draw(axes = axes, matrix = matrix)
        canvas.texts.draw(
            color = Color.Red,
            fontHeight = 24.0,
            text = "x",
            pointTopLeft = axes.pX.mut().let {
                it.mul(matrix)
                pointOf(x = it.x, y = it.y)
            },
        )
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = 24.0,
            text = "y",
            pointTopLeft = axes.pY.mut().let {
                it.mul(matrix)
                pointOf(x = it.x, y = it.y)
            },
        )
        canvas.texts.draw(
            color = Color.Blue,
            fontHeight = 24.0,
            text = "z",
            pointTopLeft = axes.pZ.mut().let {
                it.mul(matrix)
                pointOf(x = it.x, y = it.y)
            },
        )
//        draw(axes = axes, matrix = rm, dX = dX, dY = dY, dZ = dZ)
        /*
        GLUtil.colorOf(Color.Blue)
        val p0 = MutablePoint3D(x = 0.0, y = 0.0, z = 0.0)
        val p1 = MutablePoint3D(x = 0.0, y = 0.0, z = 24.0)
        p0.mul(rm)
        p1.mul(rm)
        drawLine(
            p0 = p0,
            p1 = p1,
            dX = dX,
            dY = dY,
            dZ = dZ,
        )
        */
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = ps.width - 96.0, y = ps.height - fontHeight * 2),
        )
        listOf(
            String.format("a:x: %+6.2f", angle.x),
            String.format("a:y: %+6.2f", angle.y),
            String.format("a:z: %+6.2f", angle.z),
        ).forEachIndexed { index, text ->
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = fontHeight,
                text = text,
                pointTopLeft = pointOf(x = fontHeight, y = fontHeight * (1 + index)),
            )
        }
    }
}
