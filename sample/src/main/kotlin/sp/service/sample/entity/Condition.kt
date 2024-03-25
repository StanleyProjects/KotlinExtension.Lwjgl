package sp.service.sample.entity

import java.util.UUID

internal data class Condition(
    val id: UUID,
    val passed: Boolean,
)
