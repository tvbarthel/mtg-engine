package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * The end step, often referred to as the "end of turn" and previously known as the "end of turn step", is the first
 * step of the ending phase. It is usually the last opportunity for a player to perform any action before the next
 * player's turn starts
 *
 * https://mtg.gamepedia.com/Ending_phase#End_step
 */
class EndStep() : Step {
    override fun proceed(gameState: GameState): GameState {
        return gameState
    }
}