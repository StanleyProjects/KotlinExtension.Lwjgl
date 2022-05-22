package sp.kx.lwjgl.stb

import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryUtil
import sp.kx.lwjgl.entity.font.BitmapBox
import sp.kx.lwjgl.entity.font.FontVMetrics
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

object STBUtil {
    fun getPackedQuad(
        buffer: STBTTPackedchar.Buffer,
        fontHeight: Float,
        index: Int,
        xBuffer: FloatBuffer,
        yBuffer: FloatBuffer,
        quad: STBTTAlignedQuad,
        isAlignToInteger: Boolean = false
    ) {
        val width = fontHeight * 128.0
        val height = fontHeight * 16.0
        STBTruetype.stbtt_GetPackedQuad(
            buffer,
            width.toInt(),
            height.toInt(),
            index,
            xBuffer,
            yBuffer,
            quad,
            isAlignToInteger
        )
    }
}

fun STBTTPackContext.pack(
    pixels: ByteBuffer,
    width: Int,
    height: Int,
    strideInBytes: Int = 0,
    padding: Int = 1,
    alloc: Long? = null,
    block: () -> Unit
) {
    STBTruetype.stbtt_PackBegin(
        this,
        pixels,
        width,
        height,
        strideInBytes,
        padding,
        alloc ?: MemoryUtil.NULL
    )
    block()
    STBTruetype.stbtt_PackEnd(this)
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

fun STBTTFontinfo.toFontVMetrics(fontHeight: Float): FontVMetrics {
    val ascentBuffer = BufferUtils.createIntBuffer(1)
    val descentBuffer = BufferUtils.createIntBuffer(1)
    val lineGapBuffer: IntBuffer? = null // todo
    STBTruetype.stbtt_GetFontVMetrics(this, ascentBuffer, descentBuffer, lineGapBuffer)
    val scale = STBTruetype.stbtt_ScaleForPixelHeight(this, fontHeight)
    return FontVMetrics(
        ascent = ascentBuffer[0] * scale,
        descent = descentBuffer[0] * scale
    )
}

fun STBTTFontinfo.toBitmapBox(fontHeight: Float): BitmapBox {
    val scale = STBTruetype.stbtt_ScaleForPixelHeight(this, fontHeight)
    val x0Buffer = BufferUtils.createIntBuffer(1)
    val y0Buffer = BufferUtils.createIntBuffer(1)
    val x1Buffer = BufferUtils.createIntBuffer(1)
    val y1Buffer = BufferUtils.createIntBuffer(1)
    STBTruetype.stbtt_GetCodepointBitmapBox(this, '|'.code, scale, scale, x0Buffer, y0Buffer, x1Buffer, y1Buffer)
    return BitmapBox(
        left = x0Buffer.get(0),
        bottom = y0Buffer.get(0),
        right = x1Buffer.get(0),
        top = y1Buffer.get(0)
    )
}

fun STBTTFontinfo.isAvailable(char: Char): Boolean {
    val index = STBTruetype.stbtt_FindGlyphIndex(this, char.code)
    return index != 0
}
