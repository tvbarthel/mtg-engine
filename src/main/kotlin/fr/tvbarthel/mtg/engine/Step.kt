package fr.tvbarthel.mtg.engine

/**
 * Encapsulate sub-part of a game [Phase].
 */
interface Step {

    /**
     * Must proceed to the given step.
     *
     * i.e. based on the current state, apply change and output the resulting state.
     */
    fun proceed(agents: Map<Int, Agent>, state: GameState): GameState
}