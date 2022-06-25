package sp.service.sample.game.entity

import sp.kx.math.foundation.entity.geometry.Point
import sp.kx.math.implementation.entity.geometry.moved

class MutablePoint(override var x: Double, override var y: Double) : Point {
    fun move(length: Double, direction: Double) {
        set(moved(length = length, direction = direction))
    }

    fun set(that: Point) {
        x = that.x
        y = that.y
    }
}
