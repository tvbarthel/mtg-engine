package fr.tvbarthel.mtg.engine.playing

import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Player
import fr.tvbarthel.mtg.engine.playing.phase.BeginningPhase
import fr.tvbarthel.mtg.engine.playing.phase.CombatPhase
import fr.tvbarthel.mtg.engine.playing.phase.EndingPhase
import fr.tvbarthel.mtg.engine.playing.phase.MainPhase
import io.kotlintest.TestCase
import io.kotlintest.specs.StringSpec
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlin.test.assertEquals

class PlayingStageTest : StringSpec() {

    @RelaxedMockK
    lateinit var beginningPhase: BeginningPhase
    @RelaxedMockK
    lateinit var mainPhase: MainPhase
    @RelaxedMockK
    lateinit var combatPhase: CombatPhase

    @RelaxedMockK
    lateinit var endingPhase: EndingPhase

    @RelaxedMockK
    lateinit var agents: Map<Int, Agent>

    private lateinit var state: GameState
    private lateinit var player1: Player
    private lateinit var player2: Player

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

        player1 = Player(1).apply { health = 20 }
        player2 = Player(2).apply { health = 20 }
        state = GameState(12354890L, players = listOf(player1, player2))

        every { beginningPhase.proceed(any(), any()) } returns state
        every { mainPhase.proceed(any(), any()) } returns state
        every { endingPhase.proceed(any(), any()) } returns state
    }

    init {
        MockKAnnotations.init(this)

        "given empty state when proceed one turn then active player became second player" {
            // given
            val gameOverTurn = 0
            every { combatPhase.proceed(any(), any()) } answers {
                if (state.turn == gameOverTurn) player1.health = 0
                state
            }

            // when
            PlayingStage(beginningPhase, mainPhase, combatPhase, endingPhase).proceed(agents, state)

            // then
            assertEquals(1, state.activePlayer)
        }

        "given empty state when proceed 2 turn then active player became first player" {
            // given
            val gameOverTurn = 1
            every { combatPhase.proceed(any(), any()) } answers {
                if (state.turn == gameOverTurn) player1.health = 0
                state
            }

            // when
            PlayingStage(beginningPhase, mainPhase, combatPhase, endingPhase).proceed(agents, state)

            // then
            assertEquals(0, state.activePlayer)
        }
    }
}