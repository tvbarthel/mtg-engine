package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Phase

/**
 * The main phase is both the second and fourth phases of a turn.
 * Non-instants can usually only be played during this phase, only by the active player, and only when the stack is empty.
 *
 * The following events occur during the main phase:
 * Abilities that trigger at the beginning of the main phase go onto the stack.
 * The active player gains priority.
 *
 * Once per turn, the active player may play a land from his or her hand during this phase while the stack is empty.
 * This is considered a Special Action which does not use the stack.
 *
 * When both players yield priority in succession while the stack is empty during the pre-combat main phase,
 * the game proceeds to the combat phase. After the combat phase is complete, the game proceeds to the post-combat main phase.
 *
 * When both players yield priority in succession while the stack is empty during the post-combat main phase,
 * the game proceeds to the end phase.
 *
 * https://mtg.gamepedia.com/Main_phase
 */
class MainPhase : Phase {
    override fun proceed(gameState: GameState): GameState {
        return gameState
    }
}