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
import sp.service.sample.mut
import sp.service.sample.onMatrix
import sp.service.sample.ortho
import sp.service.sample.rotateX
import sp.service.sample.rotateY
import sp.service.sample.rotatedY
import sp.service.sample.translate

internal class MatrixLogics(
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

    override fun shouldEngineStop(): Boolean {
        return ::ses.isInitialized
    }

    private var dZ = 0.0
    private var a = 0.0
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

    private val matrix = MutableMatrix()
    private val buffer = BufferUtils.createDoubleBuffer(16)

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

    override fun onRender(canvas: Canvas) {
        val fps = engine.property.time.frequency()
        val ps = engine.property.pictureSize
        onPreRender()
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
        matrix.identity()
        matrix.ortho(l = 0.0, t = 0.0, r = ps.width, b = ps.height, zNear = -512.0, zFar = 512.0)
        val s = 64.0
        val x0 = ps.width / 2
        val y0 = ps.height / 2
        val z0 = 0.0
        val pn = (z0 + s + dZ).let { z ->
            listOf(
                -1 to -1,
                1 to -1,
                -1 to 1,
                1 to 1,
            ).map { (dX, dY) ->
                MutablePoint3D(
                    x = x0 + s * dX,
                    y = y0 + s * dY,
                    z = z,
                )
            }
        }.map {
            it.rotatedY(oX = x0, oZ = z0, radians = a)
        }
        val pf = (z0 - s + dZ).let { z ->
            listOf(
                -1 to -1,
                1 to -1,
                -1 to 1,
                1 to 1,
            ).map { (dX, dY) ->
                MutablePoint3D(
                    x = x0 + s * dX,
                    y = y0 + s * dY,
                    z = z,
                )
            }
        }.map {
            it.rotatedY(oX = x0, oZ = z0, radians = a)
        }
        onMatrix(matrix, buffer) {
            GLUtil.colorOf(Color.Gray)
            drawLine(
                x0 = ps.width / 2, y0 = 0.0, z0 = 0.0,
                x1 = ps.width / 2, y1 = ps.height, z1 = 0.0,
            )
            drawLine(
                x0 = 0.0, y0 = ps.height / 2, z0 = 0.0,
                x1 = ps.width, y1 = ps.height / 2, z1 = 0.0,
            )
            GLUtil.colorOf(Color.Green)
            drawCircle(
                x0 = ps.width / 2, y0 = ps.height / 2, z0 = 0.0,
                edgeCount = 4, radius = 6.0,
            )
//            GLUtil.colorOf(Color.Red)
//            drawLine(
//                x0 = ps.width / 2 - d, y0 = ps.height / 2 - d, z0 = z,
//                x1 = ps.width / 2 + d, y1 = ps.height / 2 - d, z1 = z,
//            )
//            GLUtil.colorOf(Color.Yellow)
//            drawLine(
//                x0 = ps.width / 2 - d, y0 = ps.height / 2 + d, z0 = z,
//                x1 = ps.width / 2 + d, y1 = ps.height / 2 + d, z1 = z,
//            )
            pn.let { (p1, p2, p3, p4) ->
                canvas.texts.draw(
                    color = Color.Green,
                    fontHeight = 24.0,
                    text = "near",
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
                    text = "far",
                    x = p1.x,
                    y = p1.y,
                )
                GLUtil.colorOf(Color.Blue)
                drawLine(p1, p2)
                drawLine(p1, p3)
                drawLine(p4, p2)
                drawLine(p4, p3)
            }
            /*
            GLUtil.colorOf(Color.Yellow)
            drawCircle(
                point = start,
                edgeCount = 4,
                radius = 6.0,
            )
            GLUtil.colorOf(Color.Blue)
            drawCircle(
                point = start.copy(x = ps.width / 2 + d),
                edgeCount = 4,
                radius = 6.0,
            )
            drawCircle(
                point = start.copy(x = ps.width / 2 - d * 3),
                edgeCount = 4,
                radius = 6.0,
            )
            drawCircle(
                point = start.copy(y = ps.height / 2 + d),
                edgeCount = 4,
                radius = 6.0,
            )
            drawCircle(
                point = start.copy(y = ps.height / 2 - d * 3),
                edgeCount = 4,
                radius = 6.0,
            )
            */
        }
        //
        val fontHeight = 24.0
        canvas.texts.draw(
            color = Color.Green,
            fontHeight = fontHeight,
            text = String.format("%6.2f", fps),
            pointTopLeft = pointOf(x = ps.width - 128.0, y = ps.height - fontHeight * 2),
        )
        listOf(
            String.format("p1: x: %+6.2f y: %+6.2f z: %+6.2f", pn[0].x, pn[0].y, pn[0].z),
//            String.format("f:x: %+6.2f f:y: %+6.2f f:z: %+6.2f", finish.x, finish.y, finish.z),
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
