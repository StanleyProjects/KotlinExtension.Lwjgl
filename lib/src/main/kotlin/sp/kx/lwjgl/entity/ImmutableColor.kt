package sp.kx.lwjgl.entity

internal class ImmutableColor(
    private val values: Long,
) : Color {
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

    companion object {
        val bag = mutableMapOf<Long, Color>()
    }
}

fun colorOf(values: Long): Color {
    return ImmutableColor.bag.getOrPut(values) {
        ImmutableColor(values = values)
    }
}

fun Color.copy(
    alpha: Byte,
    red: Byte = this.red,
    green: Byte = this.green,
    blue: Byte = this.blue,
): Color {
    val values = blue.toLong().and(0xff)
        .or(green.toLong().and(0xff).shl(8))
        .or(red.toLong().and(0xff).shl(16))
        .or(alpha.toLong().and(0xff).shl(24))
    return colorOf(values = values)
}

fun Color.copy(alpha: Float): Color {
    if (alpha < 0 || alpha > 1) TODO()
    return copy(alpha = 0xff.times(alpha).toInt().toByte())
}
