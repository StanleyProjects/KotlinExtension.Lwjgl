package sp.kx.lwjgl.engine.input

interface JoystickMapper {
    fun map(guid: String, buttons: ByteArray, axes: FloatArray): JoystickMapping?
}
