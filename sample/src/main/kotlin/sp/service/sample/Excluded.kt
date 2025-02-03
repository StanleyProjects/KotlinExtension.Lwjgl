package sp.service.sample

import sp.kx.math.Point
import sp.kx.math.angleOf
import sp.kx.math.distanceOf

@Deprecated("sp.kx.math.distanceOf") // todo hypot
fun distanceOf(point: Point): Double {
//    return distanceOf(Point.Center, point)
//    return distanceOf(
//        aX = 0.0,
//        aY = 0.0,
//        bX = point.x,
//        bY = point.y,
//    )
    return kotlin.math.hypot(x = point.x, y = point.y)
}

@Deprecated("sp.kx.math.angleOf")
fun angleOf(point: Point): Double {
//    return angleOf(Point.Center, point)
//    return angleOf(
//        aX = 0.0,
//        aY = 0.0,
//        bX = point.x,
//        bY = point.y,
//    )
    return kotlin.math.atan2(y = point.y, x = point.x)
}
