package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Phase

/**
 * The ending phase is the fifth and final phase of a turn.
 * Prior to the Magic 2010 rules changes, this phase was known as simply the end phase.
 * It consists of the following two steps:
 *
 * End step
 * Cleanup step
 *
 * https://mtg.gamepedia.com/Ending_phase
 */
class EndingPhase : Phase {
    override fun proceed(gameState: GameState): GameState {
        return gameState
    }
}