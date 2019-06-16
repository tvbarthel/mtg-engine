package fr.tvbarthel.mtg.engine

import fr.tvbarthel.mtg.engine.opening.OpeningOutput
import fr.tvbarthel.mtg.engine.playing.PlayingOutput

/**
 * Encapsulate every game output.
 * @param config game input which lead to the given result.
 * @param openingResult result of the opening stage.
 * @param playingResult result of the playing stage.
 */
data class GameResult(
    val config: GameConfig,
    val openingResult: OpeningOutput,
    val playingResult: PlayingOutput
)