package sp.kx.lwjgl.drawer

import sp.kx.calculations.geometry.Offset
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
        )
    }

    fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        topLeft: Vertex,
        text: CharSequence,
        offset: Offset,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = fontHeight,
            text = text,
            x = topLeft.x + offset.dX,
            y = topLeft.y + offset.dY,
        )
    }

    fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        topLeft: Vertex,
        text: CharSequence,
        scale: Double,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = fontHeight * scale,
            text = text,
            x = topLeft.x * scale,
            y = topLeft.y * scale,
        )
    }

    fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        topLeft: Vertex,
        text: CharSequence,
        offset: Offset,
        scale: Double,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = fontHeight * scale,
            text = text,
            x = (topLeft.x + offset.dX) * scale,
            y = (topLeft.y + offset.dY) * scale,
        )
    }

    abstract fun getTextWidth(
        fontHeight: Double,
        text: CharSequence,
        fontName: String = defaultFontName,
    ): Double

    fun getTextWidth(
        fontHeight: Double,
        text: CharSequence,
        scale: Double,
        fontName: String = defaultFontName,
    ): Double {
        return getTextWidth(
            fontName = fontName,
            fontHeight = fontHeight * scale,
            text = text,
        )
    }

    fun getTextUnits(
        fontHeight: Double,
        text: CharSequence,
        scale: Double,
        fontName: String = defaultFontName,
    ): Double {
        val width = getTextWidth(
            fontName = fontName,
            fontHeight = fontHeight * scale,
            text = text,
        )
        return width / scale
    }
}
