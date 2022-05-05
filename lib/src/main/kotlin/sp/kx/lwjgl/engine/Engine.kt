package sp.kx.lwjgl.engine

import org.lwjgl.glfw.GLFW
import sp.kx.lwjgl.entity.engine.MutableEngineProperty
import sp.kx.lwjgl.entity.input.Key
import sp.kx.lwjgl.entity.input.KeyState
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.glfw.WindowUtil
import sp.kx.lwjgl.glfw.toKeyOrNull
import sp.kx.lwjgl.glfw.toKeyStateOrNull

sealed interface Engine {
    private class EngineImpl(override val input: EngineInputState, override val property: MutableEngineProperty) : Engine

    companion object {
        fun run(supplier: (Engine) -> EngineLogic) {
            val size = size(640, 480)
            val keys = mutableMapOf<Key, KeyState>()
            val keyboard = object : EngineInputState.Keyboard {
                override fun getState(key: Key): KeyState {
                    return keys[key] ?: KeyState.RELEASE
                }
            }
            val engine = EngineImpl(
                input = EngineInputState(keyboard),
                property = MutableEngineProperty(
                    timeLast = 0.0,
                    timeNow = 0.0,
                    pictureSize = size
                )
            )
            val logic = supplier(engine)
            WindowUtil.loopWindow(
                size = size,
                title = "Engine",
                onKeyCallback = GLFWUtil.onKeyCallback { _, key: Int, scanCode: Int, action: Int, _ ->
                    println("on -> key callback: $key $scanCode $action")
                    val kk = key.toKeyOrNull()
                    if (kk != null) {
                        val state = action.toKeyStateOrNull()
                        if (state != null) {
                            keys[kk] = state
                            logic.inputCallback.onKey(kk, state)
                        }
                    }
                },
                onWindowCloseCallback = {
                    // todo
                },
                onRender = { windowId, canvas ->
                    val pictureSize = GLFWUtil.getWindowSize(windowId)
                    val timeRenderNow = System.nanoTime().toDouble()
                    engine.property.timeNow = timeRenderNow
                    engine.property.pictureSize = pictureSize
                    logic.onRender(canvas = canvas)
                    engine.property.timeLast = timeRenderNow
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

    val input: EngineInputState
    val property: EngineProperty
}
