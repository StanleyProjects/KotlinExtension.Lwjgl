package sp.kx.lwjgl.stb

import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryUtil
import sp.kx.lwjgl.entity.Size
import sp.kx.lwjgl.entity.font.FontVMetrics
import java.nio.ByteBuffer
import java.nio.FloatBuffer

object STBUtil {
    fun getPackedQuad(
        buffer: STBTTPackedchar.Buffer,
        size: Size,
        index: Int,
        xBuffer: FloatBuffer,
        yBuffer: FloatBuffer,
        quad: STBTTAlignedQuad,
        isAlignToInteger: Boolean = false
    ) {
        STBTruetype.stbtt_GetPackedQuad(
            buffer,
            size.width.toInt(),
            size.height.toInt(),
            index,
            xBuffer,
            yBuffer,
            quad,
            isAlignToInteger
        )
    }

    fun pack(
        context: STBTTPackContext,
        pixels: ByteBuffer,
        size: Size,
        strideInBytes: Int = 0,
        padding: Int = 1,
        alloc: Long? = null,
        block: () -> Unit
    ) {
        STBTruetype.stbtt_PackBegin(
            context,
            pixels,
            size.width.toInt(),
            size.height.toInt(),
            strideInBytes,
            padding,
            alloc ?: MemoryUtil.NULL
        )
        block()
        STBTruetype.stbtt_PackEnd(context)
    }
}

fun STBTTPackContext.packFontRange(
    fontByteBuffer: ByteBuffer,
    fontIndex: Int,
    fontSize: Float,
    firstUnicodeCharInRange: Int,
    charBufferForRange: STBTTPackedchar.Buffer
) {
    STBTruetype.stbtt_PackFontRange(
        this,
        fontByteBuffer,
        fontIndex,
        fontSize,
        firstUnicodeCharInRange,
        charBufferForRange
    )
}

fun STBTTFontinfo.toFontVMetrics(): FontVMetrics {
    val ascentBuffer = BufferUtils.createIntBuffer(1)
    val descentBuffer = BufferUtils.createIntBuffer(1)
    val lineGapBuffer = BufferUtils.createIntBuffer(1)
    STBTruetype.stbtt_GetFontVMetrics(this, ascentBuffer, descentBuffer, lineGapBuffer)
    return FontVMetrics(
        ascent = ascentBuffer[0],
        descent = descentBuffer[0],
        lineGap = lineGapBuffer[0]
    )
}
