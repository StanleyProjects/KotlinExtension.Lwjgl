package sp.kx.lwjgl.engine

import sp.kx.calculations.Interval
import sp.kx.lwjgl.entity.Picture
import java.nio.DoubleBuffer
import kotlin.time.Duration

interface EngineProperty {
    val launched: Duration
    val time: Interval<Duration>
    val picture: Picture
    val ortho: DoubleBuffer
}
