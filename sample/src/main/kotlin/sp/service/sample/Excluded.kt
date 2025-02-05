package sp.service.sample

import sp.kx.math.Point
import sp.kx.math.angleOf
import sp.kx.math.distanceOf

@Deprecated("sp.kx.math.distanceOf") // todo hypot
internal fun distanceOf(point: Point): Double {
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
internal fun angleOf(point: Point): Double {
//    return angleOf(Point.Center, point)
//    return angleOf(
//        aX = 0.0,
//        aY = 0.0,
//        bX = point.x,
//        bY = point.y,
//    )
    return kotlin.math.atan2(y = point.y, x = point.x)
}

@Deprecated("sp.kx.math.Matrix")
internal class Matrix(
    val m00: Double, val m01: Double, val m02: Double, val m03: Double,
    val m10: Double, val m11: Double, val m12: Double, val m13: Double,
    val m20: Double, val m21: Double, val m22: Double, val m23: Double,
    val m30: Double, val m31: Double, val m32: Double, val m33: Double,
) {
    constructor() : this(
        m00 = 1.0, m01 = 0.0, m02 = 0.0, m03 = 0.0,
        m10 = 0.0, m11 = 1.0, m12 = 0.0, m13 = 0.0,
        m20 = 0.0, m21 = 0.0, m22 = 1.0, m23 = 0.0,
        m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 1.0,
    )
}
