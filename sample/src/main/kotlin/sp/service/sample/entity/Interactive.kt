package sp.service.sample.entity

import java.util.UUID
import kotlin.time.Duration

internal class Interactive(
    val type: Class<out Any>,
    val id: UUID,
    val time: Duration,
)
