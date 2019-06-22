package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Phase
import fr.tvbarthel.mtg.engine.playing.step.DrawStep
import fr.tvbarthel.mtg.engine.playing.step.UntapStep
import fr.tvbarthel.mtg.engine.playing.step.UpkeepStep

/**
 * The beginning phase is the first phase in a turn. It consists of three steps, in order:
 * Untap step
 * Upkeep step
 * Draw step
 *
 * https://mtg.gamepedia.com/Beginning_phase
 */
class BeginningPhase(
    private val untapStep: UntapStep = UntapStep(),
    private val upkeepStep: UpkeepStep = UpkeepStep(),
    private val drawStep: DrawStep = DrawStep()
) : Phase {
    override fun proceed(gameState: GameState): GameState {
        var intermediateState = untapStep.proceed(gameState)
        intermediateState = upkeepStep.proceed(intermediateState)
        return drawStep.proceed(intermediateState)
    }
}