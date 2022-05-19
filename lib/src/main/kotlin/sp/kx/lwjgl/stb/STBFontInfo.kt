package sp.kx.lwjgl.stb

import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackedchar
import sp.kx.lwjgl.entity.font.FontVMetrics
import java.nio.ByteBuffer

data class STBFontInfo(
    val textureId: Int,
    val metrics: FontVMetrics,
    val buffer: STBTTPackedchar.Buffer,
    val container: ByteBuffer,
    val info: STBTTFontinfo
)
