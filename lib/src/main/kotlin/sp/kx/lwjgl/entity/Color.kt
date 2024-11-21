package sp.kx.lwjgl.entity

interface Color {
    val alpha: Byte
    val red: Byte
    val green: Byte
    val blue: Byte

    companion object {
        val Black = colorOf(0xff000000)
        val Red = colorOf(0xffff0000)
        val Green = colorOf(0xff00ff00)
        val Blue = colorOf(0xff0000ff)
        val Yellow = colorOf(0xffffff00)
        val White = colorOf(0xffffffff)
        val Gray = colorOf(0xff888888)
    }
}

fun Color.copy(alpha: Byte): Color {
    var values = blue.toLong().and(0xff)
    values = values.or(green.toLong().and(0xff).shl(8))
    values = values.or(red.toLong().and(0xff).shl(16))
    values = values.or(alpha.toLong().and(0xff).shl(24))
    return colorOf(values = values)
}

fun Color.copy(alpha: Float): Color {
    if (alpha < 0 || alpha > 1) TODO()
    return copy(alpha = Byte.MAX_VALUE.toInt().and(0xff).times(alpha).toInt().toByte())
}
