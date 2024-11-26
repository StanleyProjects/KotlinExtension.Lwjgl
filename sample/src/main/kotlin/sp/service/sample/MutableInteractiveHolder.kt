package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.service.sample.entity.Interactive
import java.util.UUID
import kotlin.time.Duration

internal class MutableInteractiveHolder(
    private val engine: Engine,
) : InteractiveHolder {
    override var map: Map<Class<out Any>, Map<UUID, Duration>> = emptyMap()
        private set

    override var current: Interactive? = null
        private set

    private fun hasCurrent(): Boolean {
        val current = current ?: return false
        return map[current.type]?.containsKey(current.id) ?: false
    }

    fun putIfAbsent(entries: Map<Class<out Any>, Set<UUID>>) {
        map = HashMap<Class<out Any>, Map<UUID, Duration>>().also {
            for (entry in entries) {
                if (entry.value.isEmpty()) continue
                it[entry.key] = entry.value.associateWith { id ->
                    map[entry.key]?.get(id) ?: engine.property.time.b
                }
            }
        }
        if (!hasCurrent()) {
            val type = map.keys.firstOrNull()
            if (type == null) {
                current = null
                return
            }
            val (id, time) = map[type]?.entries?.firstOrNull() ?: TODO()
            current = Interactive(
                type = type,
                id = id,
                time = time,
            )
        }
    }
}
