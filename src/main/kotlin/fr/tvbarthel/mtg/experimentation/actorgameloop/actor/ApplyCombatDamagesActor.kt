package fr.tvbarthel.mtg.experimentation.actorgameloop.actor

import fr.tvbarthel.mtg.experimentation.*
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ExitBattlefieldEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.StartStepEvent
import kotlin.math.min

class ApplyCombatDamagesActor(
    private val gameLoop: ActorGameLoop
) : Actor {

    override fun onEventReceived(event: Event) {
        if (event !is StartStepEvent) {
            return
        }

        if (event.stepContext.step != Step.CombatPhaseDamageStep) {
            return
        }

        resolveCombatDamages(event.stepContext)
    }

    override fun isAlive(): Boolean = true


    private fun resolveCombatDamages(context: StepContext) {
        val blockerMap = context.turnContext.blockActions
            .map { blockAction -> blockAction.blockedCreature.id to blockAction }
            .toMap()

        for (attackAction in context.turnContext.attackActions) {
            val blockerAction = blockerMap[attackAction.creatureCard.id]

            if (blockerAction != null) {
                resolveBlockAction(context, blockerAction)
            } else {
                println("\t ${attackAction.creatureCard} deals ${attackAction.creatureCard.power.getCurrentValue()} to ${attackAction.target}")
                attackAction.target.life -= attackAction.creatureCard.power.getCurrentValue()
            }
        }
    }

    private fun resolveBlockAction(
        context: StepContext,
        blockAction: BlockAction
    ) {
        val attackingPlayer = context.turnContext.activePlayer
        val defendingPlayer = context.turnContext.opponentPlayer

        val blockedCreature = blockAction.blockedCreature
        val blockingCreatures = blockAction.blockingCreatures.toMutableList()
        println("\t $blockedCreature blocked by $blockingCreatures")

        var remainingDamagesToDealToBlockingCreatures = blockedCreature.power.getCurrentValue()
        var damagesDealtToBlockedCreature = 0

        while (remainingDamagesToDealToBlockingCreatures > 0 && blockingCreatures.isNotEmpty()) {
            val blockingCreature = blockingCreatures.removeAt(0)
            damagesDealtToBlockedCreature += blockingCreature.power.getCurrentValue()
            val damagesDealtToBlockingCreature =
                min(remainingDamagesToDealToBlockingCreatures, blockingCreature.toughness.getCurrentValue())
            remainingDamagesToDealToBlockingCreatures -= damagesDealtToBlockingCreature

            val damageToBlockedCreature = IntValueModifier(context.turnContext, -damagesDealtToBlockedCreature)
            val damageToBlockingCreature = IntValueModifier(context.turnContext, -damagesDealtToBlockingCreature)

            blockedCreature.toughness.addModifier(damageToBlockedCreature)
            blockingCreature.toughness.addModifier(damageToBlockingCreature)

            if (blockingCreature.toughness.getCurrentValue() <= 0) {
                if (blockingCreature.isIndestructible()) {
                    println("\t Blocking creature is indestructible. It won't die.")
                } else {
                    sendExitBattlefieldEvent(blockingCreature, defendingPlayer)
                    defendingPlayer.board.remove(blockingCreature)
                    println("\t Blocking creature $blockingCreature dies.")
                }
            }

            if (blockedCreature.toughness.getCurrentValue() <= 0) {
                if (blockedCreature.isIndestructible()) {
                    println("\t Blocked creature is indestructible. It won't die.")
                } else {
                    sendExitBattlefieldEvent(blockedCreature, attackingPlayer)
                    attackingPlayer.board.remove(blockedCreature)
                    println("\t Blocked creature $blockedCreature dies.")
                }
                break
            }
        }
    }

    private fun sendExitBattlefieldEvent(card: Card, owner: Player) {
        val event = ExitBattlefieldEvent(owner, card)
        gameLoop.sendEvent(event)
    }
}