package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ShockTest : StringSpec({
    "Cast shock on player" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val shock = Shock("p1", player2)

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastInstantAction(shock))
            .playTurns(instantiateGameLoop(), player1, player2)

        // Then
        player1.life shouldBe 20
        player2.life shouldBe 18
    }

    "Kill opponent creature with shock" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val creatureP2 = FakeCreature("fake-creature-p2", 2, 2)
        val shock = Shock("p1", creatureP2)

        player2.board.add(creatureP2)

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastInstantAction(shock))
            .playTurns(instantiateGameLoop(), player1, player2)

        // Then
        player2.board.size shouldBe 0
    }
})