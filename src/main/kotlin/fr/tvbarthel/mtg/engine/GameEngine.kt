package fr.tvbarthel.mtg.engine

import fr.tvbarthel.mtg.engine.opening.OpeningStage
import fr.tvbarthel.mtg.engine.playing.PlayingStage

/**
 * Game engine supposed to be able to play an MTG game.
 */
class GameEngine(
    val openingStage: OpeningStage = OpeningStage(),
    val playingStage: PlayingStage = PlayingStage()
) {

    /**
     * Simulate a complete game of MTG.
     * @param config input of the game - aka configuration of the game to simulate.
     * @return output of the game - ake game result.
     */
    fun simulate(config: GameConfig): GameResult {
        var state = config.state

        if (config.state.turn == 0) {
            state = openingStage.proceed(config.agents, state)
        }

        state = playingStage.proceed(config.agents, state)

        return GameResult(config, state)
    }
}