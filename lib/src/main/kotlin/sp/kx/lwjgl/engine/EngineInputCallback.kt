package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.input.JoystickButton
import sp.kx.lwjgl.entity.input.KeyboardButton

interface EngineInputCallback {
    fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean)
    fun onJoystickButton(button: JoystickButton, isPressed: Boolean)
}
