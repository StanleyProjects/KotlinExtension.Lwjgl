package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.engine.MutableEngineProperty
import sp.kx.lwjgl.entity.font.FontAgent
import sp.kx.lwjgl.provider.Times

internal class MutableEngine(
    override val input: EngineInputState,
    override val property: MutableEngineProperty,
    override val fontAgent: FontAgent,
) : Engine
