package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.service.sample.entity.Interactive
import java.util.UUID

internal class MutableInteractiveHolder(
    private val engine: Engine,
) : InteractiveHolder {
    override var map: Map<Class<out Any>, Set<UUID>> = emptyMap()
        private set

    override var current: Interactive? = null
        private set

    private fun hasCurrent(): Boolean {
        val current = current ?: return false
        return map[current.type]?.contains(current.id) ?: false
    }

    fun putIfAbsent(entries: Map<Class<out Any>, Set<UUID>>) {
        map = entries.filterValues { it.isNotEmpty() }
        if (!hasCurrent()) {
            val entry = map.entries.firstOrNull()
            if (entry == null) {
                current = null
                return
            }
            val id = entry.value.firstOrNull() ?: TODO("No ids!")
            current = Interactive(
                type = entry.key,
                id = id,
                time = engine.property.time.b,
            )
        }
    }

    override fun switchCurrent() {
        val current = current ?: return
        val values = map.flatMap { (type, ids) -> ids.map { type to it } }
        if (values.size == 1) return
        for (i in values.indices) {
            val (type, id) = values[i]
            if (type == current.type && id == current.id) {
                val index = if (i == values.lastIndex) 0 else i + 1
                val value = values[index]
                this.current = Interactive(
                    type = value.first,
                    id = value.second,
                    time = engine.property.time.b,
                )
                return
            }
        }
    }
}
