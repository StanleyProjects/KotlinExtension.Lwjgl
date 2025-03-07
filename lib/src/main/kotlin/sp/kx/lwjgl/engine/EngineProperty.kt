package sp.kx.lwjgl.engine

import sp.kx.math.Interval
import sp.kx.math.Size
import java.nio.DoubleBuffer
import kotlin.time.Duration

interface EngineProperty {
    val launched: Duration
    val time: Interval<Duration>
    val pictureSize: Size
    val ortho: DoubleBuffer
}
