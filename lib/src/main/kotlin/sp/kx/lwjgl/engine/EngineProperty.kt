package sp.kx.lwjgl.engine

import sp.kx.calculations.Interval
import sp.kx.calculations.Size
import java.nio.DoubleBuffer
import kotlin.time.Duration

interface EngineProperty {
    val launched: Duration
    val time: Interval<Duration>
    val pictureSize: Size
    val ortho: DoubleBuffer
}
