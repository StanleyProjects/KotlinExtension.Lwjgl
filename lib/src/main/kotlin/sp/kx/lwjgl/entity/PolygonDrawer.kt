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
        offset: Offset,
        direction: Double,
        pointOfRotation: Point,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        measure: Measure<Double, Double>,
        direction: Double,
        pointOfRotation: Point,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        measure: Measure<Double, Double>,
        lineWidth: Double,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        measure: Measure<Double, Double>,
        lineWidth: Double,
        direction: Double,
        pointOfRotation: Point,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Double,
    )

    fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Double,
        direction: Double,
        pointOfRotation: Point,
    )

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
    )
}
