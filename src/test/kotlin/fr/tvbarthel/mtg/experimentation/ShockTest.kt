package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ShockTest : StringSpec({
    "Cast shock on player" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val shock = Shock("p1", player2)

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastInstantAction(shock))
                .playTurns(gameLoop)

            // Then
            player1.life shouldBe 20
            player2.life shouldBe 18
        }
    }

    "Kill opponent creature with shock" {
        forAllGameLoops { gameLoop ->
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
                .playTurns(gameLoop)

            // Then
            player2.board.size shouldBe 0
        }
    }

    "Do not kill opponent creature with shock if toughness is greater than damages" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val creatureP2 = FakeCreature("fake-creature-p2", 2, 3)
            val shock = Shock("p1", creatureP2)

            player2.board.add(creatureP2)

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastInstantAction(shock))
                .playTurns(gameLoop)

            // Then
            player2.board.size shouldBe 1
            player2.board[0] shouldBe creatureP2
            creatureP2.toughness.getCurrentValue() shouldBe 3
        }
    }

    "Kill my creature with shock" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val creatureP1 = FakeCreature("fake-creature-p1", 2, 2)
            val shock = Shock("p1", creatureP1)

            player1.board.add(creatureP1)

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastInstantAction(shock))
                .playTurns(gameLoop)

            // Then
            player1.board.size shouldBe 0
        }
    }

    "Kill blocking creature with shock before combat damage" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val creatureP2 = FakeCreature("fake-creature-p2", 1, 3)
            val creatureP1 = FakeCreature("fake-creature-p1", 1, 2)
            val shock = Shock("p1", creatureP2)

            player1.board.add(creatureP1)
            player2.board.add(creatureP2)

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(
                    mapOf(
                        Step.CombatPhaseDeclareAttackersStep to listOf(
                            Pair(player1, DeclareAttackersAction(creatureP1, player2))
                        ),
                        Step.CombatPhaseDeclareBlockersStep to listOf(
                            Pair(player2, DeclareBlockersAction(creatureP1, creatureP2)),
                            Pair(player1, CastInstantAction(shock))
                        )
                    )
                )
                .playTurns(gameLoop)

            // Then
            player2.board.size shouldBe 0
        }
    }
})