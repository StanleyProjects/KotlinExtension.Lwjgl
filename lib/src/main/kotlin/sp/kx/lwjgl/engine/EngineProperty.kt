package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.Size
import kotlin.time.Duration

interface EngineProperty {
    val time: Time
    val pictureSize: Size

    interface Time {
        val last: Duration
        val now: Duration

        fun diff(): Duration {
            return now - last
        }
    }
}
