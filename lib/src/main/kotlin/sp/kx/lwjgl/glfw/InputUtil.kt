package sp.kx.lwjgl.glfw

import org.lwjgl.glfw.GLFW
import sp.kx.lwjgl.engine.input.Joystick
import sp.kx.lwjgl.engine.input.JoystickMapper
import sp.kx.lwjgl.engine.input.JoystickMapping
import sp.kx.lwjgl.entity.input.GLFWJoystick
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.util.toArray

fun Int.toPressedOrNull(): Boolean? {
    return when (this) {
        GLFW.GLFW_RELEASE -> false
        GLFW.GLFW_PRESS -> true
        else -> null
    }
}

fun Int.toKeyboardButtonOrNull(): KeyboardButton? {
    return when (this) {
        GLFW.GLFW_KEY_ESCAPE -> KeyboardButton.ESCAPE
        //
        GLFW.GLFW_KEY_Q -> KeyboardButton.Q
        GLFW.GLFW_KEY_W -> KeyboardButton.W
        GLFW.GLFW_KEY_E -> KeyboardButton.E
        GLFW.GLFW_KEY_R -> KeyboardButton.R
        GLFW.GLFW_KEY_T -> KeyboardButton.T
        GLFW.GLFW_KEY_Y -> KeyboardButton.Y
        GLFW.GLFW_KEY_U -> KeyboardButton.U
        GLFW.GLFW_KEY_I -> KeyboardButton.I
        GLFW.GLFW_KEY_O -> KeyboardButton.O
        GLFW.GLFW_KEY_P -> KeyboardButton.P
        //
        GLFW.GLFW_KEY_A -> KeyboardButton.A
        GLFW.GLFW_KEY_S -> KeyboardButton.S
        GLFW.GLFW_KEY_D -> KeyboardButton.D
        GLFW.GLFW_KEY_F -> KeyboardButton.F
        GLFW.GLFW_KEY_G -> KeyboardButton.G
        GLFW.GLFW_KEY_H -> KeyboardButton.H
        GLFW.GLFW_KEY_J -> KeyboardButton.J
        GLFW.GLFW_KEY_K -> KeyboardButton.K
        GLFW.GLFW_KEY_L -> KeyboardButton.L
        //
        GLFW.GLFW_KEY_Z -> KeyboardButton.Z
        GLFW.GLFW_KEY_X -> KeyboardButton.X
        GLFW.GLFW_KEY_C -> KeyboardButton.C
        GLFW.GLFW_KEY_V -> KeyboardButton.V
        GLFW.GLFW_KEY_B -> KeyboardButton.B
        GLFW.GLFW_KEY_N -> KeyboardButton.N
        GLFW.GLFW_KEY_M -> KeyboardButton.M
        //
        else -> null
    }
}

/*
fun Key.toInt(): Int {
    return when (this) {
        Key.ESCAPE -> GLFW.GLFW_KEY_ESCAPE
        //
        Key.Q -> GLFW.GLFW_KEY_Q
        Key.W -> GLFW.GLFW_KEY_W
        Key.E -> GLFW.GLFW_KEY_E
        Key.R -> GLFW.GLFW_KEY_R
        Key.T -> GLFW.GLFW_KEY_T
        Key.Y -> GLFW.GLFW_KEY_Y
        Key.U -> GLFW.GLFW_KEY_U
        Key.I -> GLFW.GLFW_KEY_I
        Key.O -> GLFW.GLFW_KEY_O
        Key.P -> GLFW.GLFW_KEY_P
        Key.A -> GLFW.GLFW_KEY_A
        Key.S -> GLFW.GLFW_KEY_S
        Key.D -> GLFW.GLFW_KEY_D
        Key.F -> GLFW.GLFW_KEY_F
        Key.G -> GLFW.GLFW_KEY_G
        Key.H -> GLFW.GLFW_KEY_H
        Key.J -> GLFW.GLFW_KEY_J
        Key.K -> GLFW.GLFW_KEY_K
        Key.L -> GLFW.GLFW_KEY_L
        Key.Z -> GLFW.GLFW_KEY_Z
        Key.X -> GLFW.GLFW_KEY_X
        Key.C -> GLFW.GLFW_KEY_C
        Key.V -> GLFW.GLFW_KEY_V
        Key.B -> GLFW.GLFW_KEY_B
        Key.N -> GLFW.GLFW_KEY_N
        Key.M -> GLFW.GLFW_KEY_M
    }
}
*/

internal fun JoystickMapper.getJoystickMappingOrNull(joystick: GLFWJoystick): JoystickMapping? {
    return map(guid = joystick.guid, buttons = joystick.buttons, axes = joystick.axes)
}

fun JoystickMapper.getJoystickMappingOrNull(id: Int): JoystickMapping? {
    val isPresent = GLFW.glfwJoystickPresent(id)
//    println("Joystick $id is present: $isPresent")
    if (!isPresent) return null
    val GUID = GLFW.glfwGetJoystickGUID(id)
    if (GUID.isNullOrEmpty()) return null
    println("Joystick $id GUID \"$GUID\"")
//    val name = GLFW.glfwGetJoystickName(id)
//    if (name.isNullOrEmpty()) return null
//    println("Joystick $id name \"$name\"")
//    val gamepadName = GLFW.glfwGetGamepadName(id)
//    println("Joystick $id gamepad name \"$gamepadName\"")
    val buttons = GLFW.glfwGetJoystickButtons(id)?.toArray() ?: return null
    val axes = GLFW.glfwGetJoystickAxes(id)?.toArray() ?: return null
    //
    val indexes = Array<String>(buttons.size) { index ->
        String.format("%2d", index)
    }
    println("""
        -
        ------- ${indexes.toList()}
        buttons ${buttons.map { if (it.toInt() == 1) " +" else "_ "}}
        axes ${axes.toList()}
        -
    """.trimIndent())
    //
    return map(guid = GUID, buttons = buttons, axes = axes)
}
