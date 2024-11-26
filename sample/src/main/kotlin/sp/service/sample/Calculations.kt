package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.passed
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.provider.Times
import sp.service.sample.entity.Entities
import sp.service.sample.entity.Interactive
import kotlin.time.Duration.Companion.seconds

internal class Calculations(
    private val engine: Engine,
    private val env: Environment,
) {
    private val holder = MutableInteractiveHolder(engine = engine)

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

    private fun onInteractive(interactive: Interactive.Item) {
        val item = env.items.firstOrNull { it.id == interactive.id } ?: TODO()
        val passed = engine.passed(KeyboardButton.F, interactive.time)
        if (passed) {
            item.owner = env.player.id
        }
    }

    private fun onInteractive(interactive: Interactive) {
        when (interactive) {
            is Interactive.Item -> onInteractive(interactive = interactive)
            else -> Unit
        }
    }

    fun onRender() {
        getInteractive() // todo
        val interactive = holder.interactive
        if (interactive != null) onInteractive(interactive = interactive)
    }

    fun getHolder(): InteractiveHolder {
        return holder
    }
}
