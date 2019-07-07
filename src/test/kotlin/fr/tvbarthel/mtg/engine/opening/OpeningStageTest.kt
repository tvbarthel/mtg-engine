package fr.tvbarthel.mtg.engine.opening

import fr.tvbarthel.mtg.engine.*
import io.kotlintest.TestCase
import io.kotlintest.specs.StringSpec
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlin.test.assertEquals

/**
 * Ensure that [OpeningStage] behavior is the expected one and won't break in the future.
 */
class OpeningStageTest : StringSpec() {

    @RelaxedMockK
    lateinit var agent1: Agent

    @RelaxedMockK
    lateinit var agent2: Agent

    private lateinit var player1: Player

    private lateinit var player2: Player

    private lateinit var agents: Map<Int, Agent>

    private lateinit var players: MutableList<Player>

    private lateinit var agent1ActionChooser: (List<Action>) -> Action

    private lateinit var agent2ActionChooser: (List<Action>) -> Action

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

        MockKAnnotations.init(this)
        player1 = Player(1)
        repeat(60) { player1.library.add(Card()) }

        player2 = Player(2)
        repeat(60) { player2.library.add(Card()) }

        agents = mapOf(player1.id to agent1, player2.id to agent2)
        players = mutableListOf(player1, player2)

        every { agent1.chose(any(), any()) }.answers { agent1ActionChooser.invoke(secondArg()) }
        every { agent2.chose(any(), any()) }.answers { agent2ActionChooser.invoke(secondArg()) }
    }

    init {

        "given first draw when both player keep hand then hand 7 and 7 cards" {
            // given
            val state = GameState(12354890L, players)
            agent1ActionChooser = { actions -> actions.first { action -> action is KeepHandAction } }
            agent2ActionChooser = { actions -> actions.first { action -> action is KeepHandAction } }

            //  when
            OpeningStage().proceed(agents, state)

            // then
            assertEquals(7, state.players[0].hand.size)
            assertEquals(7, state.players[1].hand.size)
            assertEquals(53, state.players[0].library.size)
            assertEquals(53, state.players[1].library.size)
            assertEquals(20, state.players[0].health)
            assertEquals(20, state.players[1].health)
        }

        "given previous draw with mulligan 1 and 0 when both player keep hand then hand 7 and 6 cards" {
            // given
            val state = GameState(12354890L, players)
            agent1ActionChooser =
                { actions -> actions.first { action -> action is KeepHandAction || action is FromHandToBottomLibrary } }
            agent2ActionChooser = { actions -> actions.first { action -> action is KeepHandAction } }
            player1.mulligan = 1
            player2.mulligan = 0

            //  when
            OpeningStage().proceed(agents, state)

            // then
            assertEquals(6, state.players[0].hand.size)
            assertEquals(7, state.players[1].hand.size)
            assertEquals(54, state.players[0].library.size)
            assertEquals(53, state.players[1].library.size)
            assertEquals(20, state.players[0].health)
            assertEquals(20, state.players[1].health)
        }
//
        "given previous draw with mulligan 2 and 0 when both player keep hand then hand 7 and 6 cards" {
            // given
            val state = GameState(12354890L, players)
            agent1ActionChooser =
                { actions -> actions.first { action -> action is KeepHandAction || action is FromHandToBottomLibrary } }
            agent2ActionChooser = { actions -> actions.first { action -> action is KeepHandAction } }
            player1.mulligan = 2
            player2.mulligan = 0

            //  when
            OpeningStage().proceed(agents, state)

            // then
            assertEquals(5, state.players[0].hand.size)
            assertEquals(7, state.players[1].hand.size)
            assertEquals(55, state.players[0].library.size)
            assertEquals(53, state.players[1].library.size)
            assertEquals(20, state.players[0].health)
            assertEquals(20, state.players[1].health)
        }

        "given first draw when player 1 mulligan 4 times and player 2 keep then hand 3 and 7 cards" {
            // given
            val state = GameState(12354890L, players)
            agent1ActionChooser =
                { actions -> actions.first { action -> if (player1.mulligan < 4) action is MulliganHandAction else (action is KeepHandAction || action is FromHandToBottomLibrary) } }
            agent2ActionChooser = { actions -> actions.first { action -> action is KeepHandAction } }
            player1.mulligan = 0
            player2.mulligan = 0

            //  when
            OpeningStage().proceed(agents, state)

            // then
            assertEquals(3, state.players[0].hand.size)
            assertEquals(7, state.players[1].hand.size)
            assertEquals(57, state.players[0].library.size)
            assertEquals(53, state.players[1].library.size)
            assertEquals(20, state.players[0].health)
            assertEquals(20, state.players[1].health)
        }

        "given first draw when player 1 mulligan 4 times and player 2 mulligan 2 times then hand 3 and 5 cards" {
            // given
            val state = GameState(12354890L, players)
            agent1ActionChooser =
                { actions -> actions.first { action -> if (player1.mulligan < 4) action is MulliganHandAction else (action is KeepHandAction || action is FromHandToBottomLibrary) } }
            agent2ActionChooser =
                { actions -> actions.first { action -> if (player2.mulligan < 2) action is MulliganHandAction else (action is KeepHandAction || action is FromHandToBottomLibrary) } }
            player1.mulligan = 0
            player2.mulligan = 0

            //  when
            OpeningStage().proceed(agents, state)

            // then
            assertEquals(3, state.players[0].hand.size)
            assertEquals(5, state.players[1].hand.size)
            assertEquals(57, state.players[0].library.size)
            assertEquals(55, state.players[1].library.size)
            assertEquals(20, state.players[0].health)
            assertEquals(20, state.players[1].health)
        }

        "given first draw when player 1 mulligan every time possible player 2 keep then hand 1 and 7 cards" {
            // given
            val state = GameState(12354890L, players)
            agent1ActionChooser = { actions ->
                if (actions.find { action -> action is MulliganHandAction } != null) {
                    actions.first { action -> action is MulliganHandAction }
                } else {
                    actions.first { action -> action is KeepHandAction || action is FromHandToBottomLibrary }
                }
            }
            agent2ActionChooser = { actions -> actions.first { action -> action is KeepHandAction } }
            player1.mulligan = 0
            player2.mulligan = 0

            //  when
            OpeningStage().proceed(agents, state)

            // then
            assertEquals(1, state.players[0].hand.size)
            assertEquals(7, state.players[1].hand.size)
            assertEquals(59, state.players[0].library.size)
            assertEquals(53, state.players[1].library.size)
            assertEquals(20, state.players[0].health)
            assertEquals(20, state.players[1].health)
        }

    }
}