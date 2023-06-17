package sp.kx.lwjgl.glfw

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFWWindowCloseCallbackI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.font.FontDrawer
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.lwjgl.system.checked
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.Vector
import sp.kx.math.plus
import sp.kx.math.pointOf
import java.io.PrintStream

object WindowUtil {
    fun createWindow(
        errorPrintStream: PrintStream,
        isVisible: Boolean,
        isResizable: Boolean,
        monitorIdSupplier: () -> Long,
        size: Size,
        title: String,
        onKeyCallback: GLFWKeyCallback,
//        onJoystickCallback: GLFWJoystickCallback,
        onWindowCloseCallback: GLFWWindowCloseCallbackI
    ): Long {
        GLFWErrorCallback.createPrint(errorPrintStream).set()
        check(GLFW.glfwInit()) { "Unable to initialize GLFW!" }
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, isVisible.toGLFWInt())
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, isResizable.toGLFWInt())
        val monitorId = monitorIdSupplier().checked { "Monitor id is null!" }
        val monitorSize = GLFWUtil.getMonitorSize(monitorId)
        //
        val windowId = GLFWUtil.createWindow(size, title).checked { "Window id is null!" }
        val xPosition = (monitorSize.width - size.width) / 2
        val yPosition = (monitorSize.height- size.height) / 2
        GLFW.glfwSetWindowPos(
            windowId,
            xPosition.toInt(),
            yPosition.toInt()
        )
        //
        GLFW.glfwMakeContextCurrent(windowId)
        GL.createCapabilities()
        GLFW.glfwSwapInterval(1)
        GLFW.glfwSetKeyCallback(windowId, onKeyCallback)
//        GLFW.glfwSetJoystickCallback(onJoystickCallback) // This is called when a joystick is connected to or disconnected from the system.
        GLFW.glfwSetWindowCloseCallback(windowId, onWindowCloseCallback)
        return windowId
    }

    private class WindowCanvas(private val fontDrawer: FontDrawer) : Canvas {

        override fun drawPoint(color: Color, point: Point) {
            GLUtil.colorOf(color)
            GLUtil.transaction(GL11.GL_POINTS) {
                GLUtil.vertexOf(point)
            }
        }

        override fun drawText(color: Color, info: FontInfo, pointTopLeft: Point, text: CharSequence) {
            fontDrawer.drawText(
                info = info,
                pointTopLeft = pointTopLeft,
                color = color,
                text = text
            )
        }

        override fun drawLineLoop(color: Color, points: Iterable<Point>, lineWidth: Float) {
            GL11.glLineWidth(lineWidth)
            GLUtil.colorOf(color)
            GLUtil.transaction(GL11.GL_LINE_LOOP) {
                points.forEach {
                    GLUtil.vertexOf(it)
                }
            }
        }

        override fun drawLine(color: Color, vector: Vector, lineWidth: Float) {
            GL11.glLineWidth(lineWidth)
            GLUtil.colorOf(color)
            GLUtil.transaction(GL11.GL_LINE_STRIP) {
                GLUtil.vertexOf(vector.start)
                GLUtil.vertexOf(vector.finish)
            }
        }

        override fun drawRectangle(color: Color, pointTopLeft: Point, size: Size, lineWidth: Float) {
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
            drawLineLoop(
                color = color,
                points = points,
                lineWidth = lineWidth
            )
        }

        override fun drawRectangle(
            color: Color,
            pointTopLeft: Point,
            size: Size,
            lineWidth: Float,
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
                    lineWidth = lineWidth
                )
            }
        }
    }

    private fun onPreRender(windowId: Long) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        GLFW.glfwPollEvents()

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val size = GLFWUtil.getWindowSize(windowId)

        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glLoadIdentity()
        GLUtil.ortho(
            right = size.width,
            bottom = size.height
        )
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glLoadIdentity()
    }

    private fun onPostRender(windowId: Long) {
        GLFW.glfwSwapBuffers(windowId)
    }

    private fun loopWindow(
        windowId: Long,
        fontDrawer: FontDrawer,
        onPreLoop: (Long) -> Unit,
        onPostLoop: () -> Unit,
        onRender: (Long, Canvas) -> Unit
    ) {
        GLUtil.clearColor(Color.BLACK)
        val canvas = WindowCanvas(fontDrawer)
        onPreLoop(windowId)
        while (!GLFW.glfwWindowShouldClose(windowId)) {
            onPreRender(windowId)
            onRender(windowId, canvas)
            onPostRender(windowId)
        }
        onPostLoop()
    }

    fun destroyWindow(windowId: Long) {
        glfwFreeCallbacks(windowId)
        GLFW.glfwDestroyWindow(windowId)
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)?.free()
    }

    fun loopWindow(
        size: Size,
        title: String,
        fontDrawer: FontDrawer,
        onKeyCallback: GLFWKeyCallback,
        onWindowCloseCallback: GLFWWindowCloseCallbackI,
        onPreLoop: (Long) -> Unit,
        onPostLoop: () -> Unit,
        onRender: (Long, Canvas) -> Unit,
        monitorIdSupplier: () -> Long = GLFW::glfwGetPrimaryMonitor,
        errorPrintStream: PrintStream = System.err,
        isVisible: Boolean = true,
        isResizable: Boolean = false,
    ) {
        val windowId = createWindow(
            errorPrintStream = errorPrintStream,
            isVisible = isVisible,
            isResizable = isResizable,
            onKeyCallback = onKeyCallback,
            onWindowCloseCallback = onWindowCloseCallback,
            size = size,
            title = title,
            monitorIdSupplier = monitorIdSupplier
        )
        GLFW.glfwShowWindow(windowId)
        loopWindow(windowId, fontDrawer, onPreLoop, onPostLoop, onRender)
        destroyWindow(windowId)
    }
}
