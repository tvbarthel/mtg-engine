package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * The untap step is the first step of the beginning phase. The following events happen during the untap step, in order:
 *
 * 1- All permanents with phasing controlled by the active player phase out, and all phased-out permanents that were
 * controlled by the active player simultaneously phase in.
 *
 * 2- The active player determines which permanents controlled by that player untap, then untaps all those permanents
 * simultaneously. (The player will untap all permanents he or she controls unless a card effect prevents this.)
 *
 * No player receives priority during this step so spells or abilities cannot be played. Any state-based effects or
 * triggered abilities that happen during this step are delayed until the upkeep step.
 *
 * https://mtg.gamepedia.com/Beginning_phase#Untap_step
 */
class UntapStep : Step {
    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        return state
    }
}