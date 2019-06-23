package fr.tvbarthel.mtg.engine

data class GameState(
    val seed: Long,
    val players: List<Player> = mutableListOf(),
    val turn: Int = 0
)