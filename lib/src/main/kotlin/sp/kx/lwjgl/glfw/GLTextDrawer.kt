package sp.kx.lwjgl.glfw

import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.TextDrawer
import sp.kx.lwjgl.entity.font.FontDrawer
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.measure.Measure

internal class GLTextDrawer(
    private val fontDrawer: FontDrawer,
) : TextDrawer {
    override fun draw(color: Color, info: FontInfo, pointTopLeft: Point, text: CharSequence) {
        fontDrawer.drawText(
            info = info,
            xTopLeft = pointTopLeft.x,
            yTopLeft = pointTopLeft.y,
            color = color,
            text = text,
        )
    }

    override fun draw(color: Color, info: FontInfo, pointTopLeft: Point, offset: Offset, text: CharSequence) {
        TODO("draw:point,offset")
    }

    override fun draw(
        color: Color,
        info: FontInfo,
        pointTopLeft: Point,
        measure: Measure<Double, Double>,
        text: CharSequence
    ) {
        fontDrawer.drawText(
            info = info,
            xTopLeft = measure.transform(pointTopLeft.x),
            yTopLeft = measure.transform(pointTopLeft.y),
            color = color,
            text = text,
        )
    }

    override fun draw(
        color: Color,
        info: FontInfo,
        pointTopLeft: Point,
        offset: Offset,
        measure: Measure<Double, Double>,
        text: CharSequence
    ) {
        fontDrawer.drawText(
            info = info,
            xTopLeft = measure.transform(pointTopLeft.x + offset.dX),
            yTopLeft = measure.transform(pointTopLeft.y + offset.dY),
            color = color,
            text = text,
        )
    }
}
