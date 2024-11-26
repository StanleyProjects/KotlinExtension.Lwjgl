package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.passed
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.lwjgl.provider.Times
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Entities
import sp.service.sample.entity.Interactive
import sp.service.sample.entity.Item
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class Calculations(
    private val engine: Engine,
    private val env: Environment,
) {
    private val holder = MutableInteractiveHolder(engine = engine)

    private fun getInteractive() {
        holder.putIfAbsent(
            mapOf(
                Item::class.java to Entities.getNearestItemIDs(
                    target = env.player.moving.point,
                    items = env.items,
                    maxDistance = 1.75,
                ),
                Crate::class.java to Entities.getNearestCrateIDs(
                    target = env.player.moving.point,
                    crates = env.crates,
                    maxDistance = 1.75,
                ),
            )
        )
    }

    private fun onInteractiveItem(id: UUID, time: Duration) {
        val item = env.items.firstOrNull { it.id == id } ?: TODO()
        // todo current ?
        val passed = engine.passed(KeyboardButton.F, time)
        if (passed) {
            item.owner = env.player.id
        }
    }

    private fun onInteractive(interactive: Interactive) {
        when {
            interactive.type.isAssignableFrom(Item::class.java) -> {
                onInteractiveItem(id = interactive.id, time = interactive.time)
            }
        }
    }

    fun onRender() {
        getInteractive() // todo
        val interactive = holder.current
        if (interactive != null) onInteractive(interactive = interactive)
    }

    fun getHolder(): InteractiveHolder {
        return holder
    }
}
