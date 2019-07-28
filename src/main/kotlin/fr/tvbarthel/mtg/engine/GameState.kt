package fr.tvbarthel.mtg.engine

data class GameState(
    val seed: Long,
    val players: List<Player> = mutableListOf(),
    var turn: Int = 0,
    var startingPlayer: Int = 0,    /* index of the starting player */
    var activePlayer: Int = 0       /* index of the current active player */
) {

    /**
     * Used to know if the game has ended or not.
     */
    fun isGameOver(): Boolean = players.find { it.health == 0 } != null

    /**
     * Retrieve the current active player.
     */
    fun activePlayer(): Player = players[activePlayer]

    /**
     * Retrieve current non actives players.
     */
    fun nonActivePlayers(): List<Player> = players.filterIndexed { index, player -> index != activePlayer }
}