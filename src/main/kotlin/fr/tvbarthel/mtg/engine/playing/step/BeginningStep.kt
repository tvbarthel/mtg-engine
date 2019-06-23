package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * First step of the Combat phase
 *
 * https://mtg.gamepedia.com/Beginning_of_combat_step
 */
class BeginningStep : Step {
    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        return state
    }

}