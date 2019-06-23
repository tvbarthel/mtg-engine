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
        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)

        scriptedActionBuilder
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, PlayLandAction(Plains("plains-card-p1-a")))
            // Turn 1 - player 2 active
            .addTurn()
            // Turn 2 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, PlayLandAction(Plains("plains-card-p1-b")))


        player1.scriptedActions = scriptedActionBuilder.getActions(player1)
        player2.scriptedActions = scriptedActionBuilder.getActions(player2)

        // When
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurns(player1, player2, 3)

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
        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)

        scriptedActionBuilder
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, PlayLandAction(Plains("plains-card-p1-a")))
            // Turn 1 - player 2 active
            .addTurn()
            // Turn 2 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, SpawnCreatureAction(sanctuaryCat))
            // Turn 3 - player 2 active
            .addTurn()
            // Turn 4 - player 1 active
            .addTurn(
                Step.CombatPhaseDeclareAttackersStep,
                player1,
                DeclareAttackersAction(listOf(AttackAction(sanctuaryCat, player2)))
            )

        player1.scriptedActions = scriptedActionBuilder.getActions(player1)
        player2.scriptedActions = scriptedActionBuilder.getActions(player2)

        // When
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurns(player1, player2, 5)

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
        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)

        scriptedActionBuilder
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, PlayLandAction(Plains("plains-card-p1-a")))
            // Turn 1 - player 2 active
            .addTurn(Step.FirstMainPhaseStep, player2, PlayLandAction(Plains("plains-card-p2-a")))
            // Turn 2 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, SpawnCreatureAction(sanctuaryCatP1))
            // Turn 3 - player 2 active
            .addTurn(Step.FirstMainPhaseStep, player2, SpawnCreatureAction(sanctuaryCatP2))
            // Turn 4 - player 1 active
            .addTurn(
                mapOf(
                    Step.CombatPhaseDeclareAttackersStep to listOf(
                        Pair(
                            player1,
                            DeclareAttackersAction(listOf(AttackAction(sanctuaryCatP1, player2)))
                        )
                    ),
                    Step.CombatPhaseDeclareBlockersStep to listOf(
                        Pair(
                            player2,
                            DeclareBlockersAction(listOf(BlockAction(sanctuaryCatP1, listOf(sanctuaryCatP2))))
                        )
                    )
                )
            )

        player1.scriptedActions = scriptedActionBuilder.getActions(player1)
        player2.scriptedActions = scriptedActionBuilder.getActions(player2)

        // When
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurns(player1, player2, 5)

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
        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)

        scriptedActionBuilder
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, PlayLandAction(Plains("plains-card-p1-a")))
            // Turn 1 - player 2 active
            .addTurn(Step.FirstMainPhaseStep, player2, PlayLandAction(Plains("plains-card-p2-a")))
            // Turn 2 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, SpawnCreatureAction(fakeCreatureP1))
            // Turn 3 - player 2 active
            .addTurn(Step.FirstMainPhaseStep, player2, SpawnCreatureAction(fakeCreatureP2))
            // Turn 4 - player 1 active
            .addTurn(
                mapOf(
                    Step.CombatPhaseDeclareAttackersStep to listOf(
                        Pair(
                            player1,
                            DeclareAttackersAction(listOf(AttackAction(fakeCreatureP1, player2)))
                        )
                    ),
                    Step.CombatPhaseDeclareBlockersStep to listOf(
                        Pair(
                            player2,
                            DeclareBlockersAction(listOf(BlockAction(fakeCreatureP1, listOf(fakeCreatureP2))))
                        )
                    )
                )
            )

        player1.scriptedActions = scriptedActionBuilder.getActions(player1)
        player2.scriptedActions = scriptedActionBuilder.getActions(player2)

        // When
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurns(player1, player2, 5)

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