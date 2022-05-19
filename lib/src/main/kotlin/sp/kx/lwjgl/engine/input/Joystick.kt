package sp.kx.lwjgl.engine.input

import sp.kx.lwjgl.entity.input.JoystickAxis
import sp.kx.lwjgl.entity.input.JoystickButton

interface Joystick {
    val guid: String

    fun isPressed(button: JoystickButton): Boolean
    fun getValue(axis: JoystickAxis): Float
}
