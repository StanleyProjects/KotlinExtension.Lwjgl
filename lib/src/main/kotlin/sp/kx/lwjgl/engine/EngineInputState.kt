package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.input.Key
import sp.kx.lwjgl.entity.input.KeyState

class EngineInputState(val keyboard: Keyboard) {
    interface Keyboard {
        fun getState(key: Key): KeyState
    }
}
