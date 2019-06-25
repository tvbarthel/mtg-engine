package fr.tvbarthel.mtg.engine

/**
 * Encapsulate the decision making for a given [Player].
 */
abstract class Agent {

    /**
     * Current agent must chose an action inside the list.
     */
    abstract fun chose(state: GameState, actions: List<Action>): Action

}