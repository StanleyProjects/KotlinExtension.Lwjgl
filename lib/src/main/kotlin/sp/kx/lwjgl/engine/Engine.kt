package sp.kx.lwjgl.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback
import sp.kx.lwjgl.engine.input.StatefulKeyboard
import sp.kx.lwjgl.entity.engine.MutableEngineProperty
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.glfw.WindowUtil
import sp.kx.lwjgl.glfw.toKeyboardButtonOrNull
import sp.kx.lwjgl.provider.SystemTimes
import sp.kx.lwjgl.provider.Times
import sp.kx.math.Size
import sp.kx.math.sizeOf

sealed interface Engine {
    val input: EngineInputState
    val property: EngineProperty

    companion object {
        fun run(
            supplier: (Engine) -> EngineLogics,
            title: String = "Engine",
            size: Size? = null,
            refreshRate: Double? = null,
            monitorIdSupplier: () -> Long = GLFW::glfwGetPrimaryMonitor,
            times: Times = SystemTimes,
            defaultFontName: String,
        ) {
            // todo run once
            // todo logger
            // todo hide mouse
            val keyboard = StatefulKeyboard()
            val engine = MutableEngine(
                input = EngineInputState(keyboard),
                property = MutableEngineProperty(pictureSize = size ?: Size.Undefined),
            )
            var logics: EngineLogics = EmptyEngineLogics
            WindowUtil.loopWindow(
                title = title,
                size = size,
                refreshRate = refreshRate,
                monitorIdSupplier = monitorIdSupplier,
                errorPrintStream = System.err,
                onWindowCloseCallback = {
                    // todo
                },
                onWindowResizeCallback = { _: Long, width, height ->
                    println("Engine: on -> window resize callback: width: $width height: $height") // todo
                    engine.property.pictureSize = sizeOf(width = width, height = height)
                },
                defaultFontName = defaultFontName,
                onPreLoop = { windowId ->
                    engine.property.launched = times.now()
                    engine.property.time.a = engine.property.launched
                    engine.property.pictureSize = GLFWUtil.getWindowSize(windowId)
                    logics = supplier(engine)
                    logics.onPreLoop()
                },
                onKeyCallback = object : GLFWKeyCallback() {
                    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                        val button = key.toKeyboardButtonOrNull() ?: return
                        val isPressed = when (action) {
                            GLFW.GLFW_PRESS -> {
                                keyboard.buttons[button] = times.now()
                                true
                            }
                            GLFW.GLFW_RELEASE -> {
                                keyboard.buttons.remove(button)
                                false
                            }
                            else -> return
                        }
                        logics.inputCallback.onKeyboardButton(button, isPressed)
                    }
                },
                onRender = { windowId, canvas ->
                    engine.property.time.b = times.now()
                    logics.onRender(canvas = canvas)
                    engine.property.time.a = engine.property.time.b
                    if (logics.shouldEngineStop()) {
                        GLFW.glfwSetWindowShouldClose(windowId, true)
                    }
                },
                onPostLoop = {
                    // todo
                },
            )
        }
    }
}
