package fr.tvbarthel.mtg.experimentation.actorgameloop.actor

import fr.tvbarthel.mtg.experimentation.CastCreatureAction
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent

@Suppress("FoldInitializerAndIfToElvis")
class CastCreatureActor : Actor {

    override fun onEventReceived(event: Event) {
        if (event !is ResolveActionEvent) {
            return
        }

        val action = event.actionContext.action
        if (action !is CastCreatureAction) {
            return
        }

        val player = event.actionContext.activePlayer
        val creatureCard = action.creatureCard
        player.board.add(creatureCard)
    }

    override fun isAlive(): Boolean = true

}