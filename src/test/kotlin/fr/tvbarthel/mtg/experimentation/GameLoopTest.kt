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
            .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(sanctuaryCat))
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
            .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(sanctuaryCatP1))
            // Turn 3 - player 2 active
            .addTurn(Step.FirstMainPhaseStep, player2, CastCreatureAction(sanctuaryCatP2))
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

    "Blocking creature dies" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val fakeCreatureP1 = FakeCreature("fake-creature-p1", 4, 4)
        val fakeCreatureP2 = FakeCreature("fake-creature-p2", 1, 1)

        player1.board.add(fakeCreatureP1)
        player2.board.add(fakeCreatureP2)

        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)
        scriptedActionBuilder
            // Turn 0 - player 1 active
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
        gameLoop.playTurns(player1, player2, 1)

        // Then
        player1.life shouldBe 20
        player1.board.size shouldBe 1
        player1.board[0] shouldBe fakeCreatureP1

        player2.life shouldBe 20
        player2.board.size shouldBe 0
    }

    "Blocked creatures dies" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val fakeCreatureP1 = FakeCreature("fake-creature-p1", 1, 1)
        val fakeCreatureP2 = FakeCreature("fake-creature-p2", 4, 4)

        player1.board.add(fakeCreatureP1)
        player2.board.add(fakeCreatureP2)

        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)
        scriptedActionBuilder
            // Turn 0 - player 1 active
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
        gameLoop.playTurns(player1, player2, 1)

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
        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)

        scriptedActionBuilder
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastEnchantmentAction(ajanisWelcome))


        player1.scriptedActions = scriptedActionBuilder.getActions(player1)
        player2.scriptedActions = scriptedActionBuilder.getActions(player2)

        // When
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurns(player1, player2, 1)

        // Then
        player1.board.size shouldBe 1
        player1.board[0] shouldBe ajanisWelcome

        player2.board.size shouldBe 0
    }

    "Trigger Ajani's welcome" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val ajanisWelcome = AjanisWelcome("card-1")
        val sanctuaryCat = SanctuaryCat("card-2")
        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)

        scriptedActionBuilder
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastEnchantmentAction(ajanisWelcome))
            // Turn 1 - player 2 active
            .addTurn()
            // Turn 2 - player 1 active
            .addTurn(Step.SecondMainPhaseStep, player1, CastCreatureAction(sanctuaryCat))


        player1.scriptedActions = scriptedActionBuilder.getActions(player1)
        player2.scriptedActions = scriptedActionBuilder.getActions(player2)

        // When
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurns(player1, player2, 3)

        // Then
        player1.life shouldBe 21
        player1.board.size shouldBe 2
        player1.board[0] shouldBe ajanisWelcome
        player1.board[1] shouldBe sanctuaryCat
    }

    "Benalish Marshal boost existing creatures" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val sanctuaryCat = SanctuaryCat("card-1")
        val benalishMarshal = BenalishMarshal("card-2")
        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)

        player1.board.add(sanctuaryCat)

        scriptedActionBuilder
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(benalishMarshal))

        player1.scriptedActions = scriptedActionBuilder.getActions(player1)
        player2.scriptedActions = scriptedActionBuilder.getActions(player2)

        // When
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurns(player1, player2, 1)

        // Then
        player1.board.size shouldBe 2
        player1.board[0] shouldBe sanctuaryCat
        assert((player1.board[0] as CreatureCard).getCurrentPower() == 2)
        assert((player1.board[0] as CreatureCard).getCurrentToughness() == 3)
        player1.board[1] shouldBe benalishMarshal
    }

    "Benalish Marshal stop boosting creatures if it dies" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val sanctuaryCat = SanctuaryCat("card-1")
        val benalishMarshal = BenalishMarshal("card-2")
        val fakeCreature = FakeCreature("card-3", 99, 99)
        val scriptedActionBuilder = ScriptedActionBuilder(player1, player2)

        player1.board.add(sanctuaryCat)
        player2.board.add(fakeCreature)

        scriptedActionBuilder
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(benalishMarshal))
            // Turn 1 - player 2 active
            .addTurn(
                mapOf(
                    Step.CombatPhaseDeclareAttackersStep to listOf(
                        Pair(player2, DeclareAttackersAction(listOf(AttackAction(fakeCreature, player1))))
                    ),
                    Step.CombatPhaseDeclareBlockersStep to listOf(
                        Pair(player1, DeclareBlockersAction(listOf(BlockAction(fakeCreature, listOf(benalishMarshal)))))
                    )
                )
            )

        player1.scriptedActions = scriptedActionBuilder.getActions(player1)
        player2.scriptedActions = scriptedActionBuilder.getActions(player2)

        // When
        val gameLoop = instantiateGameLoop()
        gameLoop.playTurns(player1, player2, 2)

        // Then
        player1.board.size shouldBe 1
        player1.board[0] shouldBe sanctuaryCat
        assert((player1.board[0] as CreatureCard).getCurrentPower() == 1)
        assert((player1.board[0] as CreatureCard).getCurrentToughness() == 2)

        player2.board.size shouldBe 1
        player2.board[0] shouldBe fakeCreature
    }
})