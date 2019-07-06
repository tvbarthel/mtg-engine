package fr.tvbarthel.mtg.experimentation.actorgameloop.actor

import fr.tvbarthel.mtg.experimentation.*
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent
import kotlin.math.min

@Suppress("FoldInitializerAndIfToElvis")
class DeclareBlockersActor : Actor {

    override fun onEventReceived(event: Event) {
        if (event !is ResolveActionEvent) {
            return
        }

        val action = event.actionContext.action
        if (action !is DeclareBlockersAction) {
            return
        }

        val turnContext = event.stepContext.turnContext
        turnContext.blockActions.addAll(action.blockActions)
    }

    override fun isAlive(): Boolean = true

}