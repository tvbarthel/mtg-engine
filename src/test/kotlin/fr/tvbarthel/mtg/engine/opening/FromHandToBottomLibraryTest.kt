package fr.tvbarthel.mtg.engine.opening

import fr.tvbarthel.mtg.engine.Card
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Player
import fr.tvbarthel.mtg.engine.card.white.Plains
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import java.lang.IllegalArgumentException
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Ensure that [FromHandToBottomLibrary] behavior is the expected one and won't break in the future.
 */
class FromHandToBottomLibraryTest : StringSpec() {

    @RelaxedMockK
    lateinit var card: Card

    private var player: Player

    init {
        MockKAnnotations.init(this)

        player = Player(1)

        "given card inside hand when apply then card at bottom of library" {
            // given
            player.hand.add(card)
            repeat(10) { player.library.add(Plains()) }
            val state = GameState(12354890L, listOf(player))

            // when
            FromHandToBottomLibrary(card, 1).apply(state)

            // then
            assertFalse { player.hand.contains(card) }
            assertTrue { player.library.last() === card }
        }

        "given card not inside hand when apply then exception" {
            // given
            repeat(10) { player.library.add(Plains()) }
            val state = GameState(12354890L, listOf(player))

            // when
            shouldThrow<IllegalArgumentException> {
                FromHandToBottomLibrary(card, 1).apply(state)
            }
        }

    }
}