package fr.tvbarthel.mtg.engine.playing

import fr.tvbarthel.mtg.engine.Stage

/**
 * [Stage] which must ensure the opening stage.
 */
class PlayingStage : Stage<PlayingInput, PlayingOutput> {
    override fun proceed(input: PlayingInput): PlayingOutput {
        return PlayingOutput()
    }
}