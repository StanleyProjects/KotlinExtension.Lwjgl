package sp.service.sample.entity

import sp.kx.math.Point
import java.util.UUID

internal class Relay(
    val id: UUID,
    var enabled: Boolean,
    val point: Point,
) {
    fun toggle() {
        enabled = !enabled
    }
}
