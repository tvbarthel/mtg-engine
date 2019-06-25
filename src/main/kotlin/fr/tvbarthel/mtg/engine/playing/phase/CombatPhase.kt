package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Phase
import fr.tvbarthel.mtg.engine.playing.step.*

/**
 * The combat phase is the third phase in a turn, and has five steps in this order:
 * Beginning of Combat Step
 * Declare Attackers Step
 * Declare Blockers Step
 * Combat Damage Step
 * End of Combat Step
 *
 * If no creatures are declared as attackers, the declare blockers step and combat damage step is skipped.
 * If any attacking or blocking creatures has first strike or double strike, there are two combat damage steps.
 *
 * https://mtg.gamepedia.com/Combat_phase
 */
class CombatPhase(
    private val beginningStep: BeginningStep = BeginningStep(),
    private val attackersStep: AttackersStep = AttackersStep(),
    private val blockersStep: BlockersStep = BlockersStep(),
    private val damageStep: DamageStep = DamageStep(),
    private val endOfCombatStep: EndOfCombatStep = EndOfCombatStep()
) : Phase {
    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        var intermediateState = beginningStep.proceed(agents, state)
        intermediateState = attackersStep.proceed(agents, intermediateState)
        intermediateState = blockersStep.proceed(agents, intermediateState)
        intermediateState = damageStep.proceed(agents, intermediateState)
        return endOfCombatStep.proceed(agents, intermediateState)
    }
}