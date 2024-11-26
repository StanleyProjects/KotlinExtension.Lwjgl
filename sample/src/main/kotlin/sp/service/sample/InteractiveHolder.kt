package sp.service.sample

import sp.service.sample.entity.Interactive
import java.util.UUID
import kotlin.time.Duration

internal interface InteractiveHolder {
    val map: Map<Class<out Any>, Map<UUID, Duration>>
    val current: Interactive?
}
