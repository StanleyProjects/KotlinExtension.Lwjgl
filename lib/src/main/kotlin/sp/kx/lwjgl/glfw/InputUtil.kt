package sp.kx.lwjgl.glfw

import org.lwjgl.glfw.GLFW
import sp.kx.lwjgl.entity.input.Key
import sp.kx.lwjgl.entity.input.KeyState

fun Int.toKeyStateOrNull(): KeyState? {
    return when (this) {
        GLFW.GLFW_RELEASE -> KeyState.RELEASE
        GLFW.GLFW_PRESS -> KeyState.PRESS
        else -> null
    }
}

fun Int.toKeyOrNull(): Key? {
    return when (this) {
        GLFW.GLFW_KEY_ESCAPE -> Key.ESCAPE
        //
        GLFW.GLFW_KEY_Q -> Key.Q
        GLFW.GLFW_KEY_W -> Key.W
        GLFW.GLFW_KEY_E -> Key.E
        GLFW.GLFW_KEY_R -> Key.R
        GLFW.GLFW_KEY_T -> Key.T
        GLFW.GLFW_KEY_Y -> Key.Y
        GLFW.GLFW_KEY_U -> Key.U
        GLFW.GLFW_KEY_I -> Key.I
        GLFW.GLFW_KEY_O -> Key.O
        GLFW.GLFW_KEY_P -> Key.P
        //
        GLFW.GLFW_KEY_A -> Key.A
        GLFW.GLFW_KEY_S -> Key.S
        GLFW.GLFW_KEY_D -> Key.D
        GLFW.GLFW_KEY_F -> Key.F
        GLFW.GLFW_KEY_G -> Key.G
        GLFW.GLFW_KEY_H -> Key.H
        GLFW.GLFW_KEY_J -> Key.J
        GLFW.GLFW_KEY_K -> Key.K
        GLFW.GLFW_KEY_L -> Key.L
        //
        GLFW.GLFW_KEY_Z -> Key.Z
        GLFW.GLFW_KEY_X -> Key.X
        GLFW.GLFW_KEY_C -> Key.C
        GLFW.GLFW_KEY_V -> Key.V
        GLFW.GLFW_KEY_B -> Key.B
        GLFW.GLFW_KEY_N -> Key.N
        GLFW.GLFW_KEY_M -> Key.M
        //
        else -> null
    }
}

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
