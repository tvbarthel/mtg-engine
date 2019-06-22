package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * The cleanup step is the second and final step of the ending phase. Spells and abilities may be played during this
 * step only if the conditions for any state-based actions exist or if any abilities have triggered. In that case,
 * those state-based actions are performed and/or those abilities go on the stack and the active player gets priority
 * and players may cast spells and activate abilities. Once all players pass priority when the stack is empty,
 * the step repeats.
 *
 * https://mtg.gamepedia.com/Ending_phase#Cleanup_step
 */
class CleanupStep() : Step {
    override fun proceed(gameState: GameState): GameState {
        return gameState
    }
}