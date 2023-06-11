package sp.kx.lwjgl.entity.font

import sp.kx.lwjgl.entity.Color
import sp.kx.math.Point

interface FontDrawer {
    fun drawText(
        info: FontInfo,
        color: Color,
        pointTopLeft: Point,
        text: CharSequence
    )
}
