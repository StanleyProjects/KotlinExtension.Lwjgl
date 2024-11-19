package sp.kx.lwjgl.entity

class MutableColor(var values: Long): Color {
    override val alpha: Byte get() {
        return values.shr(24).toByte()
    }

    override val red: Byte get() {
        return values.shr(16).toByte()
    }

    override val green: Byte get() {
        return values.shr(8).toByte()
    }

    override val blue: Byte get() {
        return values.toByte()
    }
}

fun colorOf(values: Long): Color {
    return MutableColor(values = values)
}
