package sp.kx.lwjgl.engine

import org.lwjgl.glfw.GLFW
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.glfw.WindowUtil

object Engine {
    fun run(logic: EngineLogic) {
        var timeRenderLast = 0.0
        WindowUtil.loopWindow(
            size = size(640, 480),
            title = "Engine",
            onKeyCallback = GLFWUtil.onKeyCallback { _, key: Int, scanCode: Int, action: Int, _ ->
                println("on -> key callback: $key $scanCode $action")
            },
            onWindowCloseCallback = {
                // todo
            },
            onRender = { windowId, canvas ->
                val pictureSize = GLFWUtil.getWindowSize(windowId)
                val timeRenderNow = System.nanoTime().toDouble()
                logic.onRender(
                    canvas = canvas,
                    engineProperty = engineProperty(
                        timeLast = timeRenderLast,
                        timeNow = timeRenderNow,
                        pictureSize = pictureSize
                    )
                )
                timeRenderLast = timeRenderNow
                if (logic.shouldEngineStop()) {
                    GLFW.glfwSetWindowShouldClose(windowId, true)
                }
            },
            onPreLoop = { windowId ->
                // todo
            },
            onPostLoop = {
                // todo
            },
        )
    }
}
