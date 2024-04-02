package sp.service.sample.entity

import sp.kx.math.Point
import java.util.UUID

internal class Relay(
    val id: UUID,
    var enabled: Boolean,
    val point: Point,
    val type: Type,
) {
    sealed interface Type {
        data object Unconditional : Type
        data class RequiringItem(val itemId: UUID) : Type
    }

    fun toggle() {
        enabled = !enabled
    }
}
