package sp.kx.lwjgl.drawer

import sp.kx.calculations.geometry.Vertex
import sp.kx.lwjgl.entity.Color

abstract class TextDrawer(
    private val defaultFontName: String,
) {
    abstract fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        text: CharSequence,
        x: Double,
        y: Double,
        z: Double,
    )

    fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        topLeft: Vertex,
        text: CharSequence,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = fontHeight,
            text = text,
            x = topLeft.x,
            y = topLeft.y,
            z = topLeft.z,
        )
    }

    abstract fun getTextWidth(
        fontHeight: Double,
        text: CharSequence,
        fontName: String = defaultFontName,
    ): Double
}
