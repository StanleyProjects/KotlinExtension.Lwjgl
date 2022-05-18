package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.engine.MutableEngineProperty
import sp.kx.lwjgl.entity.font.FontAgent

sealed interface Engine {
    val input: EngineInputState
    val property: EngineProperty
    val fontAgent: FontAgent
}

internal class EngineImpl(
    override val input: EngineInputState,
    override val property: MutableEngineProperty,
    override val fontAgent: FontAgent
) : Engine
