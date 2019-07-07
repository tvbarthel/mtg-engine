package fr.tvbarthel.mtg.engine.opening

import fr.tvbarthel.mtg.engine.Action
import fr.tvbarthel.mtg.engine.Card
import fr.tvbarthel.mtg.engine.Event
import fr.tvbarthel.mtg.engine.GameState

/**
 * Action used to keep the given hand and use it as starting hand.
 */
class KeepHandAction(val hand: List<Card>, private val playerId: Int) : Action() {
    override fun apply(state: GameState): List<Event> {
        val player = state.players.find { it.id == playerId }!!
        player.library.removeAll(hand)
        player.hand.addAll(hand)
        return emptyList()
    }
}