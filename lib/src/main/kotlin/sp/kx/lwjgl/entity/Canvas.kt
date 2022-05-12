package sp.kx.lwjgl.entity

import sp.kx.lwjgl.entity.font.FontInfo

interface Canvas {
    fun drawPoint(color: Color, point: Point)
    fun drawText(color: Color, info: FontInfo, pointTopLeft: Point, text: CharSequence)
}
