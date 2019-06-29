package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


class DauntlessBodyguardTest : StringSpec({
    "Spawn dauntless bodyguard with no target" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val dauntlessBodyguard = DauntlessBodyguard("dauntless-bodyguard-1")

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(
                mapOf(
                    Step.FirstMainPhaseStep to listOf(
                        Pair(player1, CastCreatureAction(dauntlessBodyguard)),
                        Pair(player1, PassAction())
                    )
                )
            )
            .playTurns(instantiateGameLoop(), player1, player2)

        // Then
        player1.board.size shouldBe 1
        player1.board[0] shouldBe dauntlessBodyguard
        player1.board[0].abilities.size shouldBe 0
    }

    "Spawn dauntless bodyguard with target" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val sanctuaryCat = SanctuaryCat("sanctuary-cat-1")
        val dauntlessBodyguard = DauntlessBodyguard("dauntless-bodyguard-1")

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(
                mapOf(
                    Step.FirstMainPhaseStep to listOf(
                        Pair(player1, CastCreatureAction(sanctuaryCat)),
                        Pair(player1, CastCreatureAction(dauntlessBodyguard)),
                        Pair(player1, SelectCreatureAction(sanctuaryCat))
                    )
                )
            )
            .playTurns(instantiateGameLoop(), player1, player2)

        // Then
        player1.board.size shouldBe 2
        player1.board[0] shouldBe sanctuaryCat
        player1.board[1] shouldBe dauntlessBodyguard
        player1.board[1].abilities.size shouldBe 1
        assert(player1.board[1].abilities[0] is DauntlessBodyguard.SacrificeToGiveIndestructibleAbility)

        val ability = player1.board[1].abilities[0] as DauntlessBodyguard.SacrificeToGiveIndestructibleAbility
        ability.owner shouldBe dauntlessBodyguard
        ability.target shouldBe sanctuaryCat
    }

    "Activate dauntless bodyguard ability" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val creatureP1 = FakeCreature("fake-creature-p1", 4, 4)
        player1.board.add(creatureP1)

        val player2 = ScriptedPlayer("Williams")
        val creatureP2 = FakeCreature("sanctuary-cat-p2", 4, 4)
        val dauntlessBodyguardP2 = DauntlessBodyguard("dauntless-bodyguard-p2")
        val dauntlessBodyguardP2Ability =
            DauntlessBodyguard.SacrificeToGiveIndestructibleAbility(dauntlessBodyguardP2, creatureP2)
        dauntlessBodyguardP2.abilities.add(dauntlessBodyguardP2Ability)
        player2.board.add(dauntlessBodyguardP2)
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
                        Pair(player2, ActivateAbilityAction(dauntlessBodyguardP2Ability))
                    )
                )
            )
            .playTurns(instantiateGameLoop(), player1, player2)

        // Then
        player1.board.size shouldBe 0
        player2.board.size shouldBe 1
        player2.board[0] shouldBe creatureP2
        creatureP2.isIndestructible() shouldBe false
    }
})