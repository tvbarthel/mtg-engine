package fr.tvbarthel.mtg.experimentation

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class BenalishMarshalTest : StringSpec({
    "Benalish Marshal boost existing creatures" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val sanctuaryCat = SanctuaryCat("card-1")
        val benalishMarshal = BenalishMarshal("card-2")

        player1.board.add(sanctuaryCat)

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(benalishMarshal))
            // Play
            .playTurns(instantiateGameLoop())

        // Then
        player1.board.size shouldBe 2
        player1.board[0] shouldBe sanctuaryCat
        assert((player1.board[0] as CreatureCard).power.getCurrentValue() == 2)
        assert((player1.board[0] as CreatureCard).toughness.getCurrentValue() == 3)
        player1.board[1] shouldBe benalishMarshal
    }

    "Benalish Marshal stop boosting creatures if it dies" {
        // Given
        val player1 = ScriptedPlayer("Ava")
        val player2 = ScriptedPlayer("Williams")
        val sanctuaryCat = SanctuaryCat("card-1")
        val benalishMarshal = BenalishMarshal("card-2")
        val fakeCreature = FakeCreature("card-3", 99, 99)

        player1.board.add(sanctuaryCat)
        player2.board.add(fakeCreature)

        // When
        ScriptedActionBuilder(player1, player2)
            // Turn 0 - player 1 active
            .addTurn(Step.FirstMainPhaseStep, player1, CastCreatureAction(benalishMarshal))
            // Turn 1 - player 2 active
            .addTurn(
                mapOf(
                    Step.CombatPhaseDeclareAttackersStep to listOf(
                        Pair(player2, DeclareAttackersAction(fakeCreature, player1))
                    ),
                    Step.CombatPhaseDeclareBlockersStep to listOf(
                        Pair(player1, DeclareBlockersAction(fakeCreature, benalishMarshal))
                    )
                )
            )
            // Play
            .playTurns(instantiateGameLoop())

        // Then
        player1.board.size shouldBe 1
        player1.board[0] shouldBe sanctuaryCat
        assert((player1.board[0] as CreatureCard).power.getCurrentValue() == 1)
        assert((player1.board[0] as CreatureCard).toughness.getCurrentValue() == 2)

        player2.board.size shouldBe 1
        player2.board[0] shouldBe fakeCreature
    }
})