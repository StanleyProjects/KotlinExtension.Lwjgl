package sp.service.sample

import sp.service.sample.entity.Interactive
import java.util.UUID

internal interface InteractiveHolder {
    val map: Map<Class<out Any>, Set<UUID>>
    val current: Interactive?

    fun switchCurrent()
}
