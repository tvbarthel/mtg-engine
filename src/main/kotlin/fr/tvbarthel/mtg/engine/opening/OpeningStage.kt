package fr.tvbarthel.mtg.engine.opening

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Stage
import kotlin.random.Random

/**
 * [Stage] which must ensure the opening stage.
 */
class OpeningStage(
    private val startingHealth: Int = 20,
    private val startingHandSize: Int = 7
) : Stage {

    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        val random = Random(state.seed)

        // shuffle decks
        state.players.forEach { it.library.shuffle(random) }

        // chose the player to start
        val players = state.players.shuffled()

        // assign starting life
        players.forEach { it.health = startingHealth }

        // assign starting hand
        while (players.find { it.hand.isEmpty() } != null) {
            players
                .filter { it.hand.isEmpty() }
                .forEach { player ->
                    val hand = player.library.take(startingHandSize)
                    val actions = if (startingHandSize - player.mulligan > 1) {
                        listOf(KeepHandAction(hand, player.id), MulliganHandAction(hand, player.id))
                    } else {
                        listOf(KeepHandAction(hand, player.id))
                    }
                    agents.getValue(player.id).chose(state, actions).apply(state)
                }
        }

        // apply London mulligan once starting hand is chosen
        // https://mtg.gamepedia.com/Mulligan#London_mulligan
        players.forEach { player ->
            repeat(player.mulligan) {
                val actions = player.hand.map { card ->
                    FromHandToBottomLibrary(card, player.id)
                }
                agents.getValue(player.id).chose(state, actions).apply(state)
            }
        }

        return state
    }
}