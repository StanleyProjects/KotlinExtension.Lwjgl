package sp.kx.lwjgl.drawer

import sp.kx.lwjgl.entity.Color
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.measure.Measure

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
        pointTopLeft: Point,
        text: CharSequence,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = fontHeight,
            text = text,
            x = pointTopLeft.x,
            y = pointTopLeft.y,
        )
    }

    fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        pointTopLeft: Point,
        text: CharSequence,
        offset: Offset,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = fontHeight,
            text = text,
            x = pointTopLeft.x + offset.dX,
            y = pointTopLeft.y + offset.dY,
        )
    }

    fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        pointTopLeft: Point,
        text: CharSequence,
        measure: Measure<Double, Double>,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = measure.transform(fontHeight),
            text = text,
            x = measure.transform(pointTopLeft.x),
            y = measure.transform(pointTopLeft.y),
        )
    }

    fun draw(
        color: Color,
        fontName: String = defaultFontName,
        fontHeight: Double,
        pointTopLeft: Point,
        text: CharSequence,
        offset: Offset,
        measure: Measure<Double, Double>,
    ) {
        draw(
            color = color,
            fontName = fontName,
            fontHeight = measure.transform(fontHeight),
            text = text,
            x = measure.transform(pointTopLeft.x + offset.dX),
            y = measure.transform(pointTopLeft.y + offset.dY),
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
