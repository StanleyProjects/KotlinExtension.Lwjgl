package sp.kx.lwjgl.system

import org.lwjgl.system.MemoryUtil

fun Long.checked(lazyMessage: () -> Any): Long {
    check(this != MemoryUtil.NULL, lazyMessage)
    return this
}
