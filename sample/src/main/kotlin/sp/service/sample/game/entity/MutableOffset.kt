package sp.service.sample.game.entity

import sp.kx.math.foundation.entity.geometry.Offset

class MutableOffset(
    override var dX: Double,
    override var dY: Double
) : Offset
