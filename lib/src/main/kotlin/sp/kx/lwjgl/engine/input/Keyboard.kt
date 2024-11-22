package sp.kx.lwjgl.engine.input

import sp.kx.lwjgl.entity.input.KeyboardButton
import kotlin.time.Duration

interface Keyboard {
    fun isPressed(button: KeyboardButton): Boolean
    fun whenPressed(button: KeyboardButton): Duration?
}
