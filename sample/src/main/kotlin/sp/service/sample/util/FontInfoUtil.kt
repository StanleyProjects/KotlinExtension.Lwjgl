package sp.service.sample.util

import sp.kx.lwjgl.entity.font.FontInfo
import java.io.InputStream

object FontInfoUtil {
    private val map = mutableMapOf<String, FontInfo>()

    fun getFontInfo(name: String = "JetBrainsMono.ttf", height: Float): FontInfo {
        val id = "${name}_${height}"
        return map.getOrPut(id) {
            object : FontInfo {
                override val id: String = id
                override val height: Float = height

                override fun getInputStream(): InputStream {
                    return ResourceUtil.requireResourceAsStream(name)
                }
            }
        }
    }
}
