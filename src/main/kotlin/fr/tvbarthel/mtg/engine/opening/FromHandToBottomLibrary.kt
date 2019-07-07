package fr.tvbarthel.mtg.engine.opening

import fr.tvbarthel.mtg.engine.Action
import fr.tvbarthel.mtg.engine.Card
import fr.tvbarthel.mtg.engine.Event
import fr.tvbarthel.mtg.engine.GameState

/**
 * Action used to remove a card from the player hand and put it inside the player library at the bottom.
 * @param card card which must be removed from the user hand and put at the bottom of the player library.
 * @param playerId id of the player holding the card.
 */
class FromHandToBottomLibrary(
    private val card: Card,
    private val playerId: Int
) : Action() {
    override fun apply(state: GameState): List<Event> {
        val player = state.players.first { it.id == playerId }
        val removed = player.hand.remove(card)
        if (!removed) {
            throw IllegalArgumentException("Card ${card} not present inside the player hand ${player.hand}")
        }
        player.library.add(card)
        return emptyList()
    }

}