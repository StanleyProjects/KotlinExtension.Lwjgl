package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.Canvas

interface EngineLogic {
    fun shouldEngineStop(): Boolean
    fun onRender(canvas: Canvas, engineProperty: EngineProperty)
}
