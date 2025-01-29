package sp.kx.lwjgl.glfw

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFWWindowCloseCallbackI
import org.lwjgl.glfw.GLFWWindowSizeCallbackI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import sp.kx.lwjgl.drawer.PolygonDrawer
import sp.kx.lwjgl.drawer.TextDrawer
import sp.kx.lwjgl.drawer.VectorDrawer
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.ft.FTTextDrawer
import sp.kx.lwjgl.gl.GLPolygonDrawer
import sp.kx.lwjgl.gl.GLVectorDrawer
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.lwjgl.system.checked
import sp.kx.math.Point
import sp.kx.math.Size
import java.io.PrintStream

object WindowUtil {
    fun createWindow(
        errorPrintStream: PrintStream,
        monitorIdSupplier: () -> Long,
        title: String,
        size: Size?,
        onKeyCallback: GLFWKeyCallback,
        onWindowCloseCallback: GLFWWindowCloseCallbackI,
        onWindowResizeCallback: GLFWWindowSizeCallbackI,
    ): Long {
        GLFWErrorCallback.createPrint(errorPrintStream).set()
        check(GLFW.glfwInit()) { "Unable to initialize GLFW!" }
        val monitorId = monitorIdSupplier().checked { "Monitor id is null!" }
        //
        GLFW.glfwDefaultWindowHints()
        val windowId: Long
        if (size == null) {
            windowId = GLFWUtil.createWindow(title = title, monitorId = monitorId).checked { "Window id is null!" }
            val mode = GLFW.glfwGetVideoMode(monitorId) ?: error("Video mode is null!")
            val message = """
                mode:width: ${mode.width()}
                mode:height: ${mode.height()}
                mode:refresh:rate: ${mode.refreshRate()}
            """.trimIndent()
            println(message) // todo
            GLFW.glfwSetWindowMonitor(windowId, monitorId, 0, 0, mode.width(), mode.height(), GLFW.GLFW_DONT_CARE)
        } else {
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE) // todo
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE) // todo
            windowId = GLFWUtil.createWindow(title = title, size = size).checked { "Window id is null!" }
            val monitorSize = GLFWUtil.getMonitorSize(monitorId)
            val xPosition = (monitorSize.width - size.width) / 2
            val yPosition = (monitorSize.height - size.height) / 2
            GLFW.glfwSetWindowPos(
                windowId,
                xPosition.toInt(),
                yPosition.toInt(),
            )
        }
        //
        GLFW.glfwMakeContextCurrent(windowId)
        GL.createCapabilities()
        GLFW.glfwSwapInterval(1)
        GLFW.glfwSetKeyCallback(windowId, onKeyCallback)
        GLFW.glfwSetWindowCloseCallback(windowId, onWindowCloseCallback)
        GLFW.glfwSetWindowSizeCallback(windowId, onWindowResizeCallback)
        return windowId
    }

    private class WindowCanvas(defaultFontName: String) : Canvas {
        override val vectors: VectorDrawer = GLVectorDrawer
        override val polygons: PolygonDrawer = GLPolygonDrawer
        override val texts: TextDrawer = FTTextDrawer(defaultFontName = defaultFontName)

        override fun drawPoint(color: Color, point: Point) {
            GLUtil.colorOf(color)
            GLUtil.transaction(GL11.GL_POINTS) {
                GLUtil.vertexOf(point)
            }
        }

        override fun drawLineLoop(color: Color, points: Iterable<Point>, lineWidth: Float) {
            GL11.glLineWidth(1f)
            GLUtil.colorOf(color)
            GLUtil.transaction(GL11.GL_LINE_LOOP) {
                points.forEach {
                    GLUtil.vertexOf(it)
                }
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
            bottom = size.height,
        )
        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glLoadIdentity()
        GL11.glLineWidth(1f)
    }

    private fun onPostRender(windowId: Long) {
        GLFW.glfwSwapBuffers(windowId)
    }

    private fun loopWindow(
        windowId: Long,
        defaultFontName: String,
        onPreLoop: (Long) -> Unit,
        onPostLoop: () -> Unit,
        onRender: (Long, Canvas) -> Unit,
        refreshRate: Double,
    ) {
        GLUtil.clearColor(Color.Black)
        val canvas = WindowCanvas(defaultFontName = defaultFontName)
        onPreLoop(windowId)
        val timeMax = (1_000_000.0 / refreshRate).toLong()
        var timeLast = System.nanoTime() / 1_000
        while (!GLFW.glfwWindowShouldClose(windowId)) {
            val timeNow = System.nanoTime() / 1_000
            if (timeNow - timeLast < timeMax) continue
            onPreRender(windowId)
            onRender(windowId, canvas)
            GLFW.glfwSwapBuffers(windowId)
            timeLast = timeNow
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
        title: String,
        size: Size?,
        refreshRate: Double?,
        defaultFontName: String,
        onKeyCallback: GLFWKeyCallback,
        onWindowCloseCallback: GLFWWindowCloseCallbackI,
        onWindowResizeCallback: GLFWWindowSizeCallbackI,
        onPreLoop: (Long) -> Unit,
        onPostLoop: () -> Unit,
        onRender: (Long, Canvas) -> Unit,
        monitorIdSupplier: () -> Long = GLFW::glfwGetPrimaryMonitor,
        errorPrintStream: PrintStream = System.err,
    ) {
        val windowId = createWindow(
            errorPrintStream = errorPrintStream,
            onKeyCallback = onKeyCallback,
            onWindowCloseCallback = onWindowCloseCallback,
            onWindowResizeCallback = onWindowResizeCallback,
            size = size,
            title = title,
            monitorIdSupplier = monitorIdSupplier,
        )
        GLFW.glfwShowWindow(windowId)
        val monitorId = monitorIdSupplier().checked { "Monitor id is null!" }
        val videoMode = GLFW.glfwGetVideoMode(monitorId) ?: error("No video mode!")
        loopWindow(
            windowId = windowId,
            defaultFontName = defaultFontName,
            onPreLoop = onPreLoop,
            onPostLoop = onPostLoop,
            onRender = onRender,
            refreshRate = refreshRate ?: videoMode.refreshRate().toDouble(),
        )
        destroyWindow(windowId)
    }
}
