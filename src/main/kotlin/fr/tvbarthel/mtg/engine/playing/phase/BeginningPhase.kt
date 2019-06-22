package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Phase

/**
 * The beginning phase is the first phase in a turn. It consists of three steps, in order:
 * Untap step
 * Upkeep step
 * Draw step
 *
 * https://mtg.gamepedia.com/Beginning_phase
 */
class BeginningPhase : Phase {
    override fun proceed(gameState: GameState): GameState {
        return gameState
    }
}