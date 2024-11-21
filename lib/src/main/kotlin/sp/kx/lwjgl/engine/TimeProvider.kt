package sp.kx.lwjgl.engine

import kotlin.time.Duration

interface TimeProvider {
    fun now(): Duration
}
