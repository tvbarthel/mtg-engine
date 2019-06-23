package fr.tvbarthel.mtg.experimentation

fun main() {
    println("First naive game loop")

    val player1 = ScriptedPlayer("Ava")
    val player2 = ScriptedPlayer("Williams")
    val sanctuaryCatP1 = SanctuaryCat("sanctuary-card-p1-a")
    val sanctuaryCatP2 = SanctuaryCat("sanctuary-card-p2-a")

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
        ),
        // Turn 3 - player 2 priority
        emptyMap(),
        // Turn 4 - player 1 priority
        mapOf(
            Step.FirstMainPhaseStep to mutableListOf<Action>(
                CastCreatureAction(sanctuaryCatP1)
            )
        ),
        // Turn 5 - player 2 priority
        emptyMap(),
        // Turn 6 - player 1 priority
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
                PlayLandAction(Plains("plains-card-p2-a")),
                CastCreatureAction(sanctuaryCatP2)
            )
        ),
        // Turn 2 - player 1 priority
        emptyMap(),
        // Turn 3 - player 2 priority
        mapOf(
            Step.FirstMainPhaseStep to mutableListOf<Action>(
                PlayLandAction(Plains("plains-card-p2-b"))
            ),
            Step.CombatPhaseDeclareAttackersStep to mutableListOf<Action>(
                DeclareAttackersAction(listOf(AttackAction(sanctuaryCatP2, player1)))
            )
        ),
        // Turn 4 - player 1 priority
        emptyMap(),
        // Turn 5 - player 2 priority
        emptyMap(),
        // Turn 6 - player 1 priority
        mapOf(
            Step.CombatPhaseDeclareBlockersStep to mutableListOf<Action>(
                DeclareBlockersAction(listOf(BlockAction(sanctuaryCatP1, listOf(sanctuaryCatP2))))
            )
        )
    )


    val gameLoop = FirstNaiveGameLoop()
    gameLoop.playTurns(player1, player2, 7)
}