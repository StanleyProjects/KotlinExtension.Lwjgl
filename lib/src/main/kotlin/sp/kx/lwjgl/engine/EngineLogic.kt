package sp.kx.lwjgl.engine

import sp.kx.lwjgl.engine.input.JoystickMapper
import sp.kx.lwjgl.entity.Canvas

interface EngineLogic {
    val joystickMapper: JoystickMapper
    val inputCallback: EngineInputCallback
    fun shouldEngineStop(): Boolean
    fun onRender(canvas: Canvas)
}
