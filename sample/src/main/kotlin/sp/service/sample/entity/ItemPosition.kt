package sp.service.sample.entity

import sp.kx.math.Point
import java.util.UUID

internal data class ItemPosition(
    val id: UUID,
    val point: Point,
)
