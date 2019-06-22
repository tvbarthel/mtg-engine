package fr.tvbarthel.mtg.engine.playing.phase

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.playing.step.*
import io.kotlintest.specs.StringSpec
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verifyOrder

/**
 * Ensure that [CombatPhase] behavior is the expected one and won't break in the future.
 */
class CombatPhaseTest : StringSpec() {

    @RelaxedMockK
    lateinit var beginningStep: BeginningStep

    @RelaxedMockK
    lateinit var attackersStep: AttackersStep

    @RelaxedMockK
    lateinit var blockersStep: BlockersStep

    @RelaxedMockK
    lateinit var damageStep: DamageStep

    @RelaxedMockK
    lateinit var endOfCombatStep: EndOfCombatStep

    init {
        MockKAnnotations.init(this)

        "given empty test when proceed then step executed in right order"{
            // given
            val state = GameState(1239407L)

            // when
            CombatPhase(beginningStep, attackersStep, blockersStep, damageStep, endOfCombatStep).proceed(state)

            // then
            verifyOrder {
                beginningStep.proceed(any())
                attackersStep.proceed(any())
                blockersStep.proceed(any())
                damageStep.proceed(any())
                endOfCombatStep.proceed(any())
            }
        }
    }
}