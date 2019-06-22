fun main() {
    println("First naive game loop")

    val player1 = ScriptedPlayer(
        scriptedActions = listOf(
            // Turn 1 - player 1 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    PlayLandAction()
                )
            ),
            // Turn 2 - player 2 priority
            emptyMap(),
            // Turn 3 - player 1 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    PlayLandAction()
                )
            ),
            // Turn 4 - player 2 priority
            emptyMap(),
            // Turn 5 - player 1 priority
            emptyMap()
        )
    )

    val player2 = ScriptedPlayer(
        scriptedActions = listOf(
            // Turn 1 - player 1 priority
            emptyMap(),
            // Turn 2 - player 2 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    PlayLandAction(),
                    SpawnCreature(SanctuaryCat())
                )
            ),
            // Turn 3 - player 1 priority
            emptyMap(),
            // Turn 4 - player 2 priority
            mapOf(
                Step.FirstMainPhaseStep to mutableListOf<Action>(
                    PlayLandAction()
                )
            ),
            // Turn 5 - player 1 priority
            emptyMap()
        )
    )

    val gameLoop = FirstNaiveGameLoop(player1, player2)

    for (turn in 0..5) {
        gameLoop.playTurn(turn)
    }
}


class FirstNaiveGameLoop(private val player1: Player, private val player2: Player) {

    fun playTurn(turn: Int) {
        playStep(turn, Step.BeginningPhaseUntapStep)
        playStep(turn, Step.BeginningPhaseUpKeepStep)
        playStep(turn, Step.BeginningPhaseDrawStep)
        playStep(turn, Step.FirstMainPhaseStep)
        playStep(turn, Step.CombatPhaseBeginningStep)
        playStep(turn, Step.CombatPhaseDeclareAttackersStep)
        playStep(turn, Step.CombatPhaseDeclareBlockersStep)
        playStep(turn, Step.CombatPhaseDamageStep)
        playStep(turn, Step.CombatPhaseEndStep)
        playStep(turn, Step.SecondMainPhaseStep)
        playStep(turn, Step.EndingPhaseEndStep)
        playStep(turn, Step.EndingPhaseCleanupStep)
    }

    private fun playStep(turn: Int, step: Step) {
        println("\nPlaying step for turn $turn $step ---->")
        while (true) {
            val player1Action = player1.getAction(turn, step)
            println("\t Player 1 Action -> $player1Action")

            val player2Action = player2.getAction(turn, step)
            println("\t Player 2 Action -> $player2Action")

            if (player1Action == null && player2Action == null) {
                break
            }
        }
        println("Playing step for turn $turn $step <----")
    }

}

enum class Step {
    BeginningPhaseUntapStep,
    BeginningPhaseUpKeepStep,
    BeginningPhaseDrawStep,
    FirstMainPhaseStep,
    CombatPhaseBeginningStep,
    CombatPhaseDeclareAttackersStep,
    CombatPhaseDeclareBlockersStep,
    CombatPhaseDamageStep,
    CombatPhaseEndStep,
    SecondMainPhaseStep,
    EndingPhaseEndStep,
    EndingPhaseCleanupStep
}

interface Action

class PlayLandAction : Action {
    override fun toString(): String {
        return "PlayLand Action"
    }
}

class SpawnCreature(val creatureCard: CreatureCard) : Action {

    override fun toString(): String {
        return "SpawnCreature Action $creatureCard"
    }

}

interface Player {

    fun getAction(turn: Int, step: Step): Action?

}

class ScriptedPlayer(private val scriptedActions: List<Map<Step, MutableList<Action>>>) : Player {

    override fun getAction(turn: Int, step: Step): Action? {
        if (turn >= scriptedActions.size) {
            return null
        }

        val actionMap = scriptedActions[turn]
        val actions = actionMap[step]

        if (actions == null) {
            return null
        }

        if (actions.size == 0) {
            return null
        }

        return actions.removeAt(0)
    }

}

interface Card {
    fun getName(): String
}

abstract class CreatureCard(val power: Int, val toughness: Int) : Card {

    override fun toString(): String {
        return "CreatureCard{name: ${getName()}, power:$power, toughness:$toughness}"
    }

}

class SanctuaryCat : CreatureCard(1, 2) {
    override fun getName() = "SanctuaryCat"
}