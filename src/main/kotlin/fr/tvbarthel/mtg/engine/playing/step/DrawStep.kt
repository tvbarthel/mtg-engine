package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * The draw step is the third step of the beginning phase. The following events occur during this phase, in order:
 *
 * 1- The active player draws a card from their library.
 *
 * 2- Any abilities that trigger at the beginning of the draw step go on the stack.
 *
 * 3- The active player gains priority.
 *
 * https://mtg.gamepedia.com/Beginning_phase#Draw_step
 */
class DrawStep : Step {
    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        return state
    }
}