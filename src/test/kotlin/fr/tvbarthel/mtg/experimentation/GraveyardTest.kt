package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class GraveyardTest : StringSpec({
    "Instant casted are added to graveyard" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val fakeInstant = FakeInstant("p1")

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastInstantAction(fakeInstant))
            .playTurns(instantiateGameLoop())

        // Then
        player1.graveyard.size shouldBe 1
        player1.graveyard[0] shouldBe fakeInstant
    }
})