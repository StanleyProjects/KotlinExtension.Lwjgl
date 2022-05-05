package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.Size

interface EngineProperty {
    val timeLast: Double
    val timeNow: Double
    val pictureSize: Size
}
