package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * The upkeep step is the second step of the beginning phase. At the beginning of the upkeep step, any abilities that
 * trigger either during the untap step or at the beginning of upkeep go on the stack. Then the active player gains
 * priority the first time during his or her turn.
 *
 * During this step, all upkeep costs are paid. The cost can be paid to gain something (Farmstead) or to prevent
 * a sacrifice (Force of Nature). The cost can also be cumulative.
 *
 * https://mtg.gamepedia.com/Beginning_phase#Upkeep_step
 */
class UpkeepStep : Step {
    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        return state
    }
}