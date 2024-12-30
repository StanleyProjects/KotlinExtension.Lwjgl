package sp.kx.lwjgl.ft

internal class FTAtlas(
    val id: Int,
    val width: Int,
    val height: Int,
    val ascender: Int,
    val descender: Int,
    val upe: Int,
    val space: Int,
    val glyphs: Map<Int, FTGlyph>,
)
