package sp.kx.lwjgl.engine

import sp.kx.math.Size
import sp.kx.math.measure.Interval
import kotlin.time.Duration

interface EngineProperty {
    val time: Interval<Duration>
    val pictureSize: Size
}
