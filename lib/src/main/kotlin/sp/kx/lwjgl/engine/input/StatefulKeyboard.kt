package sp.kx.lwjgl.engine.input

import sp.kx.lwjgl.entity.input.KeyboardButton

internal class StatefulKeyboard : Keyboard {
    // todo Boolean -> Duration
    val states = mutableMapOf<KeyboardButton, Boolean>()

    override fun isPressed(button: KeyboardButton): Boolean {
        return states[button] ?: false
    }
}
