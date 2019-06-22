package fr.tvbarthel.mtg.engine.opening

import fr.tvbarthel.mtg.engine.Stage

/**
 * [Stage] which must ensure the opening stage.
 */
class OpeningStage : Stage<OpeningInput, OpeningOutput> {
    override fun proceed(input: OpeningInput): OpeningOutput {
        return OpeningOutput()
    }
}