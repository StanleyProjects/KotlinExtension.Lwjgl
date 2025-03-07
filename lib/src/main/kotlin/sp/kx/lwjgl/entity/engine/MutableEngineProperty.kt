package sp.kx.lwjgl.entity.engine

import sp.kx.lwjgl.engine.EngineProperty
import sp.kx.math.MutableDurationInterval
import sp.kx.math.Size
import java.nio.DoubleBuffer
import kotlin.time.Duration

internal class MutableEngineProperty(
    override var pictureSize: Size,
    override val ortho: DoubleBuffer,
) : EngineProperty {
    override var launched = Duration.ZERO
    override val time = MutableDurationInterval(a = Duration.ZERO, b = Duration.ZERO)
}
