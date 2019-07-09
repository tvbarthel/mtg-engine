package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class HistoryOfBenaliaTest : StringSpec({
    "Increase history of benalia to 1st lore counter" {
        forAllGameLoops { gameLoop ->
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
                .playTurns(gameLoop)

            // Then
            player1.board.size shouldBe 2
            player1.board[0] shouldBe historyOfBenalia
            historyOfBenalia.loreCounter shouldBe 1
            assert(player1.board[1] is KnightToken)
        }
    }

    "Increase history of benalia to 2nd lore counter" {
        forAllGameLoops { gameLoop ->
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
                // Turn 1 - player 2 active
                .addTurn()
                // Turn 2 - player 1 active
                .addTurn()
                .playTurns(gameLoop)

            // Then
            player1.board.size shouldBe 3
            player1.board[0] shouldBe historyOfBenalia
            historyOfBenalia.loreCounter shouldBe 2
            assert(player1.board[1] is KnightToken)
            assert(player1.board[2] is KnightToken)
        }
    }

    "Increase history of benalia to 3rd lore counter" {
        forAllGameLoops { gameLoop ->
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
                // Turn 1 - player 2 active
                .addTurn()
                // Turn 2 - player 1 active
                .addTurn()
                // Turn 3 - player 2 active
                .addTurn()
                .playTurns(gameLoop)

            // Turn 4 - player 1 active
            val turnContext = TurnContext(4, player1, player2)
            gameLoop.playStep(turnContext, Step.BeginningPhaseUntapStep)
            gameLoop.playStep(turnContext, Step.BeginningPhaseUpKeepStep)
            gameLoop.playStep(turnContext, Step.BeginningPhaseDrawStep)

            // Then
            player1.board.size shouldBe 2
            historyOfBenalia.loreCounter shouldBe 3
            assert(player1.board[0] is KnightToken)
            (player1.board[0] as KnightToken).power.getCurrentValue() shouldBe 4
            (player1.board[0] as KnightToken).toughness.getCurrentValue() shouldBe 3
            assert(player1.board[1] is KnightToken)
            (player1.board[1] as KnightToken).power.getCurrentValue() shouldBe 4
            (player1.board[1] as KnightToken).toughness.getCurrentValue() shouldBe 3
        }
    }

    "History of benalia 3rd lore counter effects are remove at the end of the turn" {
        forAllGameLoops { gameLoop ->
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
                // Turn 1 - player 2 active
                .addTurn()
                // Turn 2 - player 1 active
                .addTurn()
                // Turn 3 - player 2 active
                .addTurn()
                // Turn 4 - player 1 active
                .addTurn()
                .playTurns(gameLoop)


            // Then
            player1.board.size shouldBe 2
            historyOfBenalia.loreCounter shouldBe 3
            assert(player1.board[0] is KnightToken)
            (player1.board[0] as KnightToken).power.getCurrentValue() shouldBe 2
            (player1.board[0] as KnightToken).toughness.getCurrentValue() shouldBe 2
            assert(player1.board[1] is KnightToken)
            (player1.board[1] as KnightToken).power.getCurrentValue() shouldBe 2
            (player1.board[1] as KnightToken).toughness.getCurrentValue() shouldBe 2
        }
    }

    "History of benalia 3rd counter effects do not apply to other knight creature" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val historyOfBenalia = HistoryOfBenalia("p1")
            val fakeCreature = FakeCreature("cat-p1", 1, 1)
            fakeCreature.creatureTypes.add(CreatureType.KNIGHT)

            historyOfBenalia.loreCounter = 2
            player1.board.add(historyOfBenalia)
            player1.board.add(fakeCreature)
            player1.scriptedActions = emptyList()
            player2.scriptedActions = emptyList()

            // Turn 0 - player 1 active
            val turnContext = TurnContext(4, player1, player2)
            gameLoop.playStep(turnContext, Step.BeginningPhaseUntapStep)
            gameLoop.playStep(turnContext, Step.BeginningPhaseUpKeepStep)
            gameLoop.playStep(turnContext, Step.BeginningPhaseDrawStep)

            // Then
            player1.board.size shouldBe 1
            fakeCreature.power.getCurrentValue() shouldBe 3
            fakeCreature.toughness.getCurrentValue() shouldBe 2
        }
    }

    "History of benalia 3rd counter effects do not apply to non-knight creature" {
        forAllGameLoops { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val historyOfBenalia = HistoryOfBenalia("p1")
            val sanctuaryCat = SanctuaryCat("cat-p1")
            historyOfBenalia.loreCounter = 2
            player1.board.add(historyOfBenalia)
            player1.board.add(sanctuaryCat)
            player1.scriptedActions = emptyList()
            player2.scriptedActions = emptyList()

            // Turn 0 - player 1 active
            val turnContext = TurnContext(4, player1, player2)
            gameLoop.playStep(turnContext, Step.BeginningPhaseUntapStep)
            gameLoop.playStep(turnContext, Step.BeginningPhaseUpKeepStep)
            gameLoop.playStep(turnContext, Step.BeginningPhaseDrawStep)

            // Then
            player1.board.size shouldBe 1
            sanctuaryCat.power.getCurrentValue() shouldBe 1
            sanctuaryCat.toughness.getCurrentValue() shouldBe 2
        }
    }
})