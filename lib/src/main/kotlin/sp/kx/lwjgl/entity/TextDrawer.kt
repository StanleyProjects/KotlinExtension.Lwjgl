package sp.kx.lwjgl.entity

import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.measure.Measure

interface TextDrawer {
    fun draw(color: Color, info: FontInfo, pointTopLeft: Point, text: CharSequence)
    fun draw(color: Color, info: FontInfo, pointTopLeft: Point, offset: Offset, text: CharSequence)
    fun draw(color: Color, info: FontInfo, pointTopLeft: Point, measure: Measure<Double, Double>, text: CharSequence)
    fun draw(color: Color, info: FontInfo, pointTopLeft: Point, offset: Offset, measure: Measure<Double, Double>, text: CharSequence)
}
