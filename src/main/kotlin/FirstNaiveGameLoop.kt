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
        mapOf(
            Step.FirstMainPhaseStep to mutableListOf<Action>(
                SpawnCreatureAction(sanctuaryCatP1)
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
                PlayLandAction(),
                SpawnCreatureAction(sanctuaryCatP2)
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


    val gameLoop = FirstNaiveGameLoop(player1, player2)

    for (turn in 0..6) {
        gameLoop.playTurn(turn)
    }
}


class FirstNaiveGameLoop(private val player1: Player, private val player2: Player) {

    private val attackActions = mutableListOf<AttackAction>()
    private val blockActions = mutableListOf<BlockAction>()

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

        if (action is DeclareBlockersAction) {
            blockActions.addAll(action.blockActions)
        }
    }

    private fun handleStepStart(step: Step) {
        when (step) {
            Step.CombatPhaseDamageStep -> {
                val blockerMap =
                    blockActions.map { blockAction -> blockAction.blockedCreature.id to blockAction }.toMap()

                for (attackAction in attackActions) {
                    val blockerAction = blockerMap[attackAction.creatureCard.id]

                    if (blockerAction != null) {
                        println("\t ${attackAction.creatureCard} blocked by ${blockerAction.blockingCreatures}")
                        // TODO apply damages to creatures
                    } else {
                        println("\t ${attackAction.creatureCard} deals ${attackAction.creatureCard.power} to ${attackAction.target}")
                        attackAction.target.life -= attackAction.creatureCard.power
                    }
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

                println("\t cleaning block actions")
                blockActions.clear()
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

class AttackAction(val creatureCard: CreatureCard, val target: Player) : Action {
    override fun toString(): String {
        return "Attack Action $creatureCard -> $target"
    }
}

class DeclareBlockersAction(val blockActions: List<BlockAction>) : Action {
    override fun toString(): String {
        return "DeclareBlockers Action $blockActions"
    }
}

class BlockAction(val blockedCreature: CreatureCard, val blockingCreatures: List<CreatureCard>) : Action {

    override fun toString(): String {
        return "Block Action $blockedCreature blocked by $blockingCreatures"
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

abstract class Card(val id: String) {
    abstract fun getName(): String
}

abstract class CreatureCard(id: String, val power: Int, val toughness: Int) : Card(id) {

    override fun toString(): String {
        return "CreatureCard{name: ${getName()}, power:$power, toughness:$toughness}"
    }

}

class SanctuaryCat(id: String) : CreatureCard(id, 1, 2) {
    override fun getName() = "SanctuaryCat"
}