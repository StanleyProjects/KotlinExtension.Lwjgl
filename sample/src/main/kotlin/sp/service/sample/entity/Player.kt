package sp.service.sample.entity

import java.util.UUID

internal class Player(
    val id: UUID,
    val moving: MutableMoving,
    val turning: MutableTurning,
)
