package sp.kx.lwjgl.drawer

import sp.kx.lwjgl.entity.Color
import sp.kx.math.Measure
import sp.kx.math.Offset
import sp.kx.math.Vertex

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
        measure: Measure<Double, Double>,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = measure.transform(fontHeight),
            text = text,
            x = measure.transform(topLeft.x),
            y = measure.transform(topLeft.y),
        )
    }

    fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        topLeft: Vertex,
        text: CharSequence,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = measure.transform(fontHeight),
            text = text,
            x = measure.transform(topLeft.x + offset.dX),
            y = measure.transform(topLeft.y + offset.dY),
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
        measure: Measure<Double, Double>,
        fontName: String = defaultFontName,
    ): Double {
        return getTextWidth(
            fontName = fontName,
            fontHeight = measure.transform(fontHeight),
            text = text,
        )
    }

    fun getTextUnits(
        fontHeight: Double,
        text: CharSequence,
        measure: Measure<Double, Double>,
        fontName: String = defaultFontName,
    ): Double {
        val width = getTextWidth(
            fontName = fontName,
            fontHeight = measure.transform(fontHeight),
            text = text,
        )
        return measure.units(width)
    }
}
