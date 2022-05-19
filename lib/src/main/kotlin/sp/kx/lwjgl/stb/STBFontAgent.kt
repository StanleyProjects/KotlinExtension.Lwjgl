package sp.kx.lwjgl.stb

import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import sp.kx.lwjgl.entity.font.FontAgent
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.system.use

internal class STBFontAgent(private val storage: STBFontStorage): FontAgent {
    private fun STBTTFontinfo.getTextWidth(height: Float, text: CharSequence): Double {
        val result = MemoryStack.stackPush().use {
            val widthBuffer = it.mallocInt(1)
            text.sumOf { char ->
                STBTruetype.stbtt_GetCodepointHMetrics(this, char.code, widthBuffer, null)
                widthBuffer.get(0)
            }
        }
        return result.toDouble() * STBTruetype.stbtt_ScaleForPixelHeight(this, height)
    }

    override fun getTextWidth(info: FontInfo, text: CharSequence): Double {
        return storage.getInfo(info).info.getTextWidth(height = info.height, text)
    }
}
