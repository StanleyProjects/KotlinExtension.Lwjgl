package sp.kx.lwjgl.entity.font

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryUtil
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.Point
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.lwjgl.stb.STBUtil
import sp.kx.lwjgl.stb.packFontRange
import sp.kx.lwjgl.stb.toFontVMetrics
import sp.kx.lwjgl.system.use
import sp.kx.lwjgl.util.IOUtil
import sp.kx.lwjgl.util.toByteBuffer
import java.io.FileInputStream
import java.io.InputStream

class AdvancedFontDrawer : FontDrawer {
    companion object {
        private val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,".toSortedSet()
        private val bufferSize = size(2048, 2048)
        private val infos = mutableMapOf<String, FontSTBInfo>()
        private const val charBufferLimit = Char.MAX_VALUE.code

        private fun drawAlignedQuad(quad: STBTTAlignedQuad) {
            GL11.glTexCoord2f(
                quad.s0(),
                quad.t0()
            )
            GLUtil.vertexOf(
                quad.x0(),
                quad.y0()
            )
            GL11.glTexCoord2f(
                quad.s1(),
                quad.t0()
            )
            GLUtil.vertexOf(
                quad.x1(),
                quad.y0()
            )
            GL11.glTexCoord2f(
                quad.s1(),
                quad.t1()
            )
            GLUtil.vertexOf(
                quad.x1(),
                quad.y1()
            )
            GL11.glTexCoord2f(
                quad.s0(),
                quad.t1()
            )
            GLUtil.vertexOf(
                quad.x0(),
                quad.y1()
            )
        }

        private fun drawText(
            fontInfo: FontSTBInfo,
            pointTopLeft: Point,
            color: Color,
            text: CharSequence
        ) {
            val x = pointTopLeft.x
            val y = pointTopLeft.y + fontInfo.lineHeight

            val xBuffer = BufferUtils.createFloatBuffer(1)
            val yBuffer = BufferUtils.createFloatBuffer(1)
            xBuffer.put(0, x.toFloat())
            yBuffer.put(0, y.toFloat())

            GLUtil.enabled(GL11.GL_TEXTURE_2D) {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontInfo.textureId)

                GLUtil.colorOf(color)

                STBTTAlignedQuad.malloc().use { quad ->
                    GLUtil.transaction(GL11.GL_QUADS) {
                        for (c in text) {
                            if (c in chars) {
                                STBUtil.getPackedQuad(
                                    buffer = fontInfo.buffer,
                                    size = bufferSize,
                                    index = c.code,
                                    xBuffer = xBuffer,
                                    yBuffer = yBuffer,
                                    quad = quad
                                )
                                drawAlignedQuad(quad)
                            }
                        }
                    }
                }
            }
        }

        private fun InputStream.getFontInfo(fontHeight: Float): FontSTBInfo {
            val fontByteBuffer = toByteBuffer(1024)
            val pixels = IOUtil.createByteBuffer(bufferSize)
            val fontInfo = STBTTFontinfo.create()
            STBTruetype.stbtt_InitFont(fontInfo, fontByteBuffer)
            val charBuffer = STBTTPackedchar.malloc(charBufferLimit)
            STBTTPackContext.malloc().use { context ->
                STBUtil.pack(
                    context = context,
                    pixels = pixels,
                    size = bufferSize
                ) {
                    charBuffer.limit(charBufferLimit)
                    charBuffer.position(chars.first().code)

                    STBTruetype.stbtt_PackSetOversampling(context, 2, 2)

                    context.packFontRange(
                        fontByteBuffer = fontByteBuffer,
                        fontIndex = 0,
                        fontSize = fontHeight,
                        firstUnicodeCharInRange = chars.first().code,
                        charBufferForRange = charBuffer
                    )
                    charBuffer.clear()
                }
            }
            val textureId = GL11.glGenTextures()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
            GLFWUtil.texImage2D(
                textureTarget = GL11.GL_TEXTURE_2D,
                textureInternalFormat = GL11.GL_ALPHA,
                textureSize = bufferSize,
                texelDataFormat = GL11.GL_ALPHA,
                texelDataType = GL11.GL_UNSIGNED_BYTE,
                pixels = pixels
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
            val fontVMetrics = fontInfo.toFontVMetrics()
            val lineHeight = fontHeight /
                    (fontVMetrics.ascent.toFloat() - fontVMetrics.descent.toFloat() - fontVMetrics.lineGap.toFloat() / 2) *
                    fontVMetrics.ascent.toFloat()
            return FontSTBInfo(
                textureId = textureId,
                lineHeight = lineHeight,
//                fontHeight = fontHeight,
                buffer = charBuffer,
//                info = fontInfo
            )
        }
    }

    override fun drawText(
        info: FontInfo,
        color: Color,
        pointTopLeft: Point,
        text: CharSequence
    ) {
        val fsi = infos.getOrPut(info.id) {
            println("create font by id: ${info.id} with size: ${info.height}")
            info.getInputStream().use {
                it.getFontInfo(info.height)
            }
        }
        drawText(fsi, pointTopLeft, color, text)
    }
}
