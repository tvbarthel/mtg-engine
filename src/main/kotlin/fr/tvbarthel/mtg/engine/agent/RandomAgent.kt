package fr.tvbarthel.mtg.engine.agent

import fr.tvbarthel.mtg.engine.Action
import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Logger
import kotlin.random.Random

/**
 * Agent taking random action.
 */
class RandomAgent(private val random: Random = Random) : Agent() {
    override fun chose(state: GameState, actions: List<Action>): Action {
        val action = actions.random(random)
        Logger.d("random agent: $action")
        return action
    }
}