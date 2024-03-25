package sp.service.sample.entity

import sp.kx.math.Vector
import java.util.UUID

internal data class Barrier(
    val id: UUID,
    val vector: Vector,
)
