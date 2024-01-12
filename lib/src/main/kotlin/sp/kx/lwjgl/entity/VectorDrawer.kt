package sp.kx.lwjgl.entity

import sp.kx.math.Offset
import sp.kx.math.Vector
import sp.kx.math.measure.Measure

interface VectorDrawer {
    fun draw(
        color: Color,
        vector: Vector,
        lineWidth: Float,
    )

    fun draw(
        color: Color,
        vector: Vector,
        offset: Offset,
        lineWidth: Float,
    )

    fun draw(
        color: Color,
        vector: Vector,
        measure: Measure<Double, Double>,
        lineWidth: Float,
    )

    fun draw(
        color: Color,
        vector: Vector,
        offset: Offset,
        measure: Measure<Double, Double>,
        lineWidth: Float,
    )
}
