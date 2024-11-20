package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.font.FontAgent

sealed interface Engine {
    val input: EngineInputState
    val property: EngineProperty
    val fontAgent: FontAgent
}
