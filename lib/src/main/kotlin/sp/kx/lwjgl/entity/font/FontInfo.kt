package sp.kx.lwjgl.entity.font

import java.io.InputStream

interface FontInfo {
    val id: String
    val height: Float
    fun getInputStream(): InputStream
}
