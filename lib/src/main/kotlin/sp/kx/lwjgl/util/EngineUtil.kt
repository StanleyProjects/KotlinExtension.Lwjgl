package sp.kx.lwjgl.util

import org.lwjgl.glfw.GLFW
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineImpl
import sp.kx.lwjgl.engine.EngineInputState
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.input.StatefulKeyboard
import sp.kx.lwjgl.entity.engine.MutableEngineProperty
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.glfw.WindowUtil
import sp.kx.lwjgl.glfw.toKeyboardButtonOrNull
import sp.kx.lwjgl.glfw.toPressedOrNull
import sp.kx.lwjgl.stb.STBFontStorage
import sp.kx.math.sizeOf
import kotlin.time.Duration.Companion.nanoseconds

object EngineUtil {
    fun run(supplier: (Engine) -> EngineLogic) {
        val size = sizeOf(width = 640.0, height = 480.0)
        val keyboard = StatefulKeyboard()
        val fontStorage = STBFontStorage()
        val engine = EngineImpl(
            input = EngineInputState(keyboard),
            property = MutableEngineProperty(pictureSize = size),
            fontAgent = fontStorage.agent
        )
        val logic = supplier(engine)
        WindowUtil.loopWindow(
            size = size,
            title = "Engine",
            fontDrawer = fontStorage.drawer,
            onKeyCallback = GLFWUtil.onKeyCallback { _, key: Int, scanCode: Int, action: Int, _ ->
                println("on -> keyboard callback: $key $scanCode $action")
                val button = key.toKeyboardButtonOrNull()
                if (button != null) {
                    val isPressed = action.toPressedOrNull()
                    if (isPressed != null) {
                        if (isPressed) {
                            keyboard.states[button] = true
                        } else {
                            keyboard.states.remove(button)
                        }
                        logic.inputCallback.onKeyboardButton(button, isPressed)
                    }
                }
            },
            onWindowCloseCallback = {
                // todo
            },
            onRender = { windowId, canvas ->
                val now = System.nanoTime().toDouble().nanoseconds
                engine.property.time.b = now
                engine.property.pictureSize = GLFWUtil.getWindowSize(windowId)
                logic.onRender(canvas = canvas)
                engine.property.time.a = now
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
