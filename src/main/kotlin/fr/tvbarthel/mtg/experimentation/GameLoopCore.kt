package fr.tvbarthel.mtg.experimentation

/**
 * This file is a collection of classes and interfaces meant to provide a flexible framework
 * to quickly craft and test different game loop approaches.
 *
 * The goal is not to have a clean and ready-to-use game loop implementation, but rather to
 * iterate quickly on different game loop implementations to see their advantages and disadvantages
 * to determine the best strategies to adopt at a bigger scale.
 */

abstract class GameLoop {
    abstract fun playTurn(turnContext: TurnContext)

    fun playTurns(player1: Player, player2: Player, nbTurn: Int) {
        for (turnIndex in 0 until nbTurn) {
            if (turnIndex % 2 == 0) {
                playTurn(TurnContext(turnIndex, player1, player2))
            } else {
                playTurn(TurnContext(turnIndex, player2, player1))
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

class PlayLandAction(val landCard: LandCard) : Action {
    override fun toString(): String {
        return "PlayLand Action"
    }
}

class CastCreatureAction(val creatureCard: CreatureCard) : Action {
    override fun toString(): String {
        return "CastCreature Action $creatureCard"
    }
}

class CastEnchantmentAction(val enchantmentCard: EnchantmentCard) : Action {
    override fun toString(): String {
        return "CastEnchantment Action $enchantmentCard"
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

class TurnContext(
    val turnIndex: Int,
    val activePlayer: Player,
    val opponentPlayer: Player
)

abstract class Player {

    var life: Int = 20

    var board: MutableList<Card> = mutableListOf()

    abstract fun getAction(turn: Int, step: Step): Action?

}


class ScriptedActionBuilder(private val player1: Player, private val player2: Player) {

    private val player1Actions = mutableListOf<MutableMap<Step, MutableList<Action>>>()
    private val player2Actions = mutableListOf<MutableMap<Step, MutableList<Action>>>()

    fun addTurn(allScriptedActions: Map<Step, List<Pair<ScriptedPlayer, Action>>>): ScriptedActionBuilder {
        val player1TurnActions = mutableMapOf<Step, MutableList<Action>>()
        val player2TurnActions = mutableMapOf<Step, MutableList<Action>>()

        allScriptedActions.forEach { (step, stepActions) ->
            val player1StepActions = mutableListOf<Action>()
            val player2StepActions = mutableListOf<Action>()

            stepActions.forEach { pairedAction ->
                val player = pairedAction.first
                val action = pairedAction.second

                when (player) {
                    player1 -> player1StepActions.add(action)
                    player2 -> player2StepActions.add(action)
                    else -> throw IllegalArgumentException("Invalid player")
                }
            }

            player1TurnActions[step] = player1StepActions
            player2TurnActions[step] = player2StepActions
        }

        player1Actions.add(player1TurnActions)
        player2Actions.add(player2TurnActions)

        return this
    }

    /**
     * Helper function to add a turn with no actions.
     */
    fun addTurn(): ScriptedActionBuilder {
        return addTurn(emptyMap())
    }

    /**
     * Helper function to add a turn with only one action.
     */
    fun addTurn(step: Step, player: ScriptedPlayer, action: Action): ScriptedActionBuilder {
        return addTurn(
            mapOf(
                step to listOf(
                    Pair(player, action)
                )
            )
        )
    }

    fun getActions(player: Player): List<MutableMap<Step, MutableList<Action>>> {
        return when (player) {
            player1 -> player1Actions
            player2 -> player2Actions
            else -> throw IllegalArgumentException("Invalid player")
        }
    }

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

class Modifier(val owner: Card, val value: Int)

abstract class Card(val id: String) {
    abstract fun getName(): String
}

abstract class CreatureCard(id: String, private val initialPower: Int, private val initialToughness: Int) : Card(id) {

    private var currentPower = initialPower
    private var currentToughness = initialToughness
    private val powerModifiers = mutableListOf<Modifier>()
    private val toughnessModifiers = mutableListOf<Modifier>()

    override fun toString(): String {
        return "CreatureCard{name: ${getName()}, initialPower:$initialPower, initialToughness:$initialToughness}"
    }

    fun getCurrentPower(): Int {
        return currentPower
    }

    fun getCurrentToughness(): Int {
        return currentToughness
    }

    fun addPowerModifier(powerModifier: Modifier) {
        powerModifiers.add(powerModifier)
        currentPower += powerModifier.value
    }

    fun addToughnessModifier(toughnessModifier: Modifier) {
        toughnessModifiers.add(toughnessModifier)
        currentToughness += toughnessModifier.value
    }

    fun removePowerModifiers(owner: Card) {
        powerModifiers.removeAll { modifier ->
            currentPower -= modifier.value
            modifier.owner == owner
        }
    }

    fun removeToughnessModifiers(owner: Card) {
        toughnessModifiers.removeAll { modifier ->
            currentToughness -= modifier.value
            modifier.owner == owner
        }
    }
}

class SanctuaryCat(id: String) : CreatureCard(id, 1, 2) {
    override fun getName() = "SanctuaryCat"
}

class BenalishMarshal(id: String) : CreatureCard(id, 3, 3) {
    override fun getName() = "Benalish Marshal"
}

class FakeCreature(id: String, power: Int, toughness: Int) : CreatureCard(id, power, toughness) {
    override fun getName() = "FakeCreaure"
}

abstract class LandCard(id: String) : Card(id)

abstract class BasicLandCard(id: String) : LandCard(id)

abstract class EnchantmentCard(id: String) : Card(id) {
    override fun toString() = "EnchantmentCard{name: ${getName()}}"
}

class AjanisWelcome(id: String) : EnchantmentCard(id) {
    override fun getName() = "Ajani's Welcome"
}

class Plains(id: String) : BasicLandCard(id) {
    override fun getName() = "Plains"
}