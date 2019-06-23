package fr.tvbarthel.mtg.engine.playing

import fr.tvbarthel.mtg.engine.Agent
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
    fun play(agents: Map<Int, Agent>, state: GameState): GameState {
        var intermediateState = beginningPhase.proceed(agents, state)
        intermediateState = mainPhase.proceed(agents, intermediateState)
        intermediateState = combatPhase.proceed(agents, intermediateState)
        intermediateState = mainPhase.proceed(agents, intermediateState)
        return endingPhase.proceed(agents, intermediateState)
    }
}