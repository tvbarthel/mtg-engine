package fr.tvbarthel.mtg.engine

/**
 * Encapsulate every game output.
 * @param config game input which lead to the given result.
 * @param turn number of total turn played for this game.
 */
data class GameResult(val config: GameConfig, val turn: Int = 0)