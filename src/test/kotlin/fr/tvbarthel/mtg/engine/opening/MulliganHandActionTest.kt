package fr.tvbarthel.mtg.engine.opening

import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Player
import fr.tvbarthel.mtg.engine.playing.zone.Hand
import fr.tvbarthel.mtg.engine.playing.zone.Library
import io.kotlintest.specs.StringSpec
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import kotlin.test.assertEquals

/**
 * Ensure that [MulliganHandAction] behavior is the expected one and won't break in the future.
 */
class MulliganHandActionTest : StringSpec() {
    @RelaxedMockK
    lateinit var mockedState: GameState

    @RelaxedMockK
    lateinit var mockedPlayer1: Player

    @RelaxedMockK
    lateinit var mockedPlayer2: Player

    @SpyK
    var mockedLibrary = Library()

    @SpyK
    var mockedHand = Hand()

    init {
        MockKAnnotations.init(this)

        every { mockedPlayer1.id } returns 1

        every { mockedPlayer1.hand } returns mockedHand
        every { mockedPlayer1.library } returns mockedLibrary

        every { mockedState.players } returns listOf(mockedPlayer1, mockedPlayer2)

        "given hand when apply then player hand empty"{
            // given
            mockedLibrary.addAll(listOf(mockk(), mockk(), mockk(), mockk()))
            val hand = mockedLibrary.take(3)

            // when
            MulliganHandAction(hand, 1).apply(mockedState)

            // then
            assert(mockedHand.isEmpty())
            assertEquals(4, mockedLibrary.size)
            verify { mockedPlayer2 wasNot Called }
        }
    }

}