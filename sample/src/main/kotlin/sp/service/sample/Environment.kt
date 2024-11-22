package sp.service.sample

import sp.kx.math.Vector
import sp.service.sample.entity.Barrier
import sp.service.sample.entity.Condition
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Item
import sp.service.sample.entity.Player
import sp.service.sample.entity.Relay
import java.util.UUID

internal class Environment(
    var state: State,
    val walls: List<Vector>,
    val player: Player,
    val conditions: List<Condition>,
    val barriers: List<Barrier>,
    val relays: List<Relay>,
    val items: List<Item>,
    val crates: List<Crate>,
) {
    sealed interface State {
        data object Walking : State
        class Inventory(var index: Int) : State
        class Swap(
            var index: Int,
            var side: Boolean,
            val src: UUID,
            val dst: UUID,
        ) : State
    }
}
