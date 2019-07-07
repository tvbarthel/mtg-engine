package fr.tvbarthel.mtg.experimentation

import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row

class GhituLavarunnerTest : StringSpec({
    "Cast Ghitu Lavarunner with no instant in graveyard" {
        forall(
            row(FirstNaiveGameLoop()),
            row(ActorGameLoop())
        ) { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val ghituLavarunner = GhituLavarunner("p1")

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(ghituLavarunner))
                .playTurns(gameLoop)

            // Then
            player1.board.size shouldBe 1
            player1.board[0] shouldBe ghituLavarunner
            ghituLavarunner.power.getCurrentValue() shouldBe 1
            ghituLavarunner.toughness.getCurrentValue() shouldBe 2
            ghituLavarunner.hasHaste() shouldBe false
        }
    }

    "Cast Ghitu Lavarunner with two instants in graveyard" {
        forall(
            row(FirstNaiveGameLoop()),
            row(ActorGameLoop())
        ) { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val ghituLavarunner = GhituLavarunner("p1")
            player1.graveyard.add(Shock("1", player2))
            player1.graveyard.add(Shock("2", player2))

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(ghituLavarunner))
                .playTurns(gameLoop)

            // Then
            player1.board.size shouldBe 1
            player1.board[0] shouldBe ghituLavarunner
            ghituLavarunner.power.getCurrentValue() shouldBe 2
            ghituLavarunner.toughness.getCurrentValue() shouldBe 2
            ghituLavarunner.hasHaste() shouldBe true
        }
    }

    "Cast an instant activate Ghitu Lavarunner bonuses" {
        forall(
            row(FirstNaiveGameLoop()),
            row(ActorGameLoop())
        ) { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val ghituLavarunner = GhituLavarunner("p1")
            val shock1 = Shock("1", player2)
            val shock2 = Shock("2", player2)
            player1.graveyard.add(shock1)


            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(ghituLavarunner))
                // Turn 1 - player 2 active
                .addTurn()
                // Turn 2 - player 1 active
                .addTurn(Step.SecondMainPhaseStep, player1, CastInstantAction(shock2))
                .playTurns(gameLoop)

            // Then
            player1.board.size shouldBe 1
            player1.board[0] shouldBe ghituLavarunner
            ghituLavarunner.power.getCurrentValue() shouldBe 2
            ghituLavarunner.toughness.getCurrentValue() shouldBe 2
            ghituLavarunner.hasHaste() shouldBe true
        }
    }

    "Cast extra instant do not re-activate Ghitu Lavarunner bonuses" {
        forall(
            row(FirstNaiveGameLoop()),
            row(ActorGameLoop())
        ) { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val ghituLavarunner = GhituLavarunner("p1")
            val shock1 = Shock("1", player2)
            val shock2 = Shock("2", player2)
            val shock3 = Shock("3", player2)
            player1.graveyard.add(shock1)


            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(ghituLavarunner))
                // Turn 1 - player 2 active
                .addTurn()
                // Turn 2 - player 1 active
                .addTurn(Step.SecondMainPhaseStep, player1, CastInstantAction(shock2))
                // Turn 3 - player 2 active
                .addTurn()
                // Turn 4 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastInstantAction(shock3))
                .playTurns(gameLoop)

            // Then
            player1.board.size shouldBe 1
            player1.board[0] shouldBe ghituLavarunner
            ghituLavarunner.power.getCurrentValue() shouldBe 2
            ghituLavarunner.toughness.getCurrentValue() shouldBe 2
            ghituLavarunner.hasHaste() shouldBe true
        }
    }
})