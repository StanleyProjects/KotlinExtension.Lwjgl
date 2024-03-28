package sp.kx.lwjgl.glfw

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.PolygonDrawer
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Point
import sp.kx.math.Size
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
        GLUtil.transaction(GL11.GL_LINE_LOOP) {
            points.forEach {
                // todo line width
                GLUtil.vertexOf(it)
            }
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
