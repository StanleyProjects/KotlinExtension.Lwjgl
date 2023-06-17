package sp.kx.lwjgl.entity.engine

import sp.kx.lwjgl.engine.EngineProperty
import sp.kx.math.Size
import sp.kx.math.measure.MutableDurationInterval
import kotlin.time.Duration

internal class MutableEngineProperty(
    override val time: MutableDurationInterval = MutableDurationInterval(a = Duration.ZERO, b = Duration.ZERO),
    override var pictureSize: Size
) : EngineProperty
