package sp.kx.lwjgl.drawer

import sp.kx.lwjgl.entity.Color
import sp.kx.math.Point

abstract class TextDrawer(
    private val defaultFontName: String,
) {
    abstract fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        pointTopLeft: Point,
        text: CharSequence,
    )

    abstract fun getTextWidth(
        fontName: String = defaultFontName,
        fontHeight: Double,
        text: CharSequence,
    ): Double
}
