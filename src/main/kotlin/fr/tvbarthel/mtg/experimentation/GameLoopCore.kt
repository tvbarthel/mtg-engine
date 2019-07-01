package fr.tvbarthel.mtg.experimentation

/**
 * This file is a collection of classes and interfaces meant to provide a flexible framework
 * to quickly craft and test different game loop approaches.
 *
 * The goal is not to have a clean and ready-to-use game loop implementation, but rather to
 * iterate quickly on different game loop implementations to see their advantages and disadvantages
 * to determine the best strategies to adopt at a bigger scale.
 *
 * Note: in those experiments, a game loop is not responsible of verifying if actions are valid or not.
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

class PassAction : Action {
    override fun toString(): String {
        return "Pass Action"
    }
}

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

class CastInstantAction(val instantCard: InstantCard) : Action {
    override fun toString(): String {
        return "CastInstant Action $instantCard"
    }
}

class ActivateAbilityAction(val ability: Ability) : Action {
    override fun toString(): String {
        return "ActivateAbility Action $ability"
    }
}

class DeclareAttackersAction(val attackActions: List<AttackAction>) : Action {

    constructor(creatureCard: CreatureCard, target: Player) : this(AttackAction(creatureCard, target))

    constructor(attackAction: AttackAction) : this(listOf(attackAction))

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

    constructor(blockedCreature: CreatureCard, blockingCreature: CreatureCard) : this(
        BlockAction(
            blockedCreature,
            listOf(blockingCreature)
        )
    )

    constructor(blockAction: BlockAction) : this(listOf(blockAction))

    override fun toString(): String {
        return "DeclareBlockers Action $blockActions"
    }
}

class BlockAction(val blockedCreature: CreatureCard, val blockingCreatures: List<CreatureCard>) : Action {
    override fun toString(): String {
        return "Block Action $blockedCreature blocked by $blockingCreatures"
    }
}

class SelectCreatureAction(val selectedCreature: CreatureCard) : Action {
    override fun toString(): String {
        return "SelectCreature Action $selectedCreature"
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

    var graveyard: MutableList<Card> = mutableListOf()

    abstract fun getAction(turn: Int, step: Step): Action?

}

class ScriptedActionBuilder(private val player1: ScriptedPlayer, private val player2: ScriptedPlayer) {

    private var nbTurns: Int = 0
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
        nbTurns += 1

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

    fun playTurns(gameLoop: GameLoop) {
        player1.scriptedActions = getActions(player1)
        player2.scriptedActions = getActions(player2)
        gameLoop.playTurns(player1, player2, nbTurns)
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

class ModifiableIntValue(private val initialValue: Int) {

    private var currentValue = initialValue
    private val modifiers = mutableListOf<IntValueModifier>()

    fun getCurrentValue(): Int {
        return currentValue
    }

    fun addModifier(owner: Any, amount: Int) {
        addModifier(IntValueModifier(owner, amount))
    }

    fun addModifier(modifier: IntValueModifier) {
        currentValue += modifier.amount
        modifiers.add(modifier)
    }

    fun removeModifiers(owner: Any) {
        val modifiersToRemove = modifiers.filter { modifier -> modifier.owner == owner }

        for (modifierToRemove in modifiersToRemove) {
            currentValue -= modifierToRemove.amount
            modifiers.remove(modifierToRemove)
        }
    }

    fun hasModifier(owner: Any): Boolean {
        return modifiers.firstOrNull { modifier -> modifier.owner == owner } != null
    }

}

class IntValueModifier(val owner: Any, val amount: Int)

class ModifiableBooleanValue(private val initialValue: Boolean) {
    private val modifiers = mutableListOf<BooleanValueModifier>()

    fun getCurrentValue(): Boolean {
        if (modifiers.isEmpty()) {
            return initialValue
        }

        return modifiers.last().value
    }

    fun addModifier(modifier: BooleanValueModifier) {
        modifiers.add(modifier)
    }

    fun removeModifier(modifier: BooleanValueModifier) {
        modifiers.remove(modifier)
    }

    fun removeModifiers(owner: Any) {
        modifiers.removeAll { modifier -> modifier.owner == owner }
    }

    fun hasModifier(owner: Any): Boolean {
        return modifiers.firstOrNull { modifier -> modifier.owner == owner } != null
    }
}

class BooleanValueModifier(val owner: CreatureCard, val value: Boolean)

interface Ability

abstract class Card(val id: String) {
    val abilities = mutableListOf<Ability>()

    abstract fun getName(): String
}

abstract class CreatureCard(
    id: String,
    private val initialPower: Int,
    private val initialToughness: Int,
    private val initialIndestructible: Boolean = false,
    private val initialHaste: Boolean = false
) : Card(id) {

    val power = ModifiableIntValue(initialPower)
    val toughness = ModifiableIntValue(initialToughness)
    val indestructible = ModifiableBooleanValue(initialIndestructible)
    val haste = ModifiableBooleanValue(initialHaste)

    override fun toString(): String {
        return "CreatureCard{name: ${getName()}, initialPower:$initialPower, initialToughness:$initialToughness}"
    }

    fun isIndestructible(): Boolean {
        return indestructible.getCurrentValue()
    }

    fun hasHaste(): Boolean {
        return haste.getCurrentValue()
    }
}

class SanctuaryCat(id: String) : CreatureCard(id, 1, 2) {
    override fun getName() = "SanctuaryCat"
}

class BenalishMarshal(id: String) : CreatureCard(id, 3, 3) {
    override fun getName() = "Benalish Marshal"
}

class DauntlessBodyguard(id: String) : CreatureCard(id, 2, 1) {
    override fun getName() = "Dauntless Bodyguard"

    class SacrificeToGiveIndestructibleAbility(val owner: DauntlessBodyguard, val target: CreatureCard) : Ability
}

class GhituLavarunner(suffix: String) : CreatureCard("ghitu-lavaruner-$suffix", 1, 2) {
    override fun getName() = "Ghitu Lavarunner"
}

class KnightToken(suffix: String) : CreatureCard("knight-token-$suffix", 2, 2) {
    override fun getName() = "Knight Token"
}

class FakeCreature(id: String, power: Int, toughness: Int) : CreatureCard(id, power, toughness) {
    override fun getName() = "FakeCreaure"
}

abstract class LandCard(id: String) : Card(id)

abstract class BasicLandCard(id: String) : LandCard(id)

class Plains(id: String) : BasicLandCard(id) {
    override fun getName() = "Plains"
}

abstract class EnchantmentCard(id: String) : Card(id) {
    override fun toString() = "EnchantmentCard{name: ${getName()}}"
}

abstract class SagaCard(id: String) : EnchantmentCard(id) {
    override fun toString() = "SagaCard{name: ${getName()}}"
}

class AjanisWelcome(id: String) : EnchantmentCard(id) {
    override fun getName() = "Ajani's Welcome"
}

class HistoryOfBenalia(suffix: String) : SagaCard("history-of-benalia-$suffix") {
    override fun getName() = "History Of Benalia"
}

abstract class InstantCard(id: String) : Card(id) {
    override fun toString() = "InstantCard{name: ${getName()}"
}

class FakeInstant(suffix: String) : InstantCard("fake-instant-$suffix") {
    override fun getName() = "FakeInstant $id"
}

class Shock(idSuffix: String, val target: Any) : InstantCard("shock-$idSuffix") {
    override fun getName() = "Shock"
}