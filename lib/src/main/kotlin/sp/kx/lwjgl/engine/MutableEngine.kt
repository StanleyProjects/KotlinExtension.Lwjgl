package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.engine.MutableEngineProperty

internal class MutableEngine(
    override val input: EngineInputState,
    override val property: MutableEngineProperty,
) : Engine
