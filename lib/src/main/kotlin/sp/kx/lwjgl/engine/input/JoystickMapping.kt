package sp.kx.lwjgl.engine.input

import sp.kx.lwjgl.entity.input.JoystickAxis
import sp.kx.lwjgl.entity.input.JoystickButton

interface JoystickMapping {
    fun getIndex(button: JoystickButton): Int
    fun getIndex(axis: JoystickAxis): Int
}
