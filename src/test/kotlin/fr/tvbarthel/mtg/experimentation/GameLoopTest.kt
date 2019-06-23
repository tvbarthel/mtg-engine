package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

fun instantiateGameLoop(): GameLoop {
    return FirstNaiveGameLoop()
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
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurn(0, player1, player2)
        gameLoop.playTurn(1, player2, player1)
        gameLoop.playTurn(2, player1, player2)

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
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurn(0, player1, player2)
        gameLoop.playTurn(1, player2, player1)
        gameLoop.playTurn(2, player1, player2)
        gameLoop.playTurn(3, player2, player1)
        gameLoop.playTurn(4, player1, player2)

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
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurn(0, player1, player2)
        gameLoop.playTurn(1, player2, player1)
        gameLoop.playTurn(2, player1, player2)
        gameLoop.playTurn(3, player2, player1)
        gameLoop.playTurn(4, player1, player2)

        // Then
        player1.life shouldBe 20
        assert(player1.board[0] is Plains)
        assert(player1.board[1] is SanctuaryCat)

        player2.life shouldBe 20
        assert(player2.board[0] is Plains)
        assert(player2.board[1] is SanctuaryCat)
    }

    "Blocked creature dies" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val fakeCreatureP1 = FakeCreature("fake-creature-p1", 4, 4)
        val fakeCreatureP2 = FakeCreature("fake-creature-p2", 1, 1)

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
                    SpawnCreatureAction(fakeCreatureP1)
                )
            ),
            // Turn 3 - player 2 priority
            emptyMap(),
            // Turn 4 - player 1 priority
            mapOf(
                Step.CombatPhaseDeclareAttackersStep to mutableListOf<Action>(
                    DeclareAttackersAction(listOf(AttackAction(fakeCreatureP1, player2)))
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
                    SpawnCreatureAction(fakeCreatureP2)
                )
            ),
            // Turn 4 - player 1 priority
            mapOf(
                Step.CombatPhaseDeclareBlockersStep to mutableListOf<Action>(
                    DeclareBlockersAction(listOf(BlockAction(fakeCreatureP1, listOf(fakeCreatureP2))))
                )
            )
        )

        // When
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurn(0, player1, player2)
        gameLoop.playTurn(1, player2, player1)
        gameLoop.playTurn(2, player1, player2)
        gameLoop.playTurn(3, player2, player1)
        gameLoop.playTurn(4, player1, player2)

        // Then
        player1.life shouldBe 20
        player1.board.size shouldBe 2
        assert(player1.board[0] is Plains)
        assert(player1.board[1] is FakeCreature)

        player2.life shouldBe 20
        player2.board.size shouldBe 1
        assert(player2.board[0] is Plains)
    }
})