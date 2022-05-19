package sp.service.sample.util

import sp.kx.lwjgl.engine.input.JoystickMapping
import sp.kx.lwjgl.entity.input.JoystickAxis
import sp.kx.lwjgl.entity.input.JoystickButton


object Dualshock4JoystickMapping : JoystickMapping {
    override fun getIndex(button: JoystickButton): Int {
        return when (button) {
            JoystickButton.A -> 1
            JoystickButton.B -> 2
            JoystickButton.X -> 0
            JoystickButton.Y -> 3
            JoystickButton.LEFT_BUMPER -> 4
            JoystickButton.RIGHT_BUMPER -> 5
            JoystickButton.BACK -> 8
            JoystickButton.START -> 9
            JoystickButton.GUIDE -> 12
            JoystickButton.LEFT_THUMB -> 10
            JoystickButton.RIGHT_THUMB -> 11
            JoystickButton.DPAD_UP -> 14
            JoystickButton.DPAD_RIGHT -> 15
            JoystickButton.DPAD_DOWN -> 16
            JoystickButton.DPAD_LEFT -> 17
        }
    }

    override fun getIndex(axis: JoystickAxis): Int {
        return when (axis) {
            JoystickAxis.LEFT_X -> 0
            JoystickAxis.RIGHT_X -> 2
            JoystickAxis.LEFT_Y -> 1
            JoystickAxis.RIGHT_Y -> 5
            JoystickAxis.LEFT_TRIGGER -> 3
            JoystickAxis.RIGHT_TRIGGER -> 4
        }
    }
}
