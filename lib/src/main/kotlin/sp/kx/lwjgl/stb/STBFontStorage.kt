package sp.kx.lwjgl.stb

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import sp.kx.lwjgl.entity.font.FontAgent
import sp.kx.lwjgl.entity.font.FontDrawer
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.glfw.GLFWUtil
import sp.kx.lwjgl.system.use
import sp.kx.lwjgl.util.toArray
import sp.kx.lwjgl.util.toByteBuffer
import java.io.InputStream

internal class STBFontStorage {
    private val map = mutableMapOf<String, STBFontInfo>()
    val agent: FontAgent = STBFontAgent(this)
    val drawer: FontDrawer = STBFontDrawer(this)

    private fun InputStream.getFontInfo(fontHeight: Float): STBFontInfo {
        val width: Int = (fontHeight * 128.0).toInt()
        val height: Int = (fontHeight * 16.0).toInt()
        val fontByteBuffer = toByteBuffer(1024)
        val pixels = BufferUtils.createByteBuffer(width * height)
        val fontInfo = STBTTFontinfo.create()
        STBTruetype.stbtt_InitFont(fontInfo, fontByteBuffer)
        val limit = Char.MAX_VALUE.code
        val charBuffer = STBTTPackedchar.malloc(limit)
        val position = Char.MIN_VALUE.code
        val fontVMetrics = fontInfo.toFontVMetrics(fontHeight = fontHeight)
        STBTTPackContext.malloc().use { context ->
            context.pack(
                pixels = pixels,
                width = width,
                height = height
            ) {
                charBuffer.limit(limit)
                charBuffer.position(position)

                STBTruetype.stbtt_PackSetOversampling(context, 2, 2)

                context.packFontRange(
                    fontByteBuffer = fontByteBuffer,
                    fontIndex = 0,
                    fontSize = fontVMetrics.ascent - fontVMetrics.descent,
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
            width = width,
            height = height,
            texelDataFormat = GL11.GL_ALPHA,
            texelDataType = GL11.GL_UNSIGNED_BYTE,
            pixels = pixels
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        (0..10).forEach { nameId ->
            val name = STBTruetype.stbtt_GetFontNameString(
                fontInfo,
                STBTruetype.STBTT_PLATFORM_ID_MICROSOFT,
                STBTruetype.STBTT_UNICODE_EID_UNICODE_1_1,
                STBTruetype.STBTT_MS_LANG_ENGLISH,
                nameId
            )?.toArray()
            if (name != null) {
                println("$nameId) ${String(name)}") // todo
            }
        }
        return STBFontInfo(
            textureId = textureId,
            metrics = fontVMetrics,
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
}
