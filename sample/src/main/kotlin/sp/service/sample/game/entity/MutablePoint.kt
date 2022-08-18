package sp.service.sample.game.entity

import sp.kx.math.foundation.entity.geometry.Point

class MutablePoint(override var x: Double, override var y: Double) : Point {
    fun set(that: Point) {
        x = that.x
        y = that.y
    }
}
