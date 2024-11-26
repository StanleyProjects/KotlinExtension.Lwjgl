package sp.service.sample

import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Interactive
import sp.service.sample.entity.Item

internal class Interactions(
    private val env: Environment,
    private val holder: InteractiveHolder,
) {
    private fun onInteractionCrate(crate: Crate) {
        env.state = Environment.State.Swap(
            index = 0,
            side = true,
            src = env.player.id,
            dst = crate.id,
        )
    }

    private fun onInteraction() {
        val interactive = holder.current ?: return
        when {
            interactive.type.isAssignableFrom(Crate::class.java) -> {
                val crate = env.crates.firstOrNull { it.id == interactive.id } ?: TODO()
                onInteractionCrate(crate = crate)
            }
        }
    }

    private fun onPressWalking(button: KeyboardButton) {
        when (button) {
            KeyboardButton.RIGHT -> holder.switchCurrent()
            KeyboardButton.F -> onInteraction()
            KeyboardButton.TAB -> {
                env.state = Environment.State.Inventory(index = 0)
            }
            else -> Unit
        }
    }

    private fun onPressInventory(state: Environment.State.Inventory, button: KeyboardButton) {
        when (button) {
            KeyboardButton.TAB, KeyboardButton.ESCAPE -> {
                env.state = Environment.State.Walking
            }
            else -> Unit
        }
        val items = env.items.filter { it.owner == env.player.id }
        if (items.isEmpty()) return
        when (button) {
            KeyboardButton.W -> {
                state.index = (items.size + state.index - 1) % items.size
            }
            KeyboardButton.S -> {
                state.index = (state.index + 1) % items.size
            }
            KeyboardButton.X -> {
                val item = items[state.index]
                item.point.set(env.player.moving.point)
                item.owner = null
                if (state.index == items.lastIndex) {
                    state.index = (items.size + state.index - 1) % items.size
                }
            }
            else -> Unit
        }
    }

    private fun onPressSwap(state: Environment.State.Swap, button: KeyboardButton) {
        when (button) {
            KeyboardButton.ESCAPE -> {
                env.state = Environment.State.Walking
            }
            else -> Unit
        }
        val owner = if (state.side) state.src else state.dst
        val items = env.items.filter { it.owner == owner }
        if (items.isNotEmpty()) when (button) {
            KeyboardButton.W -> {
                state.index = (items.size + state.index - 1) % items.size
            }
            KeyboardButton.S -> {
                state.index = (state.index + 1) % items.size
            }
            KeyboardButton.F -> {
                val item = items[state.index]
                item.point.set(env.player.moving.point)
                item.owner = if (state.side) state.dst else state.src
                if (state.index == items.lastIndex) {
                    state.index = (items.size + state.index - 1) % items.size
                }
            }
            else -> Unit
        }
        when (button) {
            KeyboardButton.A, KeyboardButton.D -> {
                state.index = 0
                state.side = !state.side
            }
            else -> Unit
        }
    }

    fun onPress(button: KeyboardButton) {
        when (val state = env.state) {
            Environment.State.Walking -> onPressWalking(button = button)
            is Environment.State.Inventory -> onPressInventory(state = state, button = button)
            is Environment.State.Swap -> onPressSwap(state = state, button = button)
        }
    }
}
