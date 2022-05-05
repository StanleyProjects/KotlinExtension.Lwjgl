package sp.kx.lwjgl.entity.engine

import sp.kx.lwjgl.engine.EngineProperty
import sp.kx.lwjgl.entity.Size

class MutableEngineProperty(
    override var timeLast: Double,
    override var timeNow: Double,
    override var pictureSize: Size
) : EngineProperty
