package sp.kx.lwjgl.entity.engine

import sp.kx.lwjgl.engine.EngineProperty
import sp.kx.lwjgl.entity.Size

internal class MutableEngineProperty(
    override var timeLast: Double = 0.0,
    override var timeNow: Double = timeLast,
    override var pictureSize: Size
) : EngineProperty
