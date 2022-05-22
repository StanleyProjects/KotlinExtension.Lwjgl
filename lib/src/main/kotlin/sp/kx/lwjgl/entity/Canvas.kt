package sp.kx.lwjgl.entity

import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.math.foundation.entity.geometry.Point

interface Canvas {
    fun drawPoint(color: Color, point: Point)
    fun drawText(color: Color, info: FontInfo, pointTopLeft: Point, text: CharSequence)
    fun drawLineLoop(
        color: Color,
        points: Iterable<Point>,
        lineWidth: Float
    )
}
