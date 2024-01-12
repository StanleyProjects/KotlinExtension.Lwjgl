package sp.kx.lwjgl.stb

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTAlignedQuad
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.font.FontDrawer
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.lwjgl.system.use
import sp.kx.math.Point

internal class STBFontDrawer(private val storage: STBFontStorage) : FontDrawer {
    companion object {
        private fun STBTTAlignedQuad.draw() {
            GL11.glTexCoord2f(s0(), t0())
            GLUtil.vertexOf(x0(), y0())
            GL11.glTexCoord2f(s1(), t0())
            GLUtil.vertexOf(x1(), y0())
            GL11.glTexCoord2f(s1(), t1())
            GLUtil.vertexOf(x1(), y1())
            GL11.glTexCoord2f(s0(), t1())
            GLUtil.vertexOf(x0(), y1())
        }
    }

    private fun drawText(
        info: STBFontInfo,
        xTopLeft: Double,
        yTopLeft: Double,
        color: Color,
        text: CharSequence,
    ) {
        val fontHeight = info.metrics.ascent - info.metrics.descent
        val xBuffer = BufferUtils.createFloatBuffer(1)
        val yBuffer = BufferUtils.createFloatBuffer(1)
        xBuffer.put(0, xTopLeft.toFloat())
        yBuffer.put(0, (yTopLeft + info.metrics.ascent).toFloat())

        GLUtil.enabled(GL11.GL_TEXTURE_2D) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, info.textureId)

            GLUtil.colorOf(color)

            STBTTAlignedQuad.malloc().use { quad ->
                GLUtil.transaction(GL11.GL_QUADS) {
                    for (char in text) {
                        if (!info.info.isAvailable(char)) {
                            println("Char #${char.code} is not available!") // todo
                            continue
                        }
                        STBUtil.getPackedQuad(
                            buffer = info.buffer,
                            fontHeight = fontHeight,
                            index = char.code,
                            xBuffer = xBuffer,
                            yBuffer = yBuffer,
                            quad = quad
                        )
                        quad.draw()
                    }
                }
            }
        }
    }

    override fun drawText(
        info: FontInfo,
        color: Color,
        xTopLeft: Double,
        yTopLeft: Double,
        text: CharSequence,
    ) {
        drawText(storage.getInfo(info), xTopLeft = xTopLeft, yTopLeft = yTopLeft, color, text)
    }
}
