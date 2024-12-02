package sp.service.sample.entity

import java.util.UUID
import kotlin.time.Duration

internal class Interactive(
    val type: Class<out Any>,
    val id: UUID,
    val time: Duration,
) {
    fun current(type: Class<out Any>, id: UUID): Boolean {
        return this.type == type && this.id == id
    }

    fun getCurrentTime(type: Class<out Any>, id: UUID): Duration? {
        if (current(type = type, id = id)) return time
        return null
    }
}
