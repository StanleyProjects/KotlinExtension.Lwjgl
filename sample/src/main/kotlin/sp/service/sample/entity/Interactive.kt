package sp.service.sample.entity

import java.util.UUID
import kotlin.time.Duration

internal sealed interface Interactive {
    class Crate(
        val id: UUID,
    ) : Interactive

    class Item(
        val id: UUID,
        val time: Duration,
    ) : Interactive
}
