package fr.tvbarthel.mtg.engine.playing

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.playing.phase.BeginningPhase
import fr.tvbarthel.mtg.engine.playing.phase.CombatPhase
import fr.tvbarthel.mtg.engine.playing.phase.EndingPhase
import fr.tvbarthel.mtg.engine.playing.phase.MainPhase

/**
 * Encapsulate logic and workflow of a single game turn.
 *
 * https://mtg.gamepedia.com/Turn_structure
 */
class Turn(
    val beginningPhase: BeginningPhase,
    val mainPhase: MainPhase,
    val combatPhase: CombatPhase,
    val endingPhase: EndingPhase
) {

    /**
     * Play the given turn.
     */
    fun play(gameState: GameState): GameState {
        var intermediateState = beginningPhase.proceed(gameState)
        intermediateState = mainPhase.proceed(intermediateState)
        intermediateState = combatPhase.proceed(intermediateState)
        intermediateState = mainPhase.proceed(intermediateState)
        return endingPhase.proceed(intermediateState)
    }
}