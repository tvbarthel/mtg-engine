package fr.tvbarthel.mtg.engine

/**
 * Encapsulate every game output.
 * @param config game input which lead to the given result.
 * @param finalState last state leading to the victory of one agent.
 */
data class GameResult(
    val config: GameConfig,
    val finalState: GameState
)