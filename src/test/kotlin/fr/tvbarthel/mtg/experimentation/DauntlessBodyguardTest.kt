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
})