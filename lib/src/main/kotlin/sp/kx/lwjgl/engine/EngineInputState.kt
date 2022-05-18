package sp.kx.lwjgl.engine

import sp.kx.lwjgl.engine.input.Joystick
import sp.kx.lwjgl.engine.input.Keyboard

class EngineInputState(
    val keyboard: Keyboard,
    val joysticks: Map<Int, Joystick>
)
