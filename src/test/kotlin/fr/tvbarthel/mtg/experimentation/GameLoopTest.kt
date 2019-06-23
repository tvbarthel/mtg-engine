package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

fun instantiateGameLoop(player1: Player, player2: Player): GameLoop {
    return FirstNaiveGameLoop(player1, player2)
}

class GameLoopTest : StringSpec({
    "Play Land Cards" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")

        player1.scriptedActions = listOf(
            // Turn 0 - player 1 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    PlayLandAction(Plains("plains-card-p1-a"))
                )
            ),
            // Turn 1 - player 2 priority
            emptyMap(),
            // Turn 2 - player 1 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    PlayLandAction(Plains("plains-card-p1-b"))
                )
            )
        )

        player2.scriptedActions = emptyList()

        // When
        val gameLoop = instantiateGameLoop(player1, player2)
        gameLoop.playTurn(0)
        gameLoop.playTurn(1)
        gameLoop.playTurn(2)

        // Then
        player1.board.size shouldBe 2
        player2.board.size shouldBe 0

        assert(player1.board[0] is Plains)
        assert(player1.board[1] is Plains)
    }


    "Attack opponent" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val sanctuaryCat = SanctuaryCat("sanctuary-cat-1")

        player1.scriptedActions = listOf(
            // Turn 0 - player 1 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    PlayLandAction(Plains("plains-card-p1-a"))
                )
            ),
            // Turn 1 - player 2 priority
            emptyMap(),
            // Turn 2 - player 1 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    SpawnCreatureAction(sanctuaryCat)
                )
            ),
            // Turn 3 - player 2 priority
            emptyMap(),
            // Turn 4 - player 1 priority
            mapOf(
                Step.CombatPhaseDeclareAttackersStep to mutableListOf<Action>(
                    DeclareAttackersAction(listOf(AttackAction(sanctuaryCat, player2)))
                )
            )
        )

        player2.scriptedActions = emptyList()

        // When
        val gameLoop = instantiateGameLoop(player1, player2)
        gameLoop.playTurn(0)
        gameLoop.playTurn(1)
        gameLoop.playTurn(2)
        gameLoop.playTurn(3)
        gameLoop.playTurn(4)

        // Then
        player1.life shouldBe 20
        assert(player1.board[1] is SanctuaryCat)

        player2.life shouldBe 19
        player2.board.size shouldBe 0
    }

    "Block attacking creatures" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val sanctuaryCatP1 = SanctuaryCat("sanctuary-cat-p1")
        val sanctuaryCatP2 = SanctuaryCat("sanctuary-cat-p2")

        player1.scriptedActions = listOf(
            // Turn 0 - player 1 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    PlayLandAction(Plains("plains-card-p1-a"))
                )
            ),
            // Turn 1 - player 2 priority
            emptyMap(),
            // Turn 2 - player 1 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    SpawnCreatureAction(sanctuaryCatP1)
                )
            ),
            // Turn 3 - player 2 priority
            emptyMap(),
            // Turn 4 - player 1 priority
            mapOf(
                Step.CombatPhaseDeclareAttackersStep to mutableListOf<Action>(
                    DeclareAttackersAction(listOf(AttackAction(sanctuaryCatP1, player2)))
                )
            )
        )

        player2.scriptedActions = listOf(
            // Turn 0 - player 1 priority
            emptyMap(),
            // Turn 1 - player 2 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    PlayLandAction(Plains("plains-card-p2-a"))
                )
            ),
            // Turn 2 - player 1 priority
            emptyMap(),
            // Turn 3 - player 2 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    SpawnCreatureAction(sanctuaryCatP2)
                )
            ),
            // Turn 4 - player 1 priority
            mapOf(
                Step.CombatPhaseDeclareBlockersStep to mutableListOf<Action>(
                    DeclareBlockersAction(listOf(BlockAction(sanctuaryCatP1, listOf(sanctuaryCatP2))))
                )
            )
        )

        // When
        val gameLoop = instantiateGameLoop(player1, player2)
        gameLoop.playTurn(0)
        gameLoop.playTurn(1)
        gameLoop.playTurn(2)
        gameLoop.playTurn(3)
        gameLoop.playTurn(4)

        // Then
        player1.life shouldBe 20
        assert(player1.board[0] is Plains)
        assert(player1.board[1] is SanctuaryCat)

        player2.life shouldBe 20
        assert(player2.board[0] is Plains)
        assert(player2.board[1] is SanctuaryCat)
    }
})