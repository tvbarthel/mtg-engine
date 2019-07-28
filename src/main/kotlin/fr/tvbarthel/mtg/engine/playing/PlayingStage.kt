package fr.tvbarthel.mtg.engine.playing

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Stage
import fr.tvbarthel.mtg.engine.playing.phase.BeginningPhase
import fr.tvbarthel.mtg.engine.playing.phase.CombatPhase
import fr.tvbarthel.mtg.engine.playing.phase.EndingPhase
import fr.tvbarthel.mtg.engine.playing.phase.MainPhase

/**
 * [Stage] which must ensure the opening stage.
 */
class PlayingStage(
    val beginningPhase: BeginningPhase = BeginningPhase(),
    val mainPhase: MainPhase = MainPhase(),
    val combatPhase: CombatPhase = CombatPhase(),
    val endingPhase: EndingPhase = EndingPhase()
) : Stage {
    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        var tempState: GameState = state

        while (!state.isGameOver()) {
            val newTurn = Turn(beginningPhase, mainPhase, combatPhase, endingPhase)
            tempState = newTurn.play(agents, tempState)
            tempState.activePlayer = (tempState.activePlayer + 1) % tempState.players.size
            tempState.turn++
        }

        return tempState
    }
}