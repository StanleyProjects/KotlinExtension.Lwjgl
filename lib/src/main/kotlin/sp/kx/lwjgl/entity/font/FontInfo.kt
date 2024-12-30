package sp.kx.lwjgl.entity.font

import java.io.InputStream

@Deprecated(message = "replace with sp.kx.lwjgl.drawer.TextDrawer")
interface FontInfo {
    val id: String
    val height: Float
    fun getInputStream(): InputStream
}
