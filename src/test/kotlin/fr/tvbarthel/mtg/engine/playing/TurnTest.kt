package fr.tvbarthel.mtg.engine.playing

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.playing.phase.BeginningPhase
import fr.tvbarthel.mtg.engine.playing.phase.CombatPhase
import fr.tvbarthel.mtg.engine.playing.phase.EndingPhase
import fr.tvbarthel.mtg.engine.playing.phase.MainPhase
import io.kotlintest.specs.StringSpec
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verifyOrder

/**
 * Ensure that [Turn] behavior is the expected one and won't break in the future.
 */
class TurnTest : StringSpec() {

    @RelaxedMockK
    lateinit var beginningPhase: BeginningPhase

    @RelaxedMockK
    lateinit var mainPhase: MainPhase

    @RelaxedMockK
    lateinit var combatPhase: CombatPhase

    @RelaxedMockK
    lateinit var endingPhase: EndingPhase

    init {
        MockKAnnotations.init(this)

        "given empty state when play turn then right execution order" {
            // given
            val state = GameState(12354890L)

            // when
            Turn(beginningPhase, mainPhase, combatPhase, endingPhase).play(state)

            // then
            verifyOrder {
                beginningPhase.proceed(any())
                mainPhase.proceed(any())
                combatPhase.proceed(any())
                mainPhase.proceed(any())
                endingPhase.proceed(any())
            }
        }
    }
}