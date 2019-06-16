package fr.tvbarthel.mtg.engine

/**
 * Encapsulate every game inputs.
 *
 * @param seed game seed used for every random function. Same seed must result in the same game output.
 */
data class GameConfig(val seed: Long = System.currentTimeMillis())