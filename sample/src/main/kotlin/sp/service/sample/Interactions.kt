package sp.service.sample

import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.service.sample.entity.Entities
import sp.service.sample.entity.Item

internal class Interactions(private val env: Environment) {
    private fun onInteractionItem(item: Item) {
        item.owner = env.player.id
    }

    private fun onInteraction() {
        val item = Entities.getNearestItem(
            target = env.player.moving.point,
            items = env.items,
            maxDistance = 1.75,
        )
        if (item != null) {
            onInteractionItem(item = item)
            return
        }
    }

    private fun onPressWalking(button: KeyboardButton) {
        when (button) {
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
        TODO("Interactions:onPressSwap($state, $button)")
    }

    fun onPress(button: KeyboardButton) {
        when (val state = env.state) {
            Environment.State.Walking -> onPressWalking(button = button)
            is Environment.State.Inventory -> onPressInventory(state = state, button = button)
            is Environment.State.Swap -> onPressSwap(state = state, button = button)
        }
    }
}
