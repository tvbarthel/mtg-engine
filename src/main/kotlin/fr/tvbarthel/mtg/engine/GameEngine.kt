package fr.tvbarthel.mtg.engine

/**
 * Game engine supposed to be able to play an MTG game.
 */
class GameEngine {

    /**
     * Simulate a complete game of MTG.
     * @param config input of the game - aka configuration of the game to simulate.
     * @return output of the game - ake game result.
     */
    fun simulate(config: GameConfig): GameResult {
        return GameResult(config)
    }
}