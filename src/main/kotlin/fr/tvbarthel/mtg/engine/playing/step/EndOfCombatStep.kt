package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * Last step of the combat phase.
 *
 * https://mtg.gamepedia.com/End_of_combat_step
 */
class EndOfCombatStep() : Step {
    override fun proceed(gameState: GameState): GameState {
        return gameState
    }
}