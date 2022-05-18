package sp.kx.lwjgl.stb

import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.NativeType
import sp.kx.lwjgl.entity.font.FontAgent
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.system.use
import java.nio.IntBuffer

internal class STBFontAgent(private val storage: STBFontStorage): FontAgent {
    private fun getCodePoint(
        text: CharSequence,
        to: Int,
        i: Int,
        cpOut: IntBuffer
    ): Int {
        val c1 = text[i]
//        if (Character.isHighSurrogate(c1) && i + 1 < to) {
//            val c2 = text[i + 1]
//            if (Character.isLowSurrogate(c2)) {
//                cpOut.put(0, Character.toCodePoint(c1, c2))
//                return 2
//            }
//        }
        cpOut.put(0, c1.code)
        return 1
    }

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

    private fun getTextWidthOld(info: STBFontInfo, text: CharSequence): Double {
        val from = 0
        val to = text.length
        var result = 0.0
        val scale = STBTruetype.stbtt_ScaleForPixelHeight(info.info, info.lineHeight)
        println("scale $scale")
        MemoryStack.stackPush().use {
//            val widthBuffer   = it.mallocInt(1)
//            val heightBuffer   = it.mallocInt(1)
            val pCodePoint       = it.mallocInt(1)
            val pAdvancedWidth   = it.mallocInt(1)
            val pLeftSideBearing = it.mallocInt(1)
            var i = from
            while (i < to) {
//                i += getCodePoint(text, to, i, pCodePoint)
//                val cp = pCodePoint.get(0)
                val char = text[i]
                val cp = char.code
                i++
                //
                //  @NativeType("stbtt_fontinfo const *") STBTTFontinfo info,
                //  float scale_x,
                //  float scale_y,
                //  int codepoint,
                //  @NativeType("int *") IntBuffer width,
                //  @NativeType("int *") IntBuffer height,
                //  @Nullable @NativeType("int *") IntBuffer xoff,
                //  @Nullable @NativeType("int *") IntBuffer yoff
                //
//                val bitmapBuffer = STBTruetype.stbtt_GetCodepointBitmap(info.info, 0f, scale, cp, widthBuffer, heightBuffer, null, null)
                STBTruetype.stbtt_GetCodepointHMetrics(info.info, cp, pAdvancedWidth, pLeftSideBearing)
                val w = pAdvancedWidth.get(0)
                println("$i) [$char] $w")
                result += w
//                result += pAdvancedWidth.get(0)
            }
        }
        return result * STBTruetype.stbtt_ScaleForPixelHeight(info.info, info.lineHeight)
    }

    override fun getTextWidth(info: FontInfo, text: CharSequence): Double {
        return storage.getInfo(info).info.getTextWidth(height = info.height, text)
    }
}
