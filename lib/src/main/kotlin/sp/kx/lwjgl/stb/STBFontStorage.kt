package sp.kx.lwjgl.stb

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import sp.kx.lwjgl.entity.Size
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.system.use
import sp.kx.lwjgl.util.IOUtil
import sp.kx.lwjgl.util.toByteBuffer
import java.io.InputStream
import java.nio.ByteBuffer

internal class STBFontStorage(chars: Iterable<Char>) {
//    private val chars = chars.toSortedSet()
    private val map = mutableMapOf<String, STBFontInfo>()

    private fun InputStream.getFontInfo(fontHeight: Float): STBFontInfo {
        val bufferSize = size(width = fontHeight * 64 * 1.5, height = fontHeight * 8 * 1.5)
        println("ss: $bufferSize")
        val fontByteBuffer = toByteBuffer(1024)
        val pixels = IOUtil.createByteBuffer(bufferSize)
//        val container = BufferUtils.createByteBuffer(STBTTFontinfo.SIZEOF)
        val fontInfo = STBTTFontinfo.create()
        STBTruetype.stbtt_InitFont(fontInfo, fontByteBuffer)
        val limit = Char.MAX_VALUE.code
        val charBuffer = STBTTPackedchar.malloc(limit)
//        val position = chars.first().code
//        val position = 0
        val position = Char.MIN_VALUE.code
        STBTTPackContext.malloc().use { context ->
            STBUtil.pack(
                context = context,
                pixels = pixels,
                fontHeight = fontHeight
            ) {
                charBuffer.limit(limit)
                charBuffer.position(position)

                STBTruetype.stbtt_PackSetOversampling(context, 2, 2)

                context.packFontRange(
                    fontByteBuffer = fontByteBuffer,
                    fontIndex = 0,
                    fontSize = fontHeight,
                    firstUnicodeCharInRange = position,
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
        return STBFontInfo(
            textureId = textureId,
            lineHeight = lineHeight,
            buffer = charBuffer,
            container = fontByteBuffer,
            info = fontInfo
        )
    }

    fun getInfo(info: FontInfo): STBFontInfo {
        return map.getOrPut(info.id) {
            println("create font by id: ${info.id} with size: ${info.height}")
            info.getInputStream().use {
                it.getFontInfo(info.height)
            }
        }
    }

    fun isAvailable(info: STBTTFontinfo, char: Char): Boolean {
        val index = STBTruetype.stbtt_FindGlyphIndex(info, char.code)
        return index != 0
    }

//    fun getIndex(char: Char): Int {
//        return chars.indexOf(char)
//    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "size!")
    fun getBufferSize(fontHeight: Double): Size {
        return size(width = fontHeight * 64 * 1.5, height = fontHeight * 8 * 1.5)
//        return size(width = fontHeight * 256, height = fontHeight * 256)
//        return size(width = fontHeight * chars.size, height = fontHeight * 2.0)
//        return size(width = fontHeight * chars.size, height = 256.0)
//        return size(width = fontHeight * 128, height = fontHeight * 16)
//        return size(width = 1024, height = 128) // 8
//        return size(width = 1024, height = 256) // 16
//        return size(width = 2048, height = 512) // 32
//        return size(width = 1024, height = 1024)
    }
}
