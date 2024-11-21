package sp.kx.lwjgl.system

import sp.kx.lwjgl.engine.TimeProvider
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

internal object SystemTimeProvider : TimeProvider {
    override fun now(): Duration {
        return System.nanoTime().nanoseconds
    }
}
