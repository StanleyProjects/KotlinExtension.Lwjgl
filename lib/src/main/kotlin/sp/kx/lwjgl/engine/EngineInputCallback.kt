package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.input.KeyboardButton

interface EngineInputCallback {
    fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean)
    fun onJoystick(
        number: Int,
        id: String,
        name: String,
        buttons: ByteArray,
        axes: FloatArray,
    ) {
        // todo
        // noop
    }
}
