package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Phase
import fr.tvbarthel.mtg.engine.playing.step.CleanupStep
import fr.tvbarthel.mtg.engine.playing.step.EndStep

/**
 * The ending phase is the fifth and final phase of a turn.
 * Prior to the Magic 2010 rules changes, this phase was known as simply the end phase.
 * It consists of the following two steps:
 *
 * End step
 * Cleanup step
 *
 * https://mtg.gamepedia.com/Ending_phase
 */
class EndingPhase(
    private val endStep: EndStep = EndStep(),
    private val cleanupStep: CleanupStep = CleanupStep()
) : Phase {
    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        val intermediateState = endStep.proceed(agents, state)
        return cleanupStep.proceed(agents, intermediateState)
    }
}