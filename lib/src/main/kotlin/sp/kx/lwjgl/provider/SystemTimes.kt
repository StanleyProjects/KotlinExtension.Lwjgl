package sp.kx.lwjgl.provider

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

internal object SystemTimes : Times {
    override fun now(): Duration {
        return System.nanoTime().nanoseconds
    }
}
