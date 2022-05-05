package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.Canvas

interface EngineLogic {
    val inputCallback: EngineInputCallback
    fun shouldEngineStop(): Boolean
    fun onRender(canvas: Canvas, engineProperty: EngineProperty)
}
