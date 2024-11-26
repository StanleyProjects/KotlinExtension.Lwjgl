package sp.kx.lwjgl.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback
import sp.kx.lwjgl.engine.input.StatefulKeyboard
import sp.kx.lwjgl.entity.engine.MutableEngineProperty
import sp.kx.lwjgl.entity.font.FontAgent
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.glfw.WindowUtil
import sp.kx.lwjgl.glfw.toKeyboardButtonOrNull
import sp.kx.lwjgl.glfw.toPressedOrNull
import sp.kx.lwjgl.stb.STBFontStorage
import sp.kx.lwjgl.system.SystemTimeProvider
import sp.kx.math.Size
import sp.kx.math.sizeOf

sealed interface Engine {
    val input: EngineInputState
    val property: EngineProperty
    val fontAgent: FontAgent
    val timer: TimeProvider

    companion object {
        fun run(
            supplier: (Engine) -> EngineLogics,
            title: String = "Engine",
            size: Size? = null,
            timer: TimeProvider = SystemTimeProvider,
        ) {
            // todo run once
            // todo logger
            // todo hide mouse
            val keyboard = StatefulKeyboard()
            val fontStorage = STBFontStorage()
            val engine = MutableEngine(
                input = EngineInputState(keyboard),
                property = MutableEngineProperty(pictureSize = size ?: sizeOf(0, 0)),
                fontAgent = fontStorage.agent,
                timer = timer,
            )
            val logics = supplier(engine)
            WindowUtil.loopWindow(
                title = title,
                size = size,
                fontDrawer = fontStorage.drawer,
                onKeyCallback = object : GLFWKeyCallback() {
                    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                        println("on -> keyboard callback: $key $scancode $action") // todo
                        val button = key.toKeyboardButtonOrNull() ?: return
                        val isPressed = action.toPressedOrNull() ?: return
                        if (isPressed) {
                            keyboard.buttons[button] = engine.timer.now()
                        } else {
                            keyboard.buttons.remove(button)
                        }
                        logics.inputCallback.onKeyboardButton(button, isPressed)
                    }
                },
                onWindowCloseCallback = {
                    // todo
                },
                onRender = { windowId, canvas ->
                    engine.property.time.b = engine.timer.now()
                    engine.property.pictureSize = GLFWUtil.getWindowSize(windowId)
                    logics.onRender(canvas = canvas)
                    engine.property.time.a = engine.property.time.b
                    if (logics.shouldEngineStop()) {
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
}
