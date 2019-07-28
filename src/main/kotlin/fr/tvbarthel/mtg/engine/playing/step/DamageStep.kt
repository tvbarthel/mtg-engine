package fr.tvbarthel.mtg.engine.playing.step

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Step

/**
 * Combat damage is a special kind of damage that is dealt by creatures during combat.
 *
 * https://mtg.gamepedia.com/Combat_damage_step
 */
class DamageStep : Step {
    override fun proceed(agents: Map<Int, Agent>, state: GameState): GameState {
        state.nonActivePlayers().forEach { it.health = 0 }
        return state
    }
}