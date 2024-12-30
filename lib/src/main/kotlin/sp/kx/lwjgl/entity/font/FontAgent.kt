package sp.kx.lwjgl.entity.font

@Deprecated(message = "replace with sp.kx.lwjgl.drawer.TextDrawer")
interface FontAgent {
    fun getTextWidth(info: FontInfo, text: CharSequence): Double
}
