package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.enchantment

import fr.tvbarthel.mtg.experimentation.*
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.*

class HistoryOfBenaliaActor(
    val historyOfBenalia: HistoryOfBenalia,
    private val owner: Player,
    private val gameLoop: ActorGameLoop
) : Actor {

    private var needToCleanup = false

    override fun onEventReceived(event: Event, stepContext: StepContext) {
        if (event is ResolveActionEvent
            && event.actionContext.action is CastEnchantmentAction
            && event.actionContext.action.enchantmentCard == historyOfBenalia
        ) {
            increaseLoreCounter(stepContext)
            return
        }

        if (event is StartStepEvent
            && event.stepContext.step == Step.BeginningPhaseDrawStep
            && event.stepContext.turnContext.activePlayer == owner
        ) {
            increaseLoreCounter(stepContext)
            return
        }

        if (event is EndStepEvent
            && event.stepContext.step == Step.EndingPhaseCleanupStep
        ) {
            cleanup()
        }
    }

    override fun isAlive(): Boolean {
        return owner.board.contains(historyOfBenalia) || needToCleanup
    }

    private fun increaseLoreCounter(stepContext: StepContext) {
        historyOfBenalia.loreCounter += 1

        when (historyOfBenalia.loreCounter) {
            1 -> {
                val knightToken = KnightToken("${historyOfBenalia.id}-1")
                val enterBattlefieldEvent = EnterBattlefieldEvent(owner, knightToken)
                gameLoop.sendEvent(enterBattlefieldEvent, stepContext)
                owner.board.add(knightToken)
            }
            2 -> {
                val knightToken = KnightToken("${historyOfBenalia.id}-2")
                val enterBattlefieldEvent = EnterBattlefieldEvent(owner, knightToken)
                gameLoop.sendEvent(enterBattlefieldEvent, stepContext)
                owner.board.add(knightToken)
            }
            3 -> {
                owner.board.filterIsInstance<CreatureCard>()
                    .filter { creatureCard -> creatureCard.hasType(CreatureType.KNIGHT) }
                    .forEach { creatureCard ->
                        val powerModifier = IntValueModifier(historyOfBenalia, 2)
                        val toughnessModifier = IntValueModifier(historyOfBenalia, 1)

                        creatureCard.power.addModifier(powerModifier)
                        creatureCard.toughness.addModifier(toughnessModifier)

                        needToCleanup = true
                    }
            }
            else -> {
                throw IllegalArgumentException("Invalid lore counter ${historyOfBenalia.loreCounter}")
            }
        }

        if (historyOfBenalia.loreCounter >= 3) {
            owner.board.remove(historyOfBenalia)
            owner.graveyard.add(historyOfBenalia)
        }
    }

    private fun cleanup() {
        owner.board.filterIsInstance<CreatureCard>()
            .forEach { creatureCard ->
                creatureCard.toughness.removeModifiers(historyOfBenalia)
                creatureCard.power.removeModifiers(historyOfBenalia)
            }
    }
}