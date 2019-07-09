package fr.tvbarthel.mtg.experimentation.actorgameloop.actor

import fr.tvbarthel.mtg.experimentation.DeclareAttackersAction
import fr.tvbarthel.mtg.experimentation.StepContext
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent

@Suppress("FoldInitializerAndIfToElvis")
class DeclareAttackersActor : Actor {

    override fun onEventReceived(event: Event, stepContext: StepContext) {
        if (event !is ResolveActionEvent) {
            return
        }

        val action = event.actionContext.action
        if (action !is DeclareAttackersAction) {
            return
        }

        val turnContext = event.stepContext.turnContext
        turnContext.attackActions.addAll(action.attackActions)
    }

    override fun isAlive(): Boolean = true

}