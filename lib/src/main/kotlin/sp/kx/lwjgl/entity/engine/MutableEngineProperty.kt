package sp.kx.lwjgl.entity.engine

import sp.kx.calculations.Size
import sp.kx.calculations.physics.MutableDurationInterval
import sp.kx.lwjgl.engine.EngineProperty
import java.nio.DoubleBuffer
import kotlin.time.Duration

internal class MutableEngineProperty(
    override var pictureSize: Size,
    override val ortho: DoubleBuffer,
) : EngineProperty {
    override var launched = Duration.ZERO
    override val time = MutableDurationInterval(a = Duration.ZERO, b = Duration.ZERO)
}
