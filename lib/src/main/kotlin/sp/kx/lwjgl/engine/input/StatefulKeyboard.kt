package sp.kx.lwjgl.engine.input

import sp.kx.lwjgl.entity.input.KeyboardButton
import java.util.EnumSet

internal class StatefulKeyboard : Keyboard {
    // todo Boolean -> Duration
    val states: MutableSet<KeyboardButton> = EnumSet.noneOf(KeyboardButton::class.java)

    override fun isPressed(button: KeyboardButton): Boolean {
        return states.contains(button)
    }
}
