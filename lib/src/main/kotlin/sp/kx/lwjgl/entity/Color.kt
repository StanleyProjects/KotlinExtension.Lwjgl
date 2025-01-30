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
