package sp.service.sample.entity

import sp.kx.math.Point
import java.util.UUID

internal data class Position(
    val id: UUID,
    var point: Point,
)
