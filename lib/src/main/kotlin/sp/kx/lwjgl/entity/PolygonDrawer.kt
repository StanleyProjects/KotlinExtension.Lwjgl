package sp.kx.lwjgl.entity

import sp.kx.math.Offset
import sp.kx.math.Point
import sp.kx.math.Size
import sp.kx.math.measure.Measure

interface PolygonDrawer {
    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        measure: Measure<Double, Double>,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    // lineWidth

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        offset: Offset,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        measure: Measure<Double, Double>,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    // border
    // todo border on border

    fun drawRectangle(
        borderColor: Color,
        fillColor: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
    )

    fun drawRectangle(
        borderColor: Color,
        fillColor: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        offset: Offset,
    )

    fun drawRectangle(
        borderColor: Color,
        fillColor: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        measure: Measure<Double, Double>,
    )

    fun drawRectangle(
        borderColor: Color,
        fillColor: Color,
        pointTopLeft: Point,
        size: Size,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    // direction

    // todo pointOfRotation = pointTopLeft.plus(dX = size.width / 2, dY = size.height / 2)
//    fun drawRectangle(
//        color: Color,
//        pointTopLeft: Point,
//        size: Size,
//        direction: Double,
//    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        offset: Offset,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        measure: Measure<Double, Double>,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    // direction + lineWidth

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        lineWidth: Double,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        lineWidth: Double,
        offset: Offset,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        lineWidth: Double,
        measure: Measure<Double, Double>,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        direction: Double,
        pointOfRotation: Point,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    // todo direction + border

    // circle

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
    )

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
    )

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        measure: Measure<Double, Double>,
    )

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    // circle + lineWidth

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Double,
    )

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Double,
        offset: Offset,
    )

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Double,
        measure: Measure<Double, Double>,
    )

    fun drawCircle(
        color: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    // circle + border

    // todo
//    fun drawCircle(
//        borderColor: Color,
//        fillColor: Color,
//        pointCenter: Point,
//        radius: Double,
//        edgeCount: Int,
//        lineWidth: Double,
//    )

    // todo
//    fun drawCircle(
//        borderColor: Color,
//        fillColor: Color,
//        pointCenter: Point,
//        radius: Double,
//        edgeCount: Int,
//        lineWidth: Double,
//        offset: Offset,
//    )

    // todo
//    fun drawCircle(
//        borderColor: Color,
//        fillColor: Color,
//        pointCenter: Point,
//        radius: Double,
//        edgeCount: Int,
//        lineWidth: Double,
//        measure: Measure<Double, Double>,
//    )

    fun drawCircle(
        borderColor: Color,
        fillColor: Color,
        pointCenter: Point,
        radius: Double,
        edgeCount: Int,
        lineWidth: Double,
        offset: Offset,
        measure: Measure<Double, Double>,
    )
}
