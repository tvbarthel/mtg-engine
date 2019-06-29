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
})