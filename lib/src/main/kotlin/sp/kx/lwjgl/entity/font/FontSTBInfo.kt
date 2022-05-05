package sp.kx.lwjgl.entity.font

import org.lwjgl.stb.STBTTPackedchar

data class FontSTBInfo(
    val textureId: Int,
    val lineHeight: Float,
    val buffer: STBTTPackedchar.Buffer
)
