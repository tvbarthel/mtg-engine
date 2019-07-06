package fr.tvbarthel.mtg.experimentation

import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row

fun instantiateGameLoop(): GameLoop {
    return FirstNaiveGameLoop()
}

class GameLoopTest : StringSpec({
    "Play Land Cards" {
        forall(
            row(FirstNaiveGameLoop()),
            row(ActorGameLoop())
        ) { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, PlayLandAction(Plains("plains-card-p1-a")))
                // Turn 1 - player 2 active
                .addTurn()
                // Turn 2 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, PlayLandAction(Plains("plains-card-p1-b")))
                // Play
                .playTurns(gameLoop)

            // Then
            player1.board.size shouldBe 2
            player2.board.size shouldBe 0

            assert(player1.board[0] is Plains)
            assert(player1.board[1] is Plains)
        }
    }

    "Attack opponent" {
        forall(
            row(FirstNaiveGameLoop()),
            row(ActorGameLoop())
        ) { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val sanctuaryCat = SanctuaryCat("sanctuary-cat-1")

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, PlayLandAction(Plains("plains-card-p1-a")))
                // Turn 1 - player 2 active
                .addTurn()
                // Turn 2 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(sanctuaryCat))
                // Turn 3 - player 2 active
                .addTurn()
                // Turn 4 - player 1 active
                .addTurn(
                    Step.CombatPhaseDeclareAttackersStep,
                    player1,
                    DeclareAttackersAction(sanctuaryCat, player2)
                )
                // Play
                .playTurns(gameLoop)

            // Then
            player1.life shouldBe 20
            assert(player1.board[1] is SanctuaryCat)

            player2.life shouldBe 19
            player2.board.size shouldBe 0
        }
    }

    "Block attacking creatures" {
        forall(
            row(FirstNaiveGameLoop()),
            row(ActorGameLoop())
        ) { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val sanctuaryCatP1 = SanctuaryCat("sanctuary-cat-p1")
            val sanctuaryCatP2 = SanctuaryCat("sanctuary-cat-p2")

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, PlayLandAction(Plains("plains-card-p1-a")))
                // Turn 1 - player 2 active
                .addTurn(Step.FirstMainPhaseStep, player2, PlayLandAction(Plains("plains-card-p2-a")))
                // Turn 2 - player 1 active
                .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(sanctuaryCatP1))
                // Turn 3 - player 2 active
                .addTurn(Step.FirstMainPhaseStep, player2, CastCreatureAction(sanctuaryCatP2))
                // Turn 4 - player 1 active
                .addTurn(
                    mapOf(
                        Step.CombatPhaseDeclareAttackersStep to listOf(
                            Pair(player1, DeclareAttackersAction(sanctuaryCatP1, player2))
                        ),
                        Step.CombatPhaseDeclareBlockersStep to listOf(
                            Pair(player2, DeclareBlockersAction(sanctuaryCatP1, sanctuaryCatP2))
                        )
                    )
                )
                // Play
                .playTurns(gameLoop)

            // Then
            player1.life shouldBe 20
            assert(player1.board[0] is Plains)
            assert(player1.board[1] is SanctuaryCat)

            player2.life shouldBe 20
            assert(player2.board[0] is Plains)
            assert(player2.board[1] is SanctuaryCat)
        }
    }

    "Blocking creature dies" {
        forall(
            row(FirstNaiveGameLoop()),
            row(ActorGameLoop())
        ) { gameLoop ->
            // Given
            val player1 = ScriptedPlayer("Ava")
            val player2 = ScriptedPlayer("Williams")
            val fakeCreatureP1 = FakeCreature("fake-creature-p1", 4, 4)
            val fakeCreatureP2 = FakeCreature("fake-creature-p2", 1, 1)

            player1.board.add(fakeCreatureP1)
            player2.board.add(fakeCreatureP2)

            // When
            ScriptedActionBuilder(player1, player2)
                // Turn 0 - player 1 active
                .addTurn(
                    mapOf(
                        Step.CombatPhaseDeclareAttackersStep to listOf(
                            Pair(player1, DeclareAttackersAction(fakeCreatureP1, player2))
                        ),
                        Step.CombatPhaseDeclareBlockersStep to listOf(
                            Pair(player2, DeclareBlockersAction(fakeCreatureP1, fakeCreatureP2))
                        )
                    )
                )
                // Play
                .playTurns(gameLoop)

            // Then
            player1.life shouldBe 20
            player1.board.size shouldBe 1
            player1.board[0] shouldBe fakeCreatureP1

            player2.life shouldBe 20
            player2.board.size shouldBe 0
        }
    }

    "Blocked creatures dies" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val fakeCreatureP1 = FakeCreature("fake-creature-p1", 1, 1)
        val fakeCreatureP2 = FakeCreature("fake-creature-p2", 4, 4)

        player1.board.add(fakeCreatureP1)
        player2.board.add(fakeCreatureP2)

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(
                mapOf(
                    Step.CombatPhaseDeclareAttackersStep to listOf(
                        Pair(player1, DeclareAttackersAction(fakeCreatureP1, player2))
                    ),
                    Step.CombatPhaseDeclareBlockersStep to listOf(
                        Pair(player2, DeclareBlockersAction(fakeCreatureP1, fakeCreatureP2))
                    )
                )
            )
            // Play
            .playTurns(instantiateGameLoop())

        // Then
        player1.life shouldBe 20
        player1.board.size shouldBe 0

        player2.life shouldBe 20
        player2.board.size shouldBe 1
        player2.board[0] shouldBe fakeCreatureP2
    }

    "Cast Enchantment Card" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val ajanisWelcome = AjanisWelcome("card-1")

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastEnchantmentAction(ajanisWelcome))
            // Play
            .playTurns(instantiateGameLoop())

        // Then
        player1.board.size shouldBe 1
        player1.board[0] shouldBe ajanisWelcome

        player2.board.size shouldBe 0
    }

})