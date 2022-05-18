package sp.kx.lwjgl.engine.input

internal class StatefulJoystickStorage {
    val joysticks: MutableMap<Int, MappedJoystick> = mutableMapOf()
}
