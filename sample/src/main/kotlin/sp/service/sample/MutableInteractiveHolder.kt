package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Interactive
import sp.service.sample.entity.Item

internal class MutableInteractiveHolder(
    private val engine: Engine,
) : InteractiveHolder {
    override var interactive: Interactive? = null
        private set

    fun putIfAbsent(item: Item) {
        val oldValue = interactive
        if (oldValue is Interactive.Item && oldValue.id == item.id) return
        println("Item: ${item.id}")
        interactive = Interactive.Item(
            id = item.id,
            time = engine.property.time.b,
        )
    }

    fun putIfAbsent(crate: Crate) {
        val oldValue = interactive
        if (oldValue is Interactive.Crate && oldValue.id == crate.id) return
        println("Crate: ${crate.id}")
        interactive = Interactive.Crate(
            id = crate.id,
        )
    }

    fun clear() {
        if (interactive != null) {
            println("clear $interactive")
            interactive = null
        }
    }
}
