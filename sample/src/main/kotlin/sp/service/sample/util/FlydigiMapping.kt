package sp.service.sample.util

import sp.kx.lwjgl.engine.input.JoystickMapping
import sp.kx.lwjgl.entity.input.JoystickAxis
import sp.kx.lwjgl.entity.input.JoystickButton

object FlydigiMapping {
    object Vader3Pro : JoystickMapping {
        override fun getIndex(button: JoystickButton): Int {
            return when (button) {
                JoystickButton.A -> 0
                JoystickButton.B -> 1
                JoystickButton.X -> 2
                JoystickButton.Y -> 3
                JoystickButton.LEFT_BUMPER -> 4
                JoystickButton.RIGHT_BUMPER -> 5
                JoystickButton.BACK -> 6
                JoystickButton.START -> 7
                JoystickButton.GUIDE -> 10
                JoystickButton.LEFT_THUMB -> 8
                JoystickButton.RIGHT_THUMB -> 9
                JoystickButton.DPAD_UP -> 11
                JoystickButton.DPAD_RIGHT -> 12
                JoystickButton.DPAD_DOWN -> 13
                JoystickButton.DPAD_LEFT -> 14
            }
        }

        override fun getIndex(axis: JoystickAxis): Int {
            return when (axis) {
                JoystickAxis.LEFT_X -> 0
                JoystickAxis.RIGHT_X -> 3
                JoystickAxis.LEFT_Y -> 1
                JoystickAxis.RIGHT_Y -> 4
                JoystickAxis.LEFT_TRIGGER -> 2
                JoystickAxis.RIGHT_TRIGGER -> 5
            }
        }
    }
}
