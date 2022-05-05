package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.input.Key
import sp.kx.lwjgl.entity.input.KeyState

interface EngineInputCallback {
    fun onKey(key: Key, state: KeyState)
}
