package sp.service.sample.util

import sp.kx.lwjgl.engine.input.JoystickMapping
import sp.kx.lwjgl.entity.input.JoystickAxis
import sp.kx.lwjgl.entity.input.JoystickButton

object XBoxSeriesJoystickMapping : JoystickMapping {
    override fun getIndex(button: JoystickButton): Int {
        return when (button) {
            JoystickButton.A -> 0
            JoystickButton.B -> 1
            JoystickButton.X -> 3
            JoystickButton.Y -> 4
            JoystickButton.LEFT_BUMPER -> 6
            JoystickButton.RIGHT_BUMPER -> 7
            JoystickButton.BACK -> 10
            JoystickButton.START -> 11
            JoystickButton.GUIDE -> 12
            JoystickButton.LEFT_THUMB -> 13
            JoystickButton.RIGHT_THUMB -> 14
            JoystickButton.DPAD_UP -> 16
            JoystickButton.DPAD_RIGHT -> 17
            JoystickButton.DPAD_DOWN -> 18
            JoystickButton.DPAD_LEFT -> 19
        }
    }

    override fun getIndex(axis: JoystickAxis): Int {
        return when (axis) {
            JoystickAxis.LEFT_X -> 0
            JoystickAxis.RIGHT_X -> 2
            JoystickAxis.LEFT_Y -> 1
            JoystickAxis.RIGHT_Y -> 3
            JoystickAxis.LEFT_TRIGGER -> 5
            JoystickAxis.RIGHT_TRIGGER -> 4
        }
    }
}
