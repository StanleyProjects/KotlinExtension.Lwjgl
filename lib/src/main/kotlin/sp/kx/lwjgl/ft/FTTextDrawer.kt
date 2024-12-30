package sp.kx.lwjgl.ft

import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.util.freetype.FT_Face
import org.lwjgl.util.freetype.FreeType
import org.lwjgl.util.freetype.FreeType.FT_Done_Face
import org.lwjgl.util.freetype.FreeType.FT_Done_FreeType
import org.lwjgl.util.freetype.FreeType.FT_Init_FreeType
import org.lwjgl.util.freetype.FreeType.FT_Load_Char
import org.lwjgl.util.freetype.FreeType.FT_New_Memory_Face
import org.lwjgl.util.freetype.FreeType.FT_Set_Pixel_Sizes
import sp.kx.lwjgl.drawer.TextDrawer
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.opengl.GLUtil
import sp.kx.math.Point
import java.nio.ByteBuffer

internal class FTTextDrawer(
    defaultFontName: String,
) : TextDrawer(defaultFontName = defaultFontName) {
    private val fonts = mutableMapOf<String, Pair<ByteBuffer, MutableMap<Int, FTAtlas>>>()

    private fun getAtlases(fontName: String): Pair<ByteBuffer, MutableMap<Int, FTAtlas>> {
        return fonts.getOrPut(fontName) {
            val fontBytes = Thread.currentThread()
                .contextClassLoader
                .getResourceAsStream(fontName)!!.use { it.readBytes() }
            val buffer = ByteBuffer
                .allocateDirect(fontBytes.size)
                .put(fontBytes)
                .flip()
            buffer to mutableMapOf()
        }
    }

    private fun getAtlas(buffer: ByteBuffer, atlases: MutableMap<Int, FTAtlas>, fontHeight: Double): FTAtlas {
        val atlasHeight = getAtlasHeight(fontHeight = fontHeight)
        return atlases.getOrPut(atlasHeight) {
            stackPush().use { stack ->
                val lib = stack.mallocPointer(1)
                FT_Init_FreeType(lib).ftChecked()
                val pointer = stack.mallocPointer(1)
                val faceIndex: Long = 0
                FT_New_Memory_Face(lib.get(0), buffer, faceIndex, pointer).ftChecked()
                val ftFace = FT_Face.create(pointer.get(0))
                val atlas = getAtlas(ftFace = ftFace, atlasHeight = atlasHeight)
                FT_Done_Face(ftFace).ftChecked()
                FT_Done_FreeType(lib.get(0)).ftChecked()
                atlas
            }
        }
    }

    override fun draw(
        color: Color,
        fontName: String,
        fontHeight: Double,
        pointTopLeft: Point,
        text: CharSequence,
    ) {
        val (buffer, atlases) = getAtlases(fontName = fontName)
        val atlas = getAtlas(
            buffer = buffer,
            atlases = atlases,
            fontHeight = fontHeight,
        )
        draw(
            color = color,
            atlas = atlas,
            scale = fontHeight / (atlas.ascender - atlas.descender),
            x = pointTopLeft.x,
            y = pointTopLeft.y,
            text = text,
        )
    }

    companion object {
        private fun Int.ftChecked() {
            if (this == FreeType.FT_Err_Ok) return
            error("FT:error: $this!")
        }

        private fun getAtlasHeight(fontHeight: Double): Int {
            val power = kotlin.math.log2(fontHeight)
            val top = kotlin.math.min(8, kotlin.math.ceil(power).toInt())
            return 1 shl kotlin.math.max(1, top)
        }

        private fun getTexture(
            width: Int,
            height: Int,
        ): Int {
            val id = GL11.glGenTextures()
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
            val buffer: ByteBuffer? = null
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_ALPHA,
                width,
                height,
                0,
                GL11.GL_ALPHA,
                GL11.GL_UNSIGNED_BYTE,
                buffer,
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
            return id
        }

        private fun getAtlas(
            ftFace: FT_Face,
            atlasHeight: Int,
        ): FTAtlas {
            val pixel_height = atlasHeight
            val pixel_width = 0
            // http://refspecs.linux-foundation.org/freetype/freetype-doc-2.1.10/docs/reference/ft2-base_interface.html#FT_Set_Pixel_Sizes
            FT_Set_Pixel_Sizes(ftFace, pixel_width, pixel_height).ftChecked()
            val size = ftFace.size() ?: TODO("FTTextDrawer:getAtlas: no size!")
            val metrics = size.metrics()
            val ascender: Int = (metrics.ascender() shr 6).toInt()
            val descender: Int = (metrics.descender() shr 6).toInt()
            val glyphs = mutableMapOf<Int, FTGlyph>()
            val buffers = mutableMapOf<Int, ByteBuffer>()
            var width = 0
            var height = 0
            val padding = 4
            for (code in 33..126) {
                FT_Load_Char(ftFace, code.toLong(), FreeType.FT_LOAD_RENDER).ftChecked()
                val glyph = ftFace.glyph() ?: continue
                val bitmap = glyph.bitmap()
                val pitch = bitmap.pitch()
                val rows = bitmap.rows()
                buffers[code] = bitmap.buffer(pitch * rows) ?: continue
                height = kotlin.math.max(height, rows)
                val advance: Int = (glyph.advance().x() shr 6).toInt()
                glyphs[code] = FTGlyph(
                    width = pitch,
                    height = rows,
                    x = width,
                    advance = advance,
                    left = glyph.bitmap_left(),
                    top = glyph.bitmap_top(),
                )
                width += pitch + padding
            }
            val id = getTexture(
                width = width,
                height = height,
            )
            for ((code, glyph) in glyphs) {
                val buffer = buffers[code] ?: TODO("FTTextDrawer:getAtlas: no bitmap($code:${code.toChar()})!")
                GL11.glTexSubImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    glyph.x,
                    0,
                    glyph.width,
                    glyph.height,
                    GL11.GL_ALPHA,
                    GL11.GL_UNSIGNED_BYTE,
                    buffer,
                )
            }
            FT_Load_Char(ftFace, ' '.code.toLong(), FreeType.FT_LOAD_RENDER).ftChecked()
            val space = ftFace.glyph()?.advance()?.x()?.shr(6)?.toInt() ?: (atlasHeight / 2)
            return FTAtlas(
                id = id,
                width = width,
                height = height,
                ascender = ascender,
                descender = descender,
                upe = ftFace.units_per_EM().toInt(),
                glyphs = glyphs,
                space = space,
            )
        }

        private fun draw(
            atlas: FTAtlas,
            scale: Double,
            glyph: FTGlyph,
            x: Double,
            y: Double,
        ) {
            val ipw = 1.0 / atlas.width
            val iph = 1.0 / atlas.height
            //
            val x0 = x + glyph.left * scale
            val y0 = y + (atlas.ascender - glyph.top) * scale
            val x1 = x0 + glyph.width * scale
            val y1 = y0 + glyph.height * scale
            val s0 = glyph.x * ipw
            val t0 = 0.0
            val s1 = (glyph.x + glyph.width) * ipw
            val t1 = glyph.height * iph
            //
            GL11.glTexCoord2d(s0, t0)
            GL11.glVertex2d(x0, y0)
            GL11.glTexCoord2d(s1, t0)
            GL11.glVertex2d(x1, y0)
            GL11.glTexCoord2d(s1, t1)
            GL11.glVertex2d(x1, y1)
            GL11.glTexCoord2d(s0, t1)
            GL11.glVertex2d(x0, y1)
        }

        private fun draw(
            color: Color,
            atlas: FTAtlas,
            scale: Double,
            x: Double,
            y: Double,
            text: CharSequence,
        ) {
            var i = x
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas.id)
            GLUtil.colorOf(color = color)
            for (char in text) {
                val glyph = atlas.glyphs[char.code]
                if (glyph == null) {
                    if (char == ' ') i += atlas.space * scale
                    continue
                }
                GL11.glBegin(GL11.GL_QUADS)
                draw(
                    atlas = atlas,
                    scale = scale,
                    glyph = glyph,
                    x = i,
                    y = y,
                )
                GL11.glEnd()
                i += glyph.advance * scale
            }
            GL11.glDisable(GL11.GL_TEXTURE_2D)
        }
    }
}
