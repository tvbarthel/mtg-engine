package fr.tvbarthel.mtg.engine

/**
 * Describe the contract of every step of a Magic game.
 *
 * A Phase must be seen as a pure function and should be stateless.
 *
 * https://mtg.gamepedia.com/Turn_structure
 *
 * https://mtg.gamepedia.com/Comprehensive_Rules
 * 500.1. A turn consists of five phases, in this order:
 * beginning, precombat main, combat, postcombat main, and ending.
 * Each of these phases takes place every turn, even if nothing happens during the phase.
 * The beginning, combat, and ending phases are further broken down into steps, which proceed in order.
 */
interface Phase {

    /**
     * Must proceed to the given step.
     *
     * i.e. based on the current state, apply change and output the resulting state.
     *
     * @param agents list of decision maker agent mapped by player ids.
     * @param state state from which the phase must start.
     * @return new state once the phase a finished.
     */
    fun proceed(agents: Map<Int, Agent>, state: GameState): GameState
}