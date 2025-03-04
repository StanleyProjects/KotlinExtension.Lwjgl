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
import sp.kx.math.Point
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.speedOf
import sp.kx.math.moved
import sp.kx.math.offsetOf
import sp.kx.math.pointOf
import sp.kx.math.radians
import sp.service.sample.Matrix
import sp.service.sample.MutableMatrix
import sp.service.sample.MutablePoint3D
import sp.service.sample.MutableQuaternion
import sp.service.sample.Point3D
import sp.service.sample.Quaternion
import sp.service.sample.copy
import sp.service.sample.identity
import sp.service.sample.mul
import sp.service.sample.mut
import sp.service.sample.perform
import sp.service.sample.rotateX
import sp.service.sample.rotated
import sp.service.sample.times
import sp.service.sample.translate
import kotlin.time.Duration

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
                    aX = 0.0
                    aY = 0.0
                    aZ = 0.0
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
        val aS = speedOf(2.0)
        if (engine.input.keyboard.isPressed(KeyboardButton.Shift)) {
            if (engine.input.keyboard.isPressed(KeyboardButton.Left)) {
                aZ = (aZ + aS.length(diff)).radians()
            } else if (engine.input.keyboard.isPressed(KeyboardButton.Right)) {
                aZ = (aZ - aS.length(diff)).radians()
            }
        } else {
            if (engine.input.keyboard.isPressed(KeyboardButton.Left)) {
                aY = (aY + aS.length(diff)).radians()
            } else if (engine.input.keyboard.isPressed(KeyboardButton.Right)) {
                aY = (aY - aS.length(diff)).radians()
            }
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.Up)) {
            aX = (aX+ aS.length(diff)).radians()
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Down)) {
            aX = (aX - aS.length(diff)).radians()
        }
        val dS = speedOf(48.0)
        if (engine.input.keyboard.isPressed(KeyboardButton.A)) {
            dX -= dS.length(diff)
        } else if (engine.input.keyboard.isPressed(KeyboardButton.D)) {
            dX += dS.length(diff)
        }
        if (engine.input.keyboard.isPressed(KeyboardButton.W)) {
            dY -= dS.length(diff)
        } else if (engine.input.keyboard.isPressed(KeyboardButton.S)) {
            dY += dS.length(diff)
        }
    }

    private class Axes(
        val p0: MutablePoint3D,
        val pX: MutablePoint3D,
        val pY: MutablePoint3D,
        val pZ: MutablePoint3D,
    )

    private class Cube(
        val p0: Point3D,
        val w: Double,
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
        x0: Double, y0: Double, z0: Double,
        x1: Double, y1: Double, z1: Double,
        dX: Double, dY: Double, dZ: Double,
    ) {
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex3d(x0 + dX, y0 + dY, z0 + dZ)
        GL11.glVertex3d(x1 + dX, y1 + dY, z1 + dZ)
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

    private fun drawLine(
        p0: Point3D,
        p1: Point3D,
        dX: Double,
        dY: Double,
        dZ: Double,
        q: Quaternion,
    ) {
        GL11.glBegin(GL11.GL_LINES)
        val p0q = p0.rotated(q = q)
        val p1q = p1.rotated(q = q)
        GL11.glVertex3d(p0q.x + dX, p0q.y + dY, p0q.z + dZ)
        GL11.glVertex3d(p1q.x + dX, p1q.y + dY, p1q.z + dZ)
        GL11.glEnd()
    }

//    private fun drawLine(
//        p0: Point3D,
//        p1: Point3D,
//        dX: Double, dY: Double, dZ: Double,
//        aX: Double, aY: Double, aZ: Double,
//    ) {
//        GL11.glBegin(GL11.GL_LINES)
//        val qX = MutableQuaternion.ofVector(x = 1.0, y = 0.0, z = 0.0, radians = aX / 2)
//        val qY = MutableQuaternion.ofVector(x = 0.0, y = 1.0, z = 0.0, radians = aY / 2)
//        val qZ = MutableQuaternion.ofVector(x = 0.0, y = 0.0, z = 1.0, radians = aZ / 2)
//        val p0q = p0
//            .rotated(q = qX)
//            .rotated(q = qY)
//            .rotated(q = qZ)
//        val p1q = p1
//            .rotated(q = qX)
//            .rotated(q = qY)
//            .rotated(q = qZ)
//        GL11.glVertex3d(p0q.x + dX, p0q.y + dY, p0q.z + dZ)
//        GL11.glVertex3d(p1q.x + dX, p1q.y + dY, p1q.z + dZ)
//        GL11.glEnd()
//    }

    private fun drawLine(
        p0: Point3D,
        p1: Point3D,
        dX: Double, dY: Double, dZ: Double,
        aX: Double, aY: Double, aZ: Double,
    ) {
        GL11.glBegin(GL11.GL_LINES)
        val mX = MutableMatrix.ofRotation(rX = 1.0, rY = 0.0, rZ = 0.0, radians = aX)
        val mY = MutableMatrix.ofRotation(rX = 0.0, rY = 1.0, rZ = 0.0, radians = aY)
        val mZ = MutableMatrix.ofRotation(rX = 0.0, rY = 0.0, rZ = 1.0, radians = aZ)
        val p0q = p0.mul(mX).mul(mY).mul(mZ)
        val p1q = p1.mul(mX).mul(mY).mul(mZ)
        GL11.glVertex3d(p0q.x + dX, p0q.y + dY, p0q.z + dZ)
        GL11.glVertex3d(p1q.x + dX, p1q.y + dY, p1q.z + dZ)
        GL11.glEnd()
    }

    private fun draw(
        cube: Cube,
        dX: Double, dY: Double, dZ: Double,
        aX: Double, aY: Double, aZ: Double,
    ) {
        GLUtil.colorOf(Color.Red)
        drawLine(
            p0 = cube.p0,
            p1 = cube.p0.copy(x = cube.p0.x + cube.w),
            dX = dX, dY = dY, dZ = dZ,
            aX = aX, aY = aY, aZ = aZ,
        )
        drawLine(
            p0 = cube.p0,
            p1 = cube.p0.copy(y = cube.p0.y + cube.w),
            dX = dX, dY = dY, dZ = dZ,
            aX = aX, aY = aY, aZ = aZ,
        )
        drawLine(
            p0 = cube.p0,
            p1 = cube.p0.copy(z = cube.p0.z + cube.w),
            dX = dX, dY = dY, dZ = dZ,
            aX = aX, aY = aY, aZ = aZ,
        )
    }

    private fun draw(cube: Cube, matrix: Matrix) {
//        GLUtil.colorOf(Color.White)
//        drawLine(
//            p0 = cube.p0.copy(x = cube.p0.x + cube.w / 2, y = cube.p0.y + cube.w / 2, z = cube.p0.z - cube.w / 2),
//            p1 = cube.p0.copy(x = cube.p0.x + cube.w / 2, y = cube.p0.y + cube.w / 2, z = cube.p0.z + cube.w * 3 / 2),
//            matrix = matrix,
//        )
//        drawLine(
//            p0 = cube.p0.copy(x = cube.p0.x - cube.w / 2, y = cube.p0.y + cube.w / 2, z = cube.p0.z + cube.w / 2),
//            p1 = cube.p0.copy(x = cube.p0.x + cube.w * 3 / 2, y = cube.p0.y + cube.w / 2, z = cube.p0.z + cube.w / 2),
//            matrix = matrix,
//        )
//        drawLine(
//            p0 = cube.p0.copy(x = cube.p0.x + cube.w / 2, y = cube.p0.y - cube.w / 2, z = cube.p0.z + cube.w / 2),
//            p1 = cube.p0.copy(x = cube.p0.x + cube.w / 2, y = cube.p0.y + cube.w * 3 / 2, z = cube.p0.z + cube.w / 2),
//            matrix = matrix,
//        )
        GLUtil.colorOf(Color.Red)
        drawLine(
            p0 = cube.p0,
            p1 = cube.p0.copy(x = cube.p0.x + cube.w),
            matrix = matrix,
        )
        drawLine(
            p0 = cube.p0,
            p1 = cube.p0.copy(y = cube.p0.y + cube.w),
            matrix = matrix,
        )
        drawLine(
            p0 = cube.p0,
            p1 = cube.p0.copy(z = cube.p0.z + cube.w),
            matrix = matrix,
        )
        GLUtil.colorOf(Color.Green)
        drawLine(
            p0 = cube.p0.copy(x = cube.p0.x + cube.w, y = cube.p0.y + cube.w),
            p1 = cube.p0.copy(x = cube.p0.x + cube.w),
            matrix = matrix,
        )
        drawLine(
            p0 = cube.p0.copy(x = cube.p0.x + cube.w, y = cube.p0.y + cube.w),
            p1 = cube.p0.copy(y = cube.p0.y + cube.w),
            matrix = matrix,
        )
        drawLine(
            p0 = cube.p0.copy(x = cube.p0.x + cube.w, y = cube.p0.y + cube.w),
            p1 = cube.p0.copy(x = cube.p0.x + cube.w, y = cube.p0.y + cube.w, z = cube.p0.z + cube.w),
            matrix = matrix,
        )
        GLUtil.colorOf(Color.Blue)
        drawLine(
            p0 = cube.p0.copy(x = cube.p0.x + cube.w, z = cube.p0.z + cube.w),
            p1 = cube.p0.copy(x = cube.p0.x + cube.w),
            matrix = matrix,
        )
        drawLine(
            p0 = cube.p0.copy(x = cube.p0.x + cube.w, z = cube.p0.z + cube.w),
            p1 = cube.p0.copy(z = cube.p0.z + cube.w),
            matrix = matrix,
        )
        drawLine(
            p0 = cube.p0.copy(x = cube.p0.x + cube.w, z = cube.p0.z + cube.w),
            p1 = cube.p0.copy(x = cube.p0.x + cube.w, y = cube.p0.y + cube.w, z = cube.p0.z + cube.w),
            matrix = matrix,
        )
        GLUtil.colorOf(Color.Yellow)
        drawLine(
            p0 = cube.p0.copy(y = cube.p0.y + cube.w, z = cube.p0.z + cube.w),
            p1 = cube.p0.copy(y = cube.p0.y + cube.w),
            matrix = matrix,
        )
        drawLine(
            p0 = cube.p0.copy(y = cube.p0.y + cube.w, z = cube.p0.z + cube.w),
            p1 = cube.p0.copy(z = cube.p0.z + cube.w),
            matrix = matrix,
        )
        drawLine(
            p0 = cube.p0.copy(y = cube.p0.y + cube.w, z = cube.p0.z + cube.w),
            p1 = cube.p0.copy(x = cube.p0.x + cube.w, y = cube.p0.y + cube.w, z = cube.p0.z + cube.w),
            matrix = matrix,
        )
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

    private val cube = Cube(
        p0 = MutablePoint3D(
            x = -48.0,
            y = -48.0,
            z = -48.0,
        ),
        w = 96.0,
    )

    private var aX = 0.0
    private var aY = 0.0
    private var aZ = 0.0

    private var dX: Double
    private var dY: Double
    private var dZ: Double

    init {
        val ps = engine.property.pictureSize
        dX = ps.width / 2
        dY = ps.height / 2
        dZ = -64.0
    }

    private val matrix = MutableMatrix()
    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        onPreRender()
        //
        GLUtil.colorOf(Color.Gray)
        drawLine(
            p0 = MutablePoint3D(ps.width / 2, 0.0, -256.0),
            p1 = MutablePoint3D(ps.width / 2, ps.height, -256.0),
        )
        drawLine(
            p0 = MutablePoint3D(0.0, ps.height / 2, -256.0),
            p1 = MutablePoint3D(ps.width, ps.height / 2, -256.0),
        )
        //
//        matrix.perform(
//            dX = dX, dY = dY, dZ = dZ,
//            rX = axes.p0.x, rY = axes.p0.y, rZ = axes.p0.z,
//            aX = aX, aY = aY, aZ = aZ,
//        )
//        matrix.identity()
//        val tm = matrix.mut()
//        tm.translate(dX = ps.width / 2, dY = ps.height / 2, dZ = -64.0)
//        val rx = matrix.mut()
//        rx.rotateX(0.0, 0.0, angle.x)
//        val rm = matrix.mut()
//        rm.mul(tm)
//        rm.mul(rx)
//        matrix.rotateX(angle.x)
//        draw(axes = axes, matrix = matrix)
        //
//        matrix.perform(
//            dX = dX, dY = dY, dZ = dZ,
//            rX = cube.p0.x + cube.w / 2, rY = cube.p0.y + cube.w / 2, rZ = cube.p0.z + cube.w / 2,
//            aX = aX, aY = aY, aZ = aZ,
//        )
        //
//        val p = Point.Center.moved(1.0, aY)
//        val p = MutablePoint3D.unitOf(radians = aY)
//        val p = MutablePoint3D.unitOf(p = kotlin.math.PI / 4, t = 0.0)
//        val p = MutablePoint3D.unitOf(p = kotlin.math.PI / 4, t = aY)
//        val rX = p.x
//        val rY = p.y
//        val rZ = p.z
//        GLUtil.colorOf(Color.White)
//        drawLine(
//            x0 = 0.0, y0 = 0.0, z0 = 0.0,
//            x1 = rX * 48.0, y1 = rY * 48.0, z1 = rZ * 48.0,
//            dX = dX, dY = dY, dZ = dZ,
//        )
        matrix.perform(
            dX = dX, dY = dY, dZ = dZ,
            aX = aX, aY = aY, aZ = aZ,
        )
//        val p = MutableQuaternion.ofVector(x = , y = , z = )
        draw(cube = cube, matrix = matrix)
//        draw(
//            cube = cube,
//            dX = dX, dY = dY, dZ = dZ,
//            aX = aX, aY = aY, aZ = aZ,
//        )
        canvas.texts.draw(
            color = Color.Red,
            fontHeight = 24.0,
            text = "0",
            pointTopLeft = cube.p0.mut().let {
                it.mul(matrix)
                pointOf(x = it.x, y = it.y)
            },
        )
        canvas.texts.draw(
            color = Color.Red,
            fontHeight = 24.0,
            text = "x",
            pointTopLeft = cube.p0.copy(x = cube.p0.x + cube.w).mut().let {
                it.mul(matrix)
                pointOf(x = it.x, y = it.y)
            },
        )
        canvas.texts.draw(
            color = Color.Red,
            fontHeight = 24.0,
            text = "y",
            pointTopLeft = cube.p0.copy(y = cube.p0.y + cube.w).mut().let {
                it.mul(matrix)
                pointOf(x = it.x, y = it.y)
            },
        )
        canvas.texts.draw(
            color = Color.Red,
            fontHeight = 24.0,
            text = "z",
            pointTopLeft = cube.p0.copy(z = cube.p0.z + cube.w).mut().let {
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
            String.format("aX: %+6.2f", aX),
            String.format("aY: %+6.2f", aY),
            String.format("aZ: %+6.2f", aZ),
            String.format("dX: %+6.2f", dX),
            String.format("dY: %+6.2f", dY),
            String.format("dZ: %+6.2f", dZ),
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
