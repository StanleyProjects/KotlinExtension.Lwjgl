package sp.kx.lwjgl.entity.engine

import sp.kx.lwjgl.engine.EngineProperty
import sp.kx.lwjgl.entity.Size
import kotlin.time.Duration

internal class MutableEngineProperty(
    override val time: MutableTime = MutableTime(),
    override var pictureSize: Size
) : EngineProperty

internal class MutableTime(
    override var last: Duration = Duration.ZERO,
    override var now: Duration = last,
) : EngineProperty.Time
