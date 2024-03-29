package fr.tvbarthel.mtg.engine.opening

import fr.tvbarthel.mtg.engine.Action
import fr.tvbarthel.mtg.engine.Card
import fr.tvbarthel.mtg.engine.Event
import fr.tvbarthel.mtg.engine.GameState
import kotlin.random.Random

/**
 * Action used to mulligan the given hand and request a new staring hand.
 */
class MulliganHandAction(val hand: List<Card>, private val playerId: Int) : Action() {
    override fun apply(state: GameState): List<Event> {
        state.players.find { it.id == playerId }!!.let { player ->
            player.mulligan += 1
            player.library.shuffle(Random(state.seed))
        }
        return emptyList()
    }

    override fun toString(): String = "Mulligan hand $hand"
}