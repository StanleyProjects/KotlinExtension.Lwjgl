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

internal object GLPolygonDrawer : PolygonDrawer {
    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            GLUtil.vertexOf(pointTopLeft = pointTopLeft, size = size)
        }
    }

    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size, offset: Offset) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            GLUtil.vertexOf(pointTopLeft = pointTopLeft, size = size, offset = offset)
        }
    }

    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size, measure: Measure<Double, Double>) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            GLUtil.vertexOf(pointTopLeft = pointTopLeft, size = size, measure = measure)
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            GLUtil.vertexOf(pointTopLeft = pointTopLeft, size = size, offset = offset, measure = measure)
        }
    }

    // lineWidth

    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size, lineWidth: Double) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOfMoved(
                pointTopLeft = pointTopLeft,
                size = size,
                lineWidth = lineWidth,
            )
        }
    }

    override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size, lineWidth: Double, offset: Offset) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOfMoved(
                pointTopLeft = pointTopLeft,
                size = size,
                lineWidth = lineWidth,
                offset = offset,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        measure: Measure<Double, Double>,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOfMoved(
                pointTopLeft = pointTopLeft,
                size = size,
                lineWidth = lineWidth,
                measure = measure,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOfMoved(
                pointTopLeft = pointTopLeft,
                size = size,
                lineWidth = lineWidth,
                offset = offset,
                measure = measure,
            )
        }
    }

    // border

    override fun drawRectangle(
        borderColor: Color,
        fillColor: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(fillColor)
        GLUtil.transaction(GL11.GL_POLYGON) {
            GLUtil.vertexOf(pointTopLeft = pointTopLeft, size = size)
        }
        GLUtil.colorOf(borderColor)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOfMoved(
                pointTopLeft = pointTopLeft,
                size = size,
                lineWidth = lineWidth,
            )
        }
    }

    override fun drawRectangle(
        borderColor: Color,
        fillColor: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        offset: Offset,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(fillColor)
        GLUtil.transaction(GL11.GL_POLYGON) {
            GLUtil.vertexOf(pointTopLeft = pointTopLeft, size = size, offset = offset)
        }
        GLUtil.colorOf(borderColor)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOfMoved(
                pointTopLeft = pointTopLeft,
                size = size,
                lineWidth = lineWidth,
                offset = offset,
            )
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
        GL11.glLineWidth(1f)
        GLUtil.colorOf(fillColor)
        GLUtil.transaction(GL11.GL_POLYGON) {
            GLUtil.vertexOf(pointTopLeft = pointTopLeft, size = size, measure = measure)
        }
        GLUtil.colorOf(borderColor)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOfMoved(
                pointTopLeft = pointTopLeft,
                size = size,
                lineWidth = lineWidth,
                measure = measure,
            )
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
        GL11.glLineWidth(1f)
        GLUtil.colorOf(fillColor)
        GLUtil.transaction(GL11.GL_POLYGON) {
            GLUtil.vertexOf(pointTopLeft = pointTopLeft, size = size, offset = offset, measure = measure)
        }
        GLUtil.colorOf(borderColor)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            GLUtil.vertexOfMoved(
                pointTopLeft = pointTopLeft,
                size = size,
                lineWidth = lineWidth,
                offset = offset,
                measure = measure,
            )
        }
    }

    // pointOfRotation

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
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
        direction: Double,
        pointOfRotation: Point,
        offset: Offset,
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
        direction: Double,
        pointOfRotation: Point,
        measure: Measure<Double, Double>,
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
        direction: Double,
        pointOfRotation: Point,
        offset: Offset,
        measure: Measure<Double, Double>,
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
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        lineWidth: Double,
    ) {
        GLUtil.onMatrix {
            GLUtil.translated(point = pointOfRotation)
            GL11.glRotated(Math.toDegrees(direction), 0.0, 0.0, 1.0)
            drawRectangle(
                color = color,
                pointTopLeft = pointTopLeft.plus(
                    dX = -pointOfRotation.x,
                    dY = -pointOfRotation.y,
                ),
                size = size,
                lineWidth = lineWidth,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        lineWidth: Double,
        offset: Offset,
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
                lineWidth = lineWidth,
            )
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        lineWidth: Double,
        measure: Measure<Double, Double>,
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

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
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

    @Deprecated(message = "sp.kx.lwjgl.opengl.GLUtil.vertexOf")
    private fun vertexOf(start: Point, finish: Point, lineWidth: Double) {
        val angle = angleOf(start, finish)
        GLUtil.vertexOfMoved(start, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2)
        GLUtil.vertexOfMoved(start, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2)
        GLUtil.vertexOfMoved(finish, length = lineWidth / 2, angle = angle - kotlin.math.PI / 2)
        GLUtil.vertexOfMoved(finish, length = lineWidth / 2, angle = angle + kotlin.math.PI / 2)
    }

    @Deprecated(message = "sp.kx.lwjgl.opengl.GLUtil.vertexOf")
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

    // circle

    override fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0..edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                GLUtil.vertexOf(
                    first = pointCenter.x + kotlin.math.cos(radians) * radius,
                    second = pointCenter.y + kotlin.math.sin(radians) * radius,
                )
            }
        }
    }

    override fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0..edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                GLUtil.vertexOf(
                    x = pointCenter.x + kotlin.math.cos(radians) * radius,
                    y = pointCenter.y + kotlin.math.sin(radians) * radius,
                    offset = offset,
                )
            }
        }
    }

    override fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        measure: Measure<Double, Double>,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0..edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                GLUtil.vertexOf(
                    x = pointCenter.x + kotlin.math.cos(radians) * radius,
                    y = pointCenter.y + kotlin.math.sin(radians) * radius,
                    measure = measure,
                )
            }
        }
    }

    override fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0..edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                GLUtil.vertexOf(
                    x = pointCenter.x + kotlin.math.cos(radians) * radius,
                    y = pointCenter.y + kotlin.math.sin(radians) * radius,
                    offset = offset,
                    measure = measure,
                )
            }
        }
    }

    override fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Double,
    ) {
        if (edgeCount < 3) TODO()
        GL11.glLineWidth(1f)
        GLUtil.colorOf(color)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            for (index in 0 until edgeCount) {
                val startRadians = index * 2 * kotlin.math.PI / edgeCount
                val startPoint = pointCenter.plus(
                    dX = kotlin.math.cos(startRadians) * radius,
                    dY = kotlin.math.sin(startRadians) * radius,
                )
                val finishRadians = (index + 1) * 2 * kotlin.math.PI / edgeCount
                val finishPoint = pointCenter.plus(
                    dX = kotlin.math.cos(finishRadians) * radius,
                    dY = kotlin.math.sin(finishRadians) * radius,
                )
                vertexOf(start = startPoint, finish = finishPoint, lineWidth = lineWidth)
            }
            val startRadians = (edgeCount - 1) * 2 * kotlin.math.PI / edgeCount
            val startPoint = pointCenter.plus(
                dX = kotlin.math.cos(startRadians) * radius,
                dY = kotlin.math.sin(startRadians) * radius,
            )
            val finishRadians = edgeCount * 2 * kotlin.math.PI / edgeCount
            val finishPoint = pointCenter.plus(
                dX = kotlin.math.cos(finishRadians) * radius,
                dY = kotlin.math.sin(finishRadians) * radius,
            )
            vertexOf(start = startPoint, finish = finishPoint, lineWidth = lineWidth)
            GLUtil.vertexOfMoved(
                pointCenter.plus(
                    dX = kotlin.math.cos(0.0) * radius,
                    dY = kotlin.math.sin(0.0) * radius,
                ),
                length = lineWidth / 2,
                angle = kotlin.math.PI / edgeCount - kotlin.math.PI / 2,
            )
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
        measure: Measure<Double, Double>,
    ) {
        if (edgeCount < 3) TODO()
        GL11.glLineWidth(1f)
        GLUtil.colorOf(fillColor)
        GLUtil.transaction(GL11.GL_POLYGON) {
            for (index in 0..edgeCount) {
                val radians = index * 2 * kotlin.math.PI / edgeCount
                GLUtil.vertexOf(
                    x = pointCenter.x + kotlin.math.cos(radians) * radius,
                    y = pointCenter.y + kotlin.math.sin(radians) * radius,
                    offset = offset,
                    measure = measure,
                )
            }
        }
        GLUtil.colorOf(borderColor)
        GLUtil.transaction(GL11.GL_TRIANGLE_STRIP) {
            for (index in 0 until edgeCount) {
                val startRadians = index * 2 * kotlin.math.PI / edgeCount
                val startPoint = pointCenter.plus(
                    dX = kotlin.math.cos(startRadians) * radius,
                    dY = kotlin.math.sin(startRadians) * radius,
                )
                val finishRadians = (index + 1) * 2 * kotlin.math.PI / edgeCount
                val finishPoint = pointCenter.plus(
                    dX = kotlin.math.cos(finishRadians) * radius,
                    dY = kotlin.math.sin(finishRadians) * radius,
                )
                vertexOf(start = startPoint, finish = finishPoint, lineWidth = lineWidth, offset = offset, measure = measure)
            }
            val startRadians = (edgeCount - 1) * 2 * kotlin.math.PI / edgeCount
            val startPoint = pointCenter.plus(
                dX = kotlin.math.cos(startRadians) * radius,
                dY = kotlin.math.sin(startRadians) * radius,
            )
            val finishRadians = edgeCount * 2 * kotlin.math.PI / edgeCount
            val finishPoint = pointCenter.plus(
                dX = kotlin.math.cos(finishRadians) * radius,
                dY = kotlin.math.sin(finishRadians) * radius,
            )
            vertexOf(start = startPoint, finish = finishPoint, lineWidth = lineWidth, offset = offset, measure = measure)
            GLUtil.vertexOfMoved(
                pointCenter.plus(
                    dX = kotlin.math.cos(0.0) * radius,
                    dY = kotlin.math.sin(0.0) * radius,
                ),
                length = lineWidth / 2,
                angle = kotlin.math.PI / edgeCount - kotlin.math.PI / 2,
                offset = offset,
                measure = measure,
            )
        }
    }
}
