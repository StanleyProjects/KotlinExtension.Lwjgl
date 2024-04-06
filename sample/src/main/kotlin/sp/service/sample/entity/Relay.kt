package sp.service.sample.entity

import java.util.UUID

internal class Relay(
    val id: UUID,
    val required: Required?,
) {
    data class Required(
        val type: Type,
        val itemsTags: List<UUID>,
    ) {
        enum class Type {
            Have,
            Give,
            Lose,
        }
    }
}
