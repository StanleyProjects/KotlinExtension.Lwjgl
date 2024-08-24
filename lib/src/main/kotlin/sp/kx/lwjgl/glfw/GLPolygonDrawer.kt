package sp.kx.lwjgl.glfw

import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.PolygonDrawer
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.angleOf
import sp.kx.math.measure.Measure
import sp.kx.math.plus
import sp.kx.math.pointOf

internal object GLPolygonDrawer : PolygonDrawer {
    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height,
        )
        val points = setOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
        )
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            // todo loop to 4 vertexOf(Double, Double)
            points.forEach {
                GLUtil.vertexOf(it)
            }
        }
    }

    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size, lineWidth: Double) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height,
        )
        val points = listOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
        )
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val fStart = points[0]
            val fFinish = points[1]
            vertexOf(start = fStart, finish = fFinish, lineWidth = lineWidth)
            for (i in 2 until points.size) {
                vertexOf(start = points[i-1], finish = points[i], lineWidth = lineWidth)
            }
            vertexOf(start = points.last(), finish = fStart, lineWidth = lineWidth)
            GLUtil.vertexOfMoved(fStart, length = lineWidth / 2, angle = angleOf(fStart, fFinish) - kotlin.math.PI / 2)
        }
    }

    override fun drawRectangle(
        borderColor: Color,
        fillColor: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
    ) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height
        )
        val points = listOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
        )
        GL11.glLineWidth(1f)
        GLUtil.colorOf(fillColor)
        GLUtil.transaction(GL11.GL_POLYGON) {
            points.forEach {
                GLUtil.vertexOf(it)
            }
        }
        GLUtil.colorOf(borderColor)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val fStart = points[0]
            val fFinish = points[1]
            vertexOf(start = fStart, finish = fFinish, lineWidth = lineWidth)
            for (i in 2 until points.size) {
                vertexOf(start = points[i-1], finish = points[i], lineWidth = lineWidth)
            }
            vertexOf(start = points.last(), finish = fStart, lineWidth = lineWidth)
            GLUtil.vertexOfMoved(fStart, length = lineWidth / 2, angle = angleOf(fStart, fFinish) - kotlin.math.PI / 2)
        }
    }

    override fun drawRectangle(
        borderColor: Color,
        fillColor: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height
        )
        val points = listOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
        )
        GL11.glLineWidth(1f)
        GLUtil.colorOf(fillColor)
        GLUtil.transaction(GL11.GL_POLYGON) {
            points.forEach {
                GLUtil.vertexOf(it, offset = offset, measure = measure)
            }
        }
        GLUtil.colorOf(borderColor)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val fStart = points[0]
            val fFinish = points[1]
            vertexOf(start = fStart, finish = fFinish, lineWidth = lineWidth, offset = offset, measure = measure)
            for (i in 2 until points.size) {
                vertexOf(start = points[i-1], finish = points[i], lineWidth = lineWidth, offset = offset, measure = measure)
            }
            vertexOf(start = points.last(), finish = fStart, lineWidth = lineWidth, offset = offset, measure = measure)
            GLUtil.vertexOfMoved(fStart, length = lineWidth / 2, angle = angleOf(fStart, fFinish) - kotlin.math.PI / 2, offset = offset, measure = measure)
        }
    }

    override fun drawRectangle(
        borderColor: Color,
        fillColor: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        measure: Measure<Double, Double>,
    ) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height
        )
        val points = listOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
        )
        GL11.glLineWidth(1f)
        GLUtil.colorOf(fillColor)
        GLUtil.transaction(GL11.GL_POLYGON) {
            points.forEach {
                GLUtil.vertexOf(it, measure = measure)
            }
        }
        GLUtil.colorOf(borderColor)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val fStart = points[0]
            val fFinish = points[1]
            vertexOf(start = fStart, finish = fFinish, lineWidth = lineWidth, measure = measure)
            for (i in 2 until points.size) {
                vertexOf(start = points[i-1], finish = points[i], lineWidth = lineWidth, measure = measure)
            }
            vertexOf(start = points.last(), finish = fStart, lineWidth = lineWidth, measure = measure)
            GLUtil.vertexOfMoved(fStart, length = lineWidth / 2, angle = angleOf(fStart, fFinish) - kotlin.math.PI / 2, measure = measure)
        }
    }

    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size, offset: Offset) {
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
                GLUtil.vertexOf(it, offset = offset)
            }
        }
    }

    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size, measure: Measure<Double, Double>) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height,
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
                GLUtil.vertexOf(it, measure = measure)
            }
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>
    ) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height,
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
                GLUtil.vertexOf(it, offset = offset, measure = measure)
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
                    dY = -pointOfRotation.y,
                ),
                size = size,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
        direction: Double,
        pointOfRotation: Point
    ) {
        GLUtil.onMatrix {
            GLUtil.translated(point = pointOfRotation, offset = offset)
            GL11.glRotated(Math.toDegrees(direction), 0.0, 0.0, 1.0)
            drawRectangle(
                color = color,
                pointTopLeft = pointTopLeft.plus(
                    dX = -pointOfRotation.x,
                    dY = -pointOfRotation.y,
                ),
                size = size,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        measure: Measure<Double, Double>,
        direction: Double,
        pointOfRotation: Point
    ) {
        GLUtil.onMatrix {
            GLUtil.translated(point = pointOfRotation, measure = measure)
            GL11.glRotated(Math.toDegrees(direction), 0.0, 0.0, 1.0)
            drawRectangle(
                color = color,
                pointTopLeft = pointTopLeft.plus(
                    dX = -pointOfRotation.x,
                    dY = -pointOfRotation.y,
                ),
                size = size,
                measure = measure,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        measure: Measure<Double, Double>,
        lineWidth: Double
    ) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height
        )
        val points = listOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
        )
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val fStart = points[0]
            val fFinish = points[1]
            vertexOf(start = fStart, finish = fFinish, lineWidth = lineWidth, measure = measure)
            for (i in 2 until points.size) {
                vertexOf(start = points[i-1], finish = points[i], lineWidth = lineWidth, measure = measure)
            }
            vertexOf(start = points.last(), finish = fStart, lineWidth = lineWidth, measure = measure)
            GLUtil.vertexOfMoved(fStart, length = lineWidth / 2, angle = angleOf(fStart, fFinish) - kotlin.math.PI / 2, measure = measure)
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Double,
    ) {
        val pointBottomRight = pointTopLeft.plus(
            dX = size.width,
            dY = size.height
        )
        val points = listOf(
            pointTopLeft,
            pointOf(pointBottomRight.x, pointTopLeft.y),
            pointBottomRight,
            pointOf(pointTopLeft.x, pointBottomRight.y),
        )
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val fStart = points[0]
            val fFinish = points[1]
            vertexOf(start = fStart, finish = fFinish, lineWidth = lineWidth, offset = offset, measure = measure)
            for (i in 2 until points.size) {
                vertexOf(start = points[i-1], finish = points[i], lineWidth = lineWidth, offset = offset, measure = measure)
            }
            vertexOf(start = points.last(), finish = fStart, lineWidth = lineWidth, offset = offset, measure = measure)
            GLUtil.vertexOfMoved(fStart, length = lineWidth / 2, angle = angleOf(fStart, fFinish) - kotlin.math.PI / 2, offset = offset, measure = measure)
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Double,
        direction: Double,
        pointOfRotation: Point
    ) {
        GLUtil.onMatrix {
            GLUtil.translated(point = pointOfRotation, offset = offset, measure = measure)
            GL11.glRotated(Math.toDegrees(direction), 0.0, 0.0, 1.0)
            drawRectangle(
                color = color,
                pointTopLeft = pointTopLeft.plus(
                    dX = -pointOfRotation.x,
                    dY = -pointOfRotation.y,
                ),
                size = size,
                measure = measure,
                lineWidth = lineWidth,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        measure: Measure<Double, Double>,
        lineWidth: Double,
        direction: Double,
        pointOfRotation: Point
    ) {
        GLUtil.onMatrix {
            GLUtil.translated(point = pointOfRotation, measure = measure)
            GL11.glRotated(Math.toDegrees(direction), 0.0, 0.0, 1.0)
            drawRectangle(
                color = color,
                pointTopLeft = pointTopLeft.plus(
                    dX = -pointOfRotation.x,
                    dY = -pointOfRotation.y,
                ),
                size = size,
                measure = measure,
                lineWidth = lineWidth,
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

    private fun vertexOf(
        start: Point,
        finish: Point,
        lineWidth: Double,
        measure: Measure<Double, Double>,
    ) {
        val angle = angleOf(start, finish)
        GLUtil.vertexOfMoved(start, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, measure = measure)
        GLUtil.vertexOfMoved(start, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, measure = measure)
        GLUtil.vertexOfMoved(finish, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, measure = measure)
        GLUtil.vertexOfMoved(finish, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, measure = measure)
    }

    private fun vertexOf(
        start: Point,
        finish: Point,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        val angle = angleOf(start, finish)
        GLUtil.vertexOfMoved(start, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, offset = offset, measure = measure)
        GLUtil.vertexOfMoved(start, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, offset = offset, measure = measure)
        GLUtil.vertexOfMoved(finish, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2, offset = offset, measure = measure)
        GLUtil.vertexOfMoved(finish, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2, offset = offset, measure = measure)
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

    override fun drawCircle(
        borderColor: Color,
        fillColor: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>
    ) {
        val points = (0..edgeCount).map {
            val radians = it * 2 * kotlin.math.PI / edgeCount
            pointCenter.plus(
                dX = kotlin.math.cos(radians) * radius,
                dY = kotlin.math.sin(radians) * radius,
            )
        }
        GL11.glLineWidth(1f)
        GLUtil.colorOf(borderColor)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            val fStart = points[0]
            val fFinish = points[1]
            vertexOf(start = fStart, finish = fFinish, lineWidth = lineWidth, offset = offset, measure = measure)
            for (i in 2 until points.size) {
                vertexOf(start = points[i-1], finish = points[i], lineWidth = lineWidth, offset = offset, measure = measure)
            }
            GLUtil.vertexOfMoved(fStart, length = lineWidth / 2, angle = angleOf(fStart, fFinish) - kotlin.math.PI / 2, offset = offset, measure = measure)
        }
        GLUtil.colorOf(fillColor)
        GLUtil.transaction(GL11.GL_POLYGON) {
            points.forEach {
                GLUtil.vertexOf(it, offset = offset, measure = measure)
            }
        }
    }
}
