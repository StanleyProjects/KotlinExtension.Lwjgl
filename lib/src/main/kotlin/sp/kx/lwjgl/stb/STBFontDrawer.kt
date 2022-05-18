package sp.kx.lwjgl.stb

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTAlignedQuad
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.Point
import sp.kx.lwjgl.entity.font.FontDrawer
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.lwjgl.system.use

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
        info: FontInfo,
        sfi: STBFontInfo,
        pointTopLeft: Point,
        color: Color,
        text: CharSequence
    ) {
        val x = pointTopLeft.x
        val y = pointTopLeft.y + sfi.lineHeight

        val xBuffer = BufferUtils.createFloatBuffer(1)
        val yBuffer = BufferUtils.createFloatBuffer(1)
        xBuffer.put(0, x.toFloat())
        yBuffer.put(0, y.toFloat())

        GLUtil.enabled(GL11.GL_TEXTURE_2D) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, sfi.textureId)

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
                        if (storage.isAvailable(sfi.info, char)) {
//                            println("Char ${char.code}) $char is available.")
                        } else {
                            println("Char #${char.code} is not available!")
                            continue
                        }
//                        val index = storage.getIndex(char)
//                        if (index < 0) continue
                        STBUtil.getPackedQuad(
                            buffer = sfi.buffer,
                            fontHeight = info.height,
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
        drawText(info, storage.getInfo(info), pointTopLeft, color, text)
    }
}
