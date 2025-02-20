package sp.service.sample.logics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Point
import sp.kx.math.measure.diff
import sp.kx.math.measure.frequency
import sp.kx.math.measure.speedOf
import sp.kx.math.pointOf
import sp.kx.math.radians
import sp.service.sample.MutableMatrix
import sp.service.sample.MutablePoint3D
import sp.service.sample.Point3D
import sp.service.sample.copy
import sp.service.sample.identity
import sp.service.sample.mul
import sp.service.sample.mut
import sp.service.sample.onMatrix
import sp.service.sample.ortho
import sp.service.sample.rotateX
import sp.service.sample.rotateY
import sp.service.sample.rotatedY
import sp.service.sample.scale
import sp.service.sample.set
import sp.service.sample.translate
import sp.service.sample.translated

internal class MatrixLogics(
    private val engine: Engine,
) : EngineLogics {
    private lateinit var ses: Unit
    override val inputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            if (isPressed) return
            when (button) {
                KeyboardButton.Escape -> ses = Unit
                KeyboardButton.C -> a = 0.0
                else -> Unit
            }
        }
    }

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private var dZ = -256.0
//    private var a = 0.0
    private var a = kotlin.math.PI / 2
    private fun onPreRender() {
        val diff = engine.property.time.diff()
        if (engine.input.keyboard.isPressed(KeyboardButton.Up)) {
            dZ += speedOf(1.0).length(diff)
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Down)) {
            dZ -= speedOf(1.0).length(diff)
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Left)) {
            a = (a + speedOf(1.0).length(diff)).radians()
        } else if (engine.input.keyboard.isPressed(KeyboardButton.Right)) {
            a = (a - speedOf(1.0).length(diff)).radians()
        }
    }

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
        start: Point3D,
        finish: Point3D,
    ) {
        drawLine(
            x0 = start.x, y0 = start.y, z0 = start.z,
            x1 = finish.x, y1 = finish.y, z1 = finish.z,
        )
    }

    private fun drawCube(x0: Double, y0: Double, z0: Double, s: Double) {
//        drawLine(
//            x0 = x0, y0 = y0, z0 = z0,
//            x1 = x0 + s, y1 = y0, z1 = z0,
//        )
//        drawLine(
//            x0 = x0, y0 = y0, z0 = z0,
//            x1 = x0, y1 = y0 + s, z1 = z0,
//        )
        drawLine(
            x0 = x0, y0 = y0, z0 = z0,
            x1 = x0, y1 = y0, z1 = z0 + s,
        )
//        drawLine(
//            x0 = x0 + s, y0 = y0 + s, z0 = z0,
//            x1 = x0, y1 = y0 + s, z1 = z0,
//        )
//        drawLine(
//            x0 = x0 + s, y0 = y0 + s, z0 = z0,
//            x1 = x0 + s, y1 = y0, z1 = z0,
//        )
        drawLine(
            x0 = x0 + s, y0 = y0 + s, z0 = z0,
            x1 = x0 + s, y1 = y0 + s, z1 = z0 + s,
        )
    }

    private fun drawCircle(x0: Double, y0: Double, z0: Double, edgeCount: Int, radius: Double) {
        GL11.glBegin(GL11.GL_POLYGON)
        for (index in 0 until edgeCount) {
            val radians = index * 2 * kotlin.math.PI / edgeCount
            GL11.glVertex3d(
                x0 + kotlin.math.cos(radians) * radius,
                y0 + kotlin.math.sin(radians) * radius,
                z0,
            )
        }
        GL11.glEnd()
    }

    private fun drawCircle(point: Point3D, edgeCount: Int, radius: Double) {
        drawCircle(
            x0 = point.x, y0 = point.y, z0 = point.z,
            edgeCount = edgeCount, radius = radius,
        )
    }

    private val matrix = MutableMatrix()
    private val buffer = BufferUtils.createDoubleBuffer(16)

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        onPreRender()
        //
        /*
        val m1 = MutableMatrix(
            m00 = 1.0, m01 = 4.0, m02 = 3.0, m03 = 0.0,
            m10 = 2.0, m11 = 1.0, m12 = 5.0, m13 = 0.0,
            m20 = 3.0, m21 = 2.0, m22 = 1.0, m23 = 0.0,
            m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 0.0,
        )
        val m2 = MutableMatrix(
            m00 = 5.0, m01 = 2.0, m02 = 1.0, m03 = 0.0,
            m10 = 4.0, m11 = 3.0, m12 = 2.0, m13 = 0.0,
            m20 = 2.0, m21 = 1.0, m22 = 5.0, m23 = 0.0,
            m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 0.0,
        )
        val m3 = m1.mut()
        m3.mul(m2)
        m3.also {
            check(it.m00 == 27.0) {"00: ${it.m00}"};
            check(it.m01 == 17.0) {"01: ${it.m01}(17) ${m1.m00} * ${m2.m01} + ${m1.m01} * ${m2.m11} + ${m1.m02} * ${m2.m21} + ${m1.m03} * ${m2.m31}"};
            check(it.m02 == 24.0); check(it.m03 == 0.0) {"03: ${it.m03}"}
            check(it.m10 == 24.0) {"10: ${it.m10}"}; check(it.m11 == 12.0) {"11: ${it.m11}"}; check(it.m12 == 29.0); check(it.m13 == 0.0) {"13: ${it.m13}"}
            check(it.m20 == 25.0) {"20: ${it.m20}"}; check(it.m21 == 13.0) {"21: ${it.m21}"}; check(it.m22 == 12.0); check(it.m23 == 0.0) {"23: ${it.m23}"}
            check(it.m30 == 0.0) {"30: ${it.m30}"}; check(it.m31 == 0.0) {"31: ${it.m31}"}; check(it.m32 == 0.0); check(it.m33 == 0.0) {"33: ${it.m33}"}
        }
        */
        //
        /*
        matrix.identity()
        matrix.ortho(l = 0.0, t = 0.0, r = ps.width, b = ps.height, zNear = -512.0, zFar = 512.0)
        onMatrix(matrix = matrix.mut(), buffer = buffer) {
            GLUtil.colorOf(Color.Gray)
            drawLine(
                x0 = ps.width / 2, y0 = 0.0, z0 = 0.0,
                x1 = ps.width / 2, y1 = ps.height, z1 = 0.0,
            )
            drawLine(
                x0 = 0.0, y0 = ps.height / 2, z0 = 0.0,
                x1 = ps.width, y1 = ps.height / 2, z1 = 0.0,
            )
        }
        matrix.translate(dX = ps.width / 2, dY =  ps.height / 2, dZ = 0.0)
        onMatrix(matrix = matrix.mut(), buffer = buffer) {
            GLUtil.colorOf(Color.Green)
            drawCircle(
                x0 = 0.0, y0 = 0.0, z0 = z,
                edgeCount = 4, radius = 12.0,
            )
        }
        matrix.rotateY(a)
        onMatrix(matrix = matrix.mut(), buffer = buffer) {
            GLUtil.colorOf(Color.Red)
            val d = 48.0
            drawCube(x0 = -d, y0 = -d, z0 = -d + z, s = d * 2)
        }
        */
        val s = 64.0
        val x0 = 0.0
//        val x0 = ps.width / 2
        val y0 = 0.0
//        val y0 = ps.height / 2
        val z0 = 0.0
//        val z0 = dZ
        matrix.identity()
        val tm = matrix.mut()
        tm.translate(dX = ps.width / 2, dY = ps.height / 2, dZ = dZ)
        val rx = matrix.mut()
        rx.rotateX(oY = y0, oZ = z0, radians = a)
        val ry = matrix.mut()
        ry.rotateY(oX = x0, oZ = z0, radians = a)
        val rm = matrix.mut()
//        rm.mul(
//            MutableMatrix().also {
//                it.identity()
//                it.rotateX(oY = y0, oZ = z0, radians = a)
//            }
//        )
        rm.mul(
            MutableMatrix().also {
                it.identity()
                it.rotateY(oX = x0, oZ = z0, radians = a)
            }
        )
        val sm = matrix.mut()
//        sm.scale(dX = 2.0, dY = 2.0, dZ = 2.0)
//        matrix.ortho(l = 0.0, t = 0.0, r = ps.width, b = ps.height, zNear = 0.0, zFar = 512.0)
//        matrix.translate(dX = 0.0, dY = 0.0, dZ = dZ)
        //
//        matrix.mul(tm)
//        matrix.mul(rm)
//        matrix.mul(sm)
        //
        matrix.mul(sm)
        matrix.mul(rm)
        matrix.mul(tm)
        //
//        matrix.translate(dX = ps.width / 2, dY = ps.height / 2, dZ = dZ)
//        matrix.rotateY(oX = x0, oZ = z0, radians = a)
        val pn = (z0 + s).let { z ->
            listOf(
                -1 to -1,
                1 to -1,
                -1 to 1,
                1 to 1,
            ).map { (dX, dY) ->
                val point = MutablePoint3D(
                    x = x0 + s * dX,
                    y = y0 + s * dY,
                    z = z,
                )
//                point.rotateX(oY = y0, oZ = z0, radians = a)
//                point.rotateY(oX = 0.0, oZ = 0.0, radians = a)
//                point.rotateY(oX = x0, oZ = z0, radians = a)
//                point.translate(dX = 0.0, dY = 0.0, dZ = dZ)
//                point.translate(dX = ps.width / 2, dY = ps.height / 2, dZ = dZ)
//                point.mul(matrix = matrix)
//                point.mul(rm)
//                point.mul(rx)
                point.mul(ry)
                point.mul(tm)
                point
            }
        }
        val pf = (z0 - s).let { z ->
            listOf(
                -1 to -1,
                1 to -1,
                -1 to 1,
                1 to 1,
            ).map { (dX, dY) ->
                val point = MutablePoint3D(
                    x = x0 + s * dX,
                    y = y0 + s * dY,
                    z = z,
                )
//                point.rotateX(oY = y0, oZ = z0, radians = a)
//                point.rotateY(oX = x0, oZ = z0, radians = a)
//                point.translate(dX = 0.0, dY = 0.0, dZ = dZ)
//                point.translate(dX = ps.width / 2, dY = ps.height / 2, dZ = dZ)
//                point.mul(matrix = matrix)
//                point.mul(rm)
//                point.mul(rx)
                point.mul(ry)
                point.mul(tm)
                point
            }
        }
        GLUtil.colorOf(Color.Gray)
        drawLine(
            x0 = x0, y0 = 0.0, z0 = 0.0,
            x1 = x0, y1 = ps.height, z1 = 0.0,
        )
        drawLine(
            x0 = 0.0, y0 = y0, z0 = 0.0,
            x1 = ps.width, y1 = y0, z1 = 0.0,
        )
//        GLUtil.colorOf(Color.Green)
//        drawCircle(
//            x0 = ps.width / 2, y0 = ps.height / 2, z0 = 0.0,
//            edgeCount = 4, radius = 6.0,
//        )
        /*
        (0 until 16).forEach { index ->
            GLUtil.colorOf(Color.Yellow)
            val x = 48.0 * index
            val y = y0
            val z = 64 * index - 512.0 - 64 * 2
            drawCircle(
                x0 = x, y0 = y, z0 = z,
                edgeCount = 4, radius = 6.0,
            )
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = 18.0,
                text = String.format("%3.0f", z),
                x = x,
                y = y,
            )
        }
        */
        pn.let { (p1, p2, p3, p4) ->
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = 24.0,
                text = "n1",
                x = p1.x,
                y = p1.y,
            )
            GLUtil.colorOf(Color.Red)
            drawLine(p1, p2)
            drawLine(p1, p3)
            drawLine(p4, p2)
            drawLine(p4, p3)
        }
        pf.let { (p1, p2, p3, p4) ->
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = 24.0,
                text = "f1",
                x = p1.x,
                y = p1.y,
            )
            GLUtil.colorOf(Color.Blue)
            drawLine(p1, p2)
            drawLine(p1, p3)
            drawLine(p4, p2)
            drawLine(p4, p3)
        }
        GLUtil.colorOf(Color.Yellow)
        drawLine(pn[0], pf[0])
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = ps.width - 128.0, y = ps.height - fontHeight * 2),
        )
        listOf(
            String.format("n0: x: %+6.2f y: %+6.2f z: %+6.2f", pn[0].x, pn[0].y, pn[0].z),
            String.format("f0: x: %+6.2f y: %+6.2f z: %+6.2f", pf[0].x, pf[0].y, pf[0].z),
//            String.format("p3: x: %+6.2f y: %+6.2f z: %+6.2f", pn[2].x, pn[2].y, pn[2].z),
//            String.format("p4: x: %+6.2f y: %+6.2f z: %+6.2f", pn[3].x, pn[3].y, pn[3].z),
            String.format("a: %+6.2f %+6.2f", a, java.lang.Math.toDegrees(a)),
            String.format("dZ: %+6.2f", dZ),
        ).forEachIndexed { index, text ->
            canvas.texts.draw(
                color = Color.Green,
                fontHeight = fontHeight,
                text = text,
                pointTopLeft = pointOf(x = fontHeight * 2, y = ps.height - fontHeight * (2 + index)),
            )
        }
    }
}
