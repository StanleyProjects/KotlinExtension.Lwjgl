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

    override fun switchCurrent() {
        val current = current ?: return
        val values = map.flatMap { (type, ids) -> ids.map { Interactive(type = type, id = it.key, time = it.value) } }
        for (i in values.indices) {
            val value = values[i]
            if (value.type == current.type && value.id == current.id) {
                val index = if (i == values.lastIndex) 0 else i + 1
                this.current = values[index]
                return
            }
        }
    }
}
