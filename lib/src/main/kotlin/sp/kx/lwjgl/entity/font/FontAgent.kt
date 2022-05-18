package sp.kx.lwjgl.entity.font

interface FontAgent {
    fun getTextWidth(info: FontInfo, text: CharSequence): Double
}
