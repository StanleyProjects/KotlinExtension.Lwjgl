package sp.kx.lwjgl.glfw

import org.lwjgl.glfw.GLFW
import sp.kx.lwjgl.entity.input.KeyboardButton

fun Int.toPressedOrNull(): Boolean? {
    return when (this) {
        GLFW.GLFW_RELEASE -> false
        GLFW.GLFW_PRESS -> true
        else -> null
    }
}

fun Int.toKeyboardButtonOrNull(): KeyboardButton? {
    return when (this) {
        GLFW.GLFW_KEY_ENTER -> KeyboardButton.Enter
        GLFW.GLFW_KEY_ESCAPE -> KeyboardButton.Escape
        GLFW.GLFW_KEY_TAB -> KeyboardButton.Tab
        GLFW.GLFW_KEY_SPACE -> KeyboardButton.Space
        //
        GLFW.GLFW_KEY_UP -> KeyboardButton.Up
        GLFW.GLFW_KEY_DOWN -> KeyboardButton.Down
        GLFW.GLFW_KEY_LEFT -> KeyboardButton.Left
        GLFW.GLFW_KEY_RIGHT -> KeyboardButton.Right
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
        GLFW.GLFW_KEY_LEFT_SHIFT -> KeyboardButton.Shift
        GLFW.GLFW_KEY_LEFT_CONTROL -> KeyboardButton.Control
        GLFW.GLFW_KEY_LEFT_ALT -> KeyboardButton.Alt
        GLFW.GLFW_KEY_LEFT_SUPER -> KeyboardButton.Super
        //
        GLFW.GLFW_KEY_EQUAL -> KeyboardButton.Equal
        GLFW.GLFW_KEY_MINUS -> KeyboardButton.Minus
        GLFW.GLFW_KEY_BACKSPACE -> KeyboardButton.Backspace
        //
        GLFW.GLFW_KEY_0 -> KeyboardButton.Number0
        GLFW.GLFW_KEY_1 -> KeyboardButton.Number1
        GLFW.GLFW_KEY_2 -> KeyboardButton.Number2
        GLFW.GLFW_KEY_3 -> KeyboardButton.Number3
        GLFW.GLFW_KEY_4 -> KeyboardButton.Number4
        GLFW.GLFW_KEY_5 -> KeyboardButton.Number5
        GLFW.GLFW_KEY_6 -> KeyboardButton.Number6
        GLFW.GLFW_KEY_7 -> KeyboardButton.Number7
        GLFW.GLFW_KEY_8 -> KeyboardButton.Number8
        GLFW.GLFW_KEY_9 -> KeyboardButton.Number9
        //
        else -> null
    }
}
