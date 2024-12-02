package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.provider.Times
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Engine.passed(
    button: KeyboardButton,
    min: Duration,
    target: Duration = 1.seconds,
): Boolean {
    return Times.passed(
        start = input.keyboard.whenPressed(button = button) ?: return false,
        min = min,
        finish = property.time.b,
        target = target,
    )
}

fun Engine.progress(
    button: KeyboardButton,
    min: Duration,
    target: Duration = 1.seconds,
): Double {
    return Times.progress(
        start = input.keyboard.whenPressed(button = button) ?: return 0.0,
        min = min,
        finish = property.time.b,
        target = target,
    )
}
