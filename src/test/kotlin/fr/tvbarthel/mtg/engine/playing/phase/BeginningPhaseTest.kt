package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.playing.step.DrawStep
import fr.tvbarthel.mtg.engine.playing.step.UntapStep
import fr.tvbarthel.mtg.engine.playing.step.UpkeepStep
import io.kotlintest.specs.StringSpec
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verifyOrder

/**
 * Ensure that [BeginningPhase] behavior is the expected one and won't break in the future.
 */
class BeginningPhaseTest : StringSpec() {

    @RelaxedMockK
    lateinit var untapStep: UntapStep

    @RelaxedMockK
    lateinit var upkeepStep: UpkeepStep

    @RelaxedMockK
    lateinit var drawStep: DrawStep

    @RelaxedMockK
    lateinit var agents: Map<Int, Agent>

    init {
        MockKAnnotations.init(this)

        "given empty state when proceed then step executed in right order"{
            // given
            val state = GameState(1234718L)

            // when
            BeginningPhase(untapStep, upkeepStep, drawStep).proceed(agents, state)

            // then
            verifyOrder {
                untapStep.proceed(eq(agents), any())
                upkeepStep.proceed(eq(agents), any())
                drawStep.proceed(eq(agents), any())
            }
        }
    }

}