package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * The declare attackers step is a step of the combat phase, where creatures may be assigned to attack.
 *
 * Creatures assigned in this step are attacking.
 *
 * https://mtg.gamepedia.com/Declare_attackers_step
 */
class AttackersStep() : Step {
    override fun proceed(gameState: GameState): GameState {
        return gameState
    }
}