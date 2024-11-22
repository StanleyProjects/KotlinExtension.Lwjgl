package sp.service.sample.entity

import java.util.UUID
import kotlin.time.Duration

internal data class Interactive(
    val id: UUID,
    val time: Duration,
    val type: Type,
) {
    enum class Type {
        Item,
        Crate,
    }
}
