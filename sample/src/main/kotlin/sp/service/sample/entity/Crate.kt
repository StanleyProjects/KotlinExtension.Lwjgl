package sp.service.sample.entity

import sp.kx.math.Point
import java.util.UUID

internal data class Crate(
    val id: UUID,
    val point: Point,
)
