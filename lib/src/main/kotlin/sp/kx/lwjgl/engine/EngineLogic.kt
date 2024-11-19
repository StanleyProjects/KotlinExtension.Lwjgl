package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.Canvas

@Deprecated("rename to EngineLogics")
interface EngineLogic {
    val inputCallback: EngineInputCallback
    fun shouldEngineStop(): Boolean
    fun onRender(canvas: Canvas)
}
