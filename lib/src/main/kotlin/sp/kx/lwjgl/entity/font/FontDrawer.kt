package sp.kx.lwjgl.entity.font

import sp.kx.lwjgl.entity.Color

interface FontDrawer {
    fun drawText(
        info: FontInfo,
        color: Color,
        xTopLeft: Double,
        yTopLeft: Double,
        text: CharSequence,
    )
}
