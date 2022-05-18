package sp.kx.lwjgl.engine.input

import sp.kx.lwjgl.entity.input.GLFWJoystick
import sp.kx.lwjgl.entity.input.JoystickButton

internal class MappedJoystick(
    private val gj: GLFWJoystick,
    private val mapping: JoystickMapping
) : Joystick {
    override val guid = gj.guid

    override fun isPressed(button: JoystickButton): Boolean {
        return gj.buttons[mapping.getIndex(button)].toInt() == 1
    }
}
