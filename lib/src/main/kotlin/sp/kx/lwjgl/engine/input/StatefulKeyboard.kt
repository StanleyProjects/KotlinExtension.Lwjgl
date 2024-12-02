package sp.kx.lwjgl.engine.input

import sp.kx.lwjgl.entity.input.KeyboardButton
import java.util.EnumMap
import kotlin.time.Duration

internal class StatefulKeyboard : Keyboard {
    val buttons: MutableMap<KeyboardButton, Duration> = EnumMap(KeyboardButton::class.java)

    override fun isPressed(button: KeyboardButton): Boolean {
        return buttons.containsKey(button)
    }

    override fun whenPressed(button: KeyboardButton): Duration? {
        return buttons[button]
    }
}
