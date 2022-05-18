package sp.kx.lwjgl.engine.input

import sp.kx.lwjgl.entity.input.KeyboardButton

interface Keyboard {
    fun isPressed(button: KeyboardButton): Boolean
}
