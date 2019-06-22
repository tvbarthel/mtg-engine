package fr.tvbarthel.mtg.engine

import fr.tvbarthel.mtg.engine.opening.OpeningInput
import fr.tvbarthel.mtg.engine.opening.OpeningStage
import fr.tvbarthel.mtg.engine.playing.PlayingInput
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
        val openingOutput = openingStage.proceed(OpeningInput())
        val playingOutput = playingStage.proceed(PlayingInput())
        return GameResult(config, openingOutput, playingOutput)
    }
}