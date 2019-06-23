package fr.tvbarthel.mtg.engine

/**
 * Encapsulate every game inputs.
 *
 * @param state from which the game should start.
 * @param agents agent taking decision for players mapped by player id.
 */
data class GameConfig(
    val state: GameState,
    val agents: Map<Int, Agent>,
    val seed: Long = System.currentTimeMillis()
) {

    /**
     * Used to know if the given config is valid.
     */
    fun isValid(): Boolean {
        return state.players.map { agents.containsKey(it.id) }.contains(false).not()
    }
}