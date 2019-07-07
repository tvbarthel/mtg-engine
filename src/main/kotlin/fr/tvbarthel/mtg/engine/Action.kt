package fr.tvbarthel.mtg.engine

/**
 * Encapsulate an action that can be added to the stack and eventually applied.
 */
abstract class Action {

    /**
     * Must apply the given action to the current state.
     */
    abstract fun apply(state: GameState): List<Event>
}