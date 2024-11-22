package sp.service.sample

import sp.kx.lwjgl.engine.TimeProvider
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Interactive
import sp.service.sample.entity.Item

internal class MutableInteractiveHolder(
    private val timer: TimeProvider,
) : InteractiveHolder {
    override var interactive: Interactive? = null
        private set

    fun putIfAbsent(item: Item) {
        val oldValue = interactive
        if (oldValue == null || oldValue.type != Interactive.Type.Item || oldValue.id != item.id) {
            println("Item: ${item.id}")
            interactive = Interactive(
                id = item.id,
                type = Interactive.Type.Item,
                time = timer.now(),
            )
        }
    }

    fun putIfAbsent(crate: Crate) {
        val oldValue = interactive
        if (oldValue == null || oldValue.type != Interactive.Type.Crate || oldValue.id != crate.id) {
            println("Crate: ${crate.id}")
            interactive = Interactive(
                id = crate.id,
                type = Interactive.Type.Crate,
                time = timer.now(),
            )
        }
    }

    fun clear() {
        if (interactive != null) {
            println("clear ${interactive?.type} ${interactive?.id}")
            interactive = null
        }
    }
}
