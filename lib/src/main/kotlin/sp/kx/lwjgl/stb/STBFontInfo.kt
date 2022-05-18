package sp.kx.lwjgl.stb

import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackedchar
import java.nio.ByteBuffer

data class STBFontInfo(
    val textureId: Int,
    val lineHeight: Float,
    val buffer: STBTTPackedchar.Buffer,
    val container: ByteBuffer,
    val info: STBTTFontinfo
)
