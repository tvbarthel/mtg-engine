package fr.tvbarthel.mtg.experimentation.actorgameloop.actor

import fr.tvbarthel.mtg.experimentation.PlayLandAction
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent

@Suppress("FoldInitializerAndIfToElvis")
class PlayLandActor : Actor {

    override fun onEventReceived(event: Event) {
        if (event !is ResolveActionEvent) {
            return
        }

        val actionContext = event.actionContext
        val action = actionContext.action

        if (action !is PlayLandAction) {
            return
        }

        actionContext.activePlayer.board.add(action.landCard)
    }

    override fun isAlive(): Boolean {
        return true
    }

}