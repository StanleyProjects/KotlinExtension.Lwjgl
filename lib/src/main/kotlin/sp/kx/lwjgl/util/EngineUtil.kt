package sp.kx.lwjgl.util

import org.lwjgl.glfw.GLFW
import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineImpl
import sp.kx.lwjgl.engine.EngineInputState
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.input.Joystick
import sp.kx.lwjgl.engine.input.JoystickMapper
import sp.kx.lwjgl.engine.input.MappedJoystick
import sp.kx.lwjgl.engine.input.StatefulJoystickStorage
import sp.kx.lwjgl.engine.input.StatefulKeyboard
import sp.kx.lwjgl.entity.engine.MutableEngineProperty
import sp.kx.lwjgl.entity.input.GLFWJoystick
import sp.kx.lwjgl.entity.input.JoystickButton
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.glfw.WindowUtil
import sp.kx.lwjgl.glfw.getJoystickMappingOrNull
import sp.kx.lwjgl.glfw.toKeyboardButtonOrNull
import sp.kx.lwjgl.glfw.toPressedOrNull
import sp.kx.lwjgl.stb.STBFontAgent
import sp.kx.lwjgl.stb.STBFontDrawer
import sp.kx.lwjgl.stb.STBFontStorage

object EngineUtil {
    private fun getGLFWJoystickOrNull(id: Int): GLFWJoystick? {
        val isPresent = GLFW.glfwJoystickPresent(id)
        if (!isPresent) return null
        val guid = GLFW.glfwGetJoystickGUID(id)
        if (guid.isNullOrEmpty()) return null
        val buttons = GLFW.glfwGetJoystickButtons(id)?.toArray() ?: return null
        val axes = GLFW.glfwGetJoystickAxes(id)?.toArray() ?: return null
        return GLFWJoystick(guid = guid, buttons = buttons, axes = axes)
    }

    private fun StatefulJoystickStorage.update(mapper: JoystickMapper, onButton: (JoystickButton, Boolean) -> Unit) {
        for (id in GLFW.GLFW_JOYSTICK_1..GLFW.GLFW_JOYSTICK_LAST) {
            val gj = getGLFWJoystickOrNull(id)
            if (gj == null) {
                joysticks.remove(id)
                continue
            }
            val mapping = mapper.getJoystickMappingOrNull(gj)
            if (mapping == null) {
                joysticks.remove(id)
                continue
            }
            val old = joysticks[id]
            joysticks[id] = MappedJoystick(gj, mapping)
//            println("axes: " + gj.axes.toList())
            if (old == null) continue
            JoystickButton.values().forEach { button ->
                val newValue = gj.buttons[mapping.getIndex(button)].toInt() == 1
                if (old.isPressed(button) != newValue) {
                    println("on -> joystick callback: $button $newValue")
                    onButton(button, newValue)
                }
            }
        }
    }

    fun run(supplier: (Engine) -> EngineLogic) {
        val size = size(640, 480)
        val keyboard = StatefulKeyboard()
        val joystickStorage = StatefulJoystickStorage()
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,".toSet()
        val sfs = STBFontStorage(
            chars = chars,
//            bufferSize = size(2048, 2048)
        )
        val fontAgent = STBFontAgent(storage = sfs)
        val fontDrawer = STBFontDrawer(storage = sfs)
        val engine = EngineImpl(
            input = EngineInputState(keyboard, joystickStorage.joysticks),
            property = MutableEngineProperty(pictureSize = size),
            fontAgent = fontAgent
        )
        val logic = supplier(engine)
        WindowUtil.loopWindow(
            size = size,
            title = "Engine",
            fontDrawer = fontDrawer,
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
                val now = System.nanoTime().toDouble()
                engine.property.timeNow = now
                engine.property.pictureSize = GLFWUtil.getWindowSize(windowId)
                joystickStorage.update(mapper = logic.joystickMapper, onButton = logic.inputCallback::onJoystickButton)
                logic.onRender(canvas = canvas)
                engine.property.timeLast = now
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
