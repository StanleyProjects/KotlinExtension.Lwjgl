package sp.kx.lwjgl.glfw

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.PolygonDrawer
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.angleOf
import sp.kx.math.plus
import sp.kx.math.pointOf

internal object GLPolygonDrawer : PolygonDrawer {
    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height
        )
        val points = setOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y)
        )
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            points.forEach {
                GLUtil.vertexOf(it)
            }
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point
    ) {
        GLUtil.onMatrix {
            GL11.glTranslated(pointOfRotation.x, pointOfRotation.y, 0.0)
            GL11.glRotated(Math.toDegrees(direction), 0.0, 0.0, 1.0)
            drawRectangle(
                color = color,
                pointTopLeft = pointTopLeft.plus(
                    dX = -pointOfRotation.x,
                    dY = -pointOfRotation.y
                ),
                size = size,
            )
        }
    }

    private fun vertexOf(start: Point, finish: Point, lineWidth: Double) {
        val angle = angleOf(start, finish)
        GLUtil.vertexOfMoved(start, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2)
        GLUtil.vertexOfMoved(start, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2)
        GLUtil.vertexOfMoved(finish, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2)
        GLUtil.vertexOfMoved(finish, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2)
    }

    override fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Double,
    ) {
        val points = (0..edgeCount).map {
            val radians = it * 2 * kotlin.math.PI / edgeCount
            pointCenter.plus(
                dX = kotlin.math.cos(radians) * radius,
                dY = kotlin.math.sin(radians) * radius,
            )
        }
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val fStart = points[0]
            val fFinish = points[1]
            vertexOf(start = fStart, finish = fFinish, lineWidth = lineWidth)
            for (i in 2 until points.size) {
                vertexOf(start = points[i-1], finish = points[i], lineWidth = lineWidth)
            }
            GLUtil.vertexOfMoved(fStart, length = lineWidth / 2, angle = angleOf(fStart, fFinish) - kotlin.math.PI / 2)
        }
    }

    override fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
    ) {
        val points = (0..edgeCount).map {
            val radians = it * 2 * kotlin.math.PI / edgeCount
            pointCenter.plus(
                dX = kotlin.math.cos(radians) * radius,
                dY = kotlin.math.sin(radians) * radius,
            )
        }
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            points.forEach {
                GLUtil.vertexOf(it)
            }
        }
    }
}
