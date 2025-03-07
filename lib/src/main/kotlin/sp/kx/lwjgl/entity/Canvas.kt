package sp.kx.lwjgl.entity

import sp.kx.lwjgl.drawer.PolygonDrawer
import sp.kx.lwjgl.drawer.TextDrawer
import sp.kx.lwjgl.drawer.VectorDrawer

@Deprecated(message = "drawers")
interface Canvas {
    val vectors: VectorDrawer
    val polygons: PolygonDrawer
    val texts: TextDrawer
}
