package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class AjanisWelcomeTest : StringSpec({
    "Cast Ajani's welcome" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val ajanisWelcome = AjanisWelcome("card-1")

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastEnchantmentAction(ajanisWelcome))
                // Play
                .playTurns(gameLoop)

            // Then
            player1.board.size shouldBe 1
            player1.board[0] shouldBe ajanisWelcome

            player2.board.size shouldBe 0
        }
    }

    "Trigger Ajani's welcome" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val ajanisWelcome = AjanisWelcome("card-1")
            val sanctuaryCat = SanctuaryCat("card-2")

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastEnchantmentAction(ajanisWelcome))
                // Turn 1 - player 2 active
                .addTurn()
                // Turn 2 - player 1 active
                .addTurn(Step.SecondMainPhaseStep, player1, CastCreatureAction(sanctuaryCat))
                // Play
                .playTurns(gameLoop)

            // Then
            player1.life shouldBe 21
            player1.board.size shouldBe 2
            player1.board[0] shouldBe ajanisWelcome
            player1.board[1] shouldBe sanctuaryCat
        }
    }
})