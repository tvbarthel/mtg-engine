package fr.tvbarthel.mtg.engine.agent

import fr.tvbarthel.mtg.engine.Action
import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import kotlin.random.Random

/**
 * Agent taking random action.
 */
class RandomAgent(private val random: Random) : Agent() {
    override fun chose(state: GameState, actions: List<Action>): Action {
        return actions.random(random)
    }
}