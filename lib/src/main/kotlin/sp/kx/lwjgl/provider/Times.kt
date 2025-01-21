package sp.kx.lwjgl.provider

import kotlin.time.Duration

interface Times {
    fun now(): Duration

    companion object {
        /**
         * s         f
         * *---------*
         *      <--t->
         */
        fun isPassed(start: Duration, finish: Duration, target: Duration): Boolean {
            return finish - start >= target
        }

        /**
         * s1   s2   f
         * *----*----*
         *      <--t->
         */
        fun isPassed(start: Duration, min: Duration, finish: Duration, target: Duration): Boolean {
            return finish - start.coerceAtLeast(min) >= target
        }

        fun progress(start: Duration, min: Duration, finish: Duration, target: Duration): Double {
            val diff = finish - start.coerceAtLeast(min)
            return diff.inWholeNanoseconds.toDouble() / target.inWholeNanoseconds
        }
    }
}
