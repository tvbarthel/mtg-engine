package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class GraveyardTest : StringSpec({
    "Instant casted are added to graveyard" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val fakeInstant = FakeInstant("p1")

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastInstantAction(fakeInstant))
                .playTurns(gameLoop)

            // Then
            player1.graveyard.size shouldBe 1
            player1.graveyard[0] shouldBe fakeInstant
        }
    }

    "Creature killed are added to graveyard" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val sanctuaryCat = SanctuaryCat("p2")
            val shock = Shock("p1", sanctuaryCat)

            player2.board.add(sanctuaryCat)

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastInstantAction(shock))
                .playTurns(gameLoop)

            // Then
            player2.graveyard.size shouldBe 1
            player2.graveyard[0] shouldBe sanctuaryCat
        }
    }
})