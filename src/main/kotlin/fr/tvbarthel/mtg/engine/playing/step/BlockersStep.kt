package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * The declare blockers step is a step of the combat phase, where attacking creatures may be blocked.
 *
 * Creatures assigned in this step are blocking.
 *
 * https://mtg.gamepedia.com/Declare_blockers_step
 */
class BlockersStep() : Step {
    override fun proceed(gameState: GameState): GameState {
        return gameState
    }
}