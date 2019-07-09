package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.creature

import fr.tvbarthel.mtg.experimentation.*
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.*
import java.rmi.UnexpectedException

class DauntlessBodyguardActor(
    val dauntlessBodyguard: DauntlessBodyguard,
    val owner: Player,
    val gameLoop: ActorGameLoop
) : Actor {

    private var cleanupModifier: Pair<ModifiableBooleanValue, BooleanValueModifier>? = null

    override fun onEventReceived(event: Event, stepContext: StepContext) {
        if (event is EnterBattlefieldEvent) {
            handleEnterBattlefieldEvent(event, stepContext)
            return
        }

        if (event is ResolveActionEvent) {
            handleResolveActionEvent(event, stepContext)
            return
        }

        if (event is EndStepEvent) {
            handleEndStepEvent(event)
            return
        }
    }


    override fun isAlive(): Boolean {
        return owner.board.contains(dauntlessBodyguard) || cleanupModifier != null
    }

    private fun handleEnterBattlefieldEvent(event: EnterBattlefieldEvent, stepContext: StepContext) {
        if (event.player != owner) {
            return
        }

        if (event.card != dauntlessBodyguard) {
            return
        }

        val turnIndex = stepContext.turnContext.turnIndex
        val step = stepContext.step
        val action = owner.getAction(turnIndex, step)

        when (action) {
            is PassAction -> {
                // No-op
            }
            is SelectCreatureAction -> {
                val target = action.selectedCreature
                val ability = DauntlessBodyguard.SacrificeToGiveIndestructibleAbility(dauntlessBodyguard, target)
                dauntlessBodyguard.abilities.add(ability)
            }
            else -> throw UnexpectedException("Not a valid action after $dauntlessBodyguard enters battlefield")
        }
    }

    @Suppress("FoldInitializerAndIfToElvis")
    private fun handleResolveActionEvent(event: ResolveActionEvent, stepContext: StepContext) {
        val action = event.actionContext.action
        if (action !is ActivateAbilityAction) {
            return
        }

        val ability = action.ability
        if (ability !is DauntlessBodyguard.SacrificeToGiveIndestructibleAbility) {
            return
        }

        if (ability.owner != dauntlessBodyguard) {
            return
        }

        val player = event.actionContext.activePlayer
        if (player != owner) {
            return
        }

        // Give indestructible until end of turn
        val modifier = BooleanValueModifier(ability.owner, true)
        ability.target.indestructible.addModifier(modifier)
        cleanupModifier = Pair(ability.target.indestructible, modifier)
        println("\t ${ability.target} gains indestructible until end of turns.")

        // Sacrifice dauntless bodyguard
        val exitBattlefieldEvent = ExitBattlefieldEvent(owner, dauntlessBodyguard)
        gameLoop.sendEvent(exitBattlefieldEvent, stepContext)
        player.board.remove(ability.owner)

        player.graveyard.add(dauntlessBodyguard)
        val enteredGraveyardEvent = EnteredGraveyardEvent(dauntlessBodyguard, owner)
        gameLoop.sendEvent(enteredGraveyardEvent, stepContext)
        println("\t ${ability.owner} is sacrificed.")
    }

    private fun handleEndStepEvent(event: EndStepEvent) {
        if (event.stepContext.step != Step.EndingPhaseCleanupStep) {
            return
        }

        cleanupModifier?.let {
            it.first.removeModifier(it.second)
            cleanupModifier = null
        }
    }
}