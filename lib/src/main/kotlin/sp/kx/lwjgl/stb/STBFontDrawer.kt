package sp.kx.lwjgl.stb

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.font.FontDrawer
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.lwjgl.system.use
import sp.kx.math.foundation.entity.geometry.Point

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
//        fontHeight: Float,
        pointTopLeft: Point,
        color: Color,
        text: CharSequence
    ) {
        val x = pointTopLeft.x
//        val y = pointTopLeft.y
//        val y = pointTopLeft.y + sfi.lineHeight
//        val y = pointTopLeft.y + info.height + sfi.metrics.descent * STBTruetype.stbtt_ScaleForPixelHeight(sfi.info, info.height)
//        val scale = STBTruetype.stbtt_ScaleForPixelHeight(info.info, fontHeight)
//        val baseline = pointTopLeft.y + sfi.metrics.ascent * scale
//        val baseline = pointTopLeft.y + info.height
//        val y = pointTopLeft.y + (sfi.metrics.ascent + sfi.metrics.descent) * STBTruetype.stbtt_ScaleForPixelHeight(sfi.info, info.height)
//        val y = baseline
//        val y = pointTopLeft.y + info.metrics.ascent * scale
        val fontHeight = info.metrics.ascent - info.metrics.descent
//        val y = pointTopLeft.y + fontHeight
        val y = pointTopLeft.y + info.metrics.ascent
//        val y = pointTopLeft.y + info.metrics.ascent + info.metrics.descent
//        println("""
//            fh: $fontHeight
//            ad: ${info.metrics.ascent + info.metrics.descent}
//        """.trimIndent())

        val xBuffer = BufferUtils.createFloatBuffer(1)
        val yBuffer = BufferUtils.createFloatBuffer(1)
        xBuffer.put(0, x.toFloat())
        yBuffer.put(0, y.toFloat())

        GLUtil.enabled(GL11.GL_TEXTURE_2D) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, info.textureId)

            GLUtil.colorOf(color)

            STBTTAlignedQuad.malloc().use { quad ->
                GLUtil.transaction(GL11.GL_QUADS) {
//                    println("lh: " + sfi.lineHeight)
//                    val size = storage.getBufferSize(fontHeight = info.height.toDouble())
//                    println("bs: $size")
//                    val size = size(width = fontInfo.lineHeight.toDouble(), height = fontInfo.lineHeight.toDouble())
//                    val size = size(1024, 1024)
//                    val size = size(2048, 2048)
                    for (char in text) {
                        if (storage.isAvailable(info.info, char)) {
//                            println("Char ${char.code}) $char is available.")
                        } else {
                            println("Char #${char.code} is not available!")
                            continue
                        }
//                        val index = storage.getIndex(char)
//                        if (index < 0) continue
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

    override fun drawText(info: FontInfo, color: Color, pointTopLeft: Point, text: CharSequence) {
//        val sfi = storage.getInfo(info)
//        GLUtil.enabled(GL11.GL_TEXTURE_2D) {
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, sfi.textureId)
//            GLUtil.colorOf(color)
//            GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0)
//        }
//        return // todo
//        println("fh: " + info.height)
        drawText(storage.getInfo(info),
//            fontHeight = info.height,
            pointTopLeft, color, text)
    }
}
