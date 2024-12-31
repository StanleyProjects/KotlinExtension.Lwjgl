package sp.kx.lwjgl.drawer

import sp.kx.lwjgl.entity.Color
import sp.kx.math.Offset
import sp.kx.math.Vector
import sp.kx.math.measure.Measure

interface VectorDrawer {
    fun draw(
        color: Color,
        vector: Vector,
    )

    fun draw(
        color: Color,
        vector: Vector,
        offset: Offset,
    )

    fun draw(
        color: Color,
        vector: Vector,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    fun draw(
        color: Color,
        vector: Vector,
        lineWidth: Double,
    )

    fun draw(
        color: Color,
        vector: Vector,
        offset: Offset,
        lineWidth: Double,
    )

    fun draw(
        color: Color,
        vector: Vector,
        measure: Measure<Double, Double>,
        lineWidth: Double,
    )

    fun draw(
        color: Color,
        vector: Vector,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Double,
    )

    fun draw(
        color: Color,
        vectors: List<Vector>,
        offset: Offset,
        measure: Measure<Double, Double>,
    )

    fun draw(
        color: Color,
        vectors: List<Vector>,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Double,
    )
}
