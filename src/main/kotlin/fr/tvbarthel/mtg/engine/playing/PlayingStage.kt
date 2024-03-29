package fr.tvbarthel.mtg.engine.playing

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Stage

/**
 * [Stage] which must ensure the opening stage.
 */
class PlayingStage : Stage {
    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        return state
    }
}