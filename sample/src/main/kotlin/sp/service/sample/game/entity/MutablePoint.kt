package sp.service.sample.game.entity

import sp.kx.math.foundation.entity.geometry.Point
import sp.kx.math.implementation.entity.geometry.vectorOf

class MutablePoint(override var x: Double, override var y: Double) : Point {
    fun move(length: Double, direction: Double) {
        val vector = vectorOf(start = this, length = length, direction = direction)
        x = vector.finish.x
        y = vector.finish.y
    }
}
