package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.input.JoystickButton
import sp.kx.lwjgl.entity.input.KeyboardButton

interface EngineInputCallback {
    fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean)
    fun onJoystickButton(button: JoystickButton, isPressed: Boolean)
    fun onJoystick(guid: String, buttons: ByteArray, axes: FloatArray) {
        // todo
        // noop
    }
}
