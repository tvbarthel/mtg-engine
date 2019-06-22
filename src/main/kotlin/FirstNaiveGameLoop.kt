fun main() {
    println("First naive game loop")

    val player1 = ScriptedPlayer("Ava")
    val player2 = ScriptedPlayer("Williams")
    val sanctuaryCat = SanctuaryCat()

    player1.scriptedActions = listOf(
        // Turn 0 - player 1 priority
        mapOf(
            Step.FirstMainPhaseStep to mutableListOf<Action>(
                PlayLandAction()
            )
        ),
        // Turn 1 - player 2 priority
        emptyMap(),
        // Turn 2 - player 1 priority
        mapOf(
            Step.FirstMainPhaseStep to mutableListOf<Action>(
                PlayLandAction()
            )
        ),
        // Turn 3 - player 2 priority
        emptyMap(),
        // Turn 4 - player 1 priority
        emptyMap()
    )

    player2.scriptedActions = listOf(
        // Turn 0 - player 1 priority
        emptyMap(),
        // Turn 1 - player 2 priority
        mapOf(
            Step.FirstMainPhaseStep to mutableListOf<Action>(
                PlayLandAction(),
                SpawnCreatureAction(sanctuaryCat)
            )
        ),
        // Turn 2 - player 1 priority
        emptyMap(),
        // Turn 3 - player 2 priority
        mapOf(
            Step.FirstMainPhaseStep to mutableListOf<Action>(
                PlayLandAction()
            ),
            Step.CombatPhaseDeclareAttackersStep to mutableListOf<Action>(
                DeclareAttackersAction(listOf(AttackAction(sanctuaryCat, player1)))
            )
        ),
        // Turn 4 - player 1 priority
        emptyMap()
    )


    val gameLoop = FirstNaiveGameLoop(player1, player2)

    for (turn in 0..5) {
        gameLoop.playTurn(turn)
    }
}


class FirstNaiveGameLoop(private val player1: Player, private val player2: Player) {

    private val attackActions = mutableListOf<AttackAction>()

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
        handleStepStart(step)

        while (true) {
            val player1Action = player1.getAction(turn, step)
            println("\t Player $player1 Action -> $player1Action")
            handlePlayerAction(player1, player2, player1Action)

            val player2Action = player2.getAction(turn, step)
            println("\t Player $player2 Action -> $player2Action")
            handlePlayerAction(player2, player1, player2Action)

            if (player1Action == null && player2Action == null) {
                break
            }
        }
        handleStepEnd(step)
        println("Playing step for turn $turn $step <----")
    }

    private fun handlePlayerAction(player: Player, opponent: Player, action: Action?) {
        if (action == null) {
            return
        }

        if (action is DeclareAttackersAction) {
            attackActions.addAll(action.attackActions)
        }
    }

    private fun handleStepStart(step: Step) {
        when (step) {
            Step.CombatPhaseDamageStep -> {
                for (attackAction in attackActions) {
                    println("\t ${attackAction.creatureCard} deals ${attackAction.creatureCard.power} to ${attackAction.target}")
                    attackAction.target.life -= attackAction.creatureCard.power
                }
            }
            else -> {

            }
        }
    }

    private fun handleStepEnd(step: Step) {
        when (step) {
            Step.EndingPhaseCleanupStep -> {
                println("\t cleaning attack actions")
                attackActions.clear()
            }
            else -> {
            }
        }
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

class SpawnCreatureAction(val creatureCard: CreatureCard) : Action {

    override fun toString(): String {
        return "SpawnCreature Action $creatureCard"
    }

}

class DeclareAttackersAction(val attackActions: List<AttackAction>) : Action {
    override fun toString(): String {
        return "DeclareAttackers Action $attackActions"
    }
}

class AttackAction(val creatureCard: CreatureCard, val target: Player) {
    override fun toString(): String {
        return "Attack Action $creatureCard -> $target"
    }
}

abstract class Player {

    var life: Int = 20

    abstract fun getAction(turn: Int, step: Step): Action?

}

class ScriptedPlayer(private val name: String) : Player() {

    lateinit var scriptedActions: List<Map<Step, MutableList<Action>>>

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

    override fun toString(): String {
        return "ScriptedPlayer{$name, life:$life}"
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