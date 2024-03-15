package sp.service.sample.logic

import sp.kx.lwjgl.entity.Color
import sp.kx.math.Vector

internal class Barrier(
    val color: Color,
    val vector: Vector,
    val passable: Passable,
) {
    sealed interface Passable {
        data object Not : Passable
        data object Full : Passable
    }
}
