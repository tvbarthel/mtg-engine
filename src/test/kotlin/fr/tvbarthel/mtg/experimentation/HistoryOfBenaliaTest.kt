package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class HistoryOfBenaliaTest : StringSpec({
    "Casting history of benalia spawns a knight token" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val historyOfBenalia = HistoryOfBenalia("p1")

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(
                mapOf(
                    Step.FirstMainPhaseStep to listOf(
                        Pair(player1, CastEnchantmentAction(historyOfBenalia))
                    )
                )
            )
            .playTurns(instantiateGameLoop())

        // Then
        player1.board.size shouldBe 2
        player1.board[0] shouldBe historyOfBenalia
        assert(player1.board[1] is KnightToken)
    }
})