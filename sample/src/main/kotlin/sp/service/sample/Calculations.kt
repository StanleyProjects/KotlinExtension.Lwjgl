package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.service.sample.entity.Entities

internal class Calculations(
    private val engine: Engine,
    private val env: Environment,
) {
    private val holder = MutableInteractiveHolder(timer = engine.timer)

    private fun getInteractive() {
        val item = Entities.getNearestItem(
            target = env.player.moving.point,
            items = env.items,
            maxDistance = 1.75,
        )
        if (item != null) {
            holder.putIfAbsent(item = item)
            return
        }
        val crate = Entities.getNearestCrate(
            target = env.player.moving.point,
            crates = env.crates,
            maxDistance = 1.75,
        )
        if (crate != null) {
            holder.putIfAbsent(crate = crate)
            return
        }
        holder.clear()
    }

    fun onRender() {
        getInteractive() // todo
    }

    fun getHolder(): InteractiveHolder {
        return holder
    }
}
