package sp.kx.lwjgl.util

import org.lwjgl.glfw.GLFW
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputState
import sp.kx.lwjgl.engine.EngineLogics
import sp.kx.lwjgl.engine.MutableEngine
import sp.kx.lwjgl.engine.TimeProvider
import sp.kx.lwjgl.engine.input.StatefulKeyboard
import sp.kx.lwjgl.entity.engine.MutableEngineProperty
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.glfw.WindowUtil
import sp.kx.lwjgl.glfw.toKeyboardButtonOrNull
import sp.kx.lwjgl.glfw.toPressedOrNull
import sp.kx.lwjgl.stb.STBFontStorage
import sp.kx.lwjgl.system.SystemTimeProvider
import sp.kx.math.Size
import sp.kx.math.sizeOf

object EngineUtil {
    fun run(
        supplier: (Engine) -> EngineLogics,
        title: String = "Engine",
        size: Size? = null,
        timer: TimeProvider = SystemTimeProvider,
    ) {
        val keyboard = StatefulKeyboard()
        val fontStorage = STBFontStorage()
        val engine = MutableEngine(
            input = EngineInputState(keyboard),
            property = MutableEngineProperty(pictureSize = size ?: sizeOf(0, 0)),
            fontAgent = fontStorage.agent,
            timer = timer,
        )
        val logic = supplier(engine)
        WindowUtil.loopWindow(
            title = title,
            size = size,
            fontDrawer = fontStorage.drawer,
            onKeyCallback = GLFWUtil.onKeyCallback { _, key: Int, scanCode: Int, action: Int, _ ->
                println("on -> keyboard callback: $key $scanCode $action") // todo
                val button = key.toKeyboardButtonOrNull()
                if (button != null) {
                    val isPressed = action.toPressedOrNull()
                    if (isPressed != null) {
                        if (isPressed) {
                            keyboard.states.add(button)
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
                engine.property.time.b = engine.timer.now()
                engine.property.pictureSize = GLFWUtil.getWindowSize(windowId)
                logic.onRender(canvas = canvas)
                engine.property.time.a = engine.property.time.b
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
