package sp.service.sample.entity

import sp.kx.math.Vector
import java.util.UUID

internal class Barrier(
    val id: UUID,
    val vector: Vector,
    var opened: Boolean,
    val tags: List<List<UUID>>
)
