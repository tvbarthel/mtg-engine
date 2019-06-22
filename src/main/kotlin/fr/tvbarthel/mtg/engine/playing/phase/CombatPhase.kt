package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Phase

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
class CombatPhase : Phase {
    override fun proceed(gameState: GameState): GameState {
        return gameState
    }
}