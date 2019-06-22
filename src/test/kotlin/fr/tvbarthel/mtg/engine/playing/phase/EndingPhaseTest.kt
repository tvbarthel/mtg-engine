package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.playing.step.CleanupStep
import fr.tvbarthel.mtg.engine.playing.step.EndStep
import io.kotlintest.specs.StringSpec
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verifyOrder

/**
 * Ensure that [EndingPhase] behavior is the expected one and won't break in the future.
 */
class EndingPhaseTest : StringSpec() {

    @RelaxedMockK
    lateinit var endStep: EndStep

    @RelaxedMockK
    lateinit var cleanupStep: CleanupStep

    init {
        MockKAnnotations.init(this)

        "given empty state when proceed then step executed in right order"{
            // given
            val state = GameState(1234789L)

            // when
            EndingPhase(endStep, cleanupStep).proceed(state)

            // then
            verifyOrder {
                endStep.proceed(any())
                cleanupStep.proceed(any())
            }
        }
    }
}