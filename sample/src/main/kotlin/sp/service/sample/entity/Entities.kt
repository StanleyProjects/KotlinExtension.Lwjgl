package sp.service.sample.entity

import sp.kx.math.Point
import sp.kx.math.distanceOf
import sp.kx.math.gt

internal object Entities {
    fun getNearestItem(
        target: Point,
        items: List<Item>,
        maxDistance: Double,
    ): Item? {
        var nearest: Pair<Item, Double>? = null
        for (item in items) {
            if (item.owner != null) continue
            val distance = distanceOf(item.point, target)
            if (distance.gt(other = maxDistance, points = 12)) continue
            if (nearest == null || nearest.second > distance) {
                nearest = item to distance
            }
        }
        return nearest?.first
    }

    fun getNearestCrate(
        target: Point,
        crates: List<Crate>,
        maxDistance: Double,
    ): Crate? {
        var nearest: Pair<Crate, Double>? = null
        for (crate in crates) {
            val distance = distanceOf(crate.point, target)
            if (distance.gt(other = maxDistance, points = 12)) continue
            if (nearest == null || nearest.second > distance) {
                nearest = crate to distance
            }
        }
        return nearest?.first
    }
}
