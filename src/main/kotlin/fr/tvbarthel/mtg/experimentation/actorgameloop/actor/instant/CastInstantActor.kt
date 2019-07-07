package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.instant

import fr.tvbarthel.mtg.experimentation.CastInstantAction
import fr.tvbarthel.mtg.experimentation.Shock
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.EnteredGraveyardEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent

@Suppress("FoldInitializerAndIfToElvis")
class CastInstantActor(private val gameLoop: ActorGameLoop) : Actor {

    override fun onEventReceived(event: Event) {
        if (event !is ResolveActionEvent) {
            return
        }

        val action = event.actionContext.action
        if (action !is CastInstantAction) {
            return
        }

        val player = event.actionContext.activePlayer
        val card = action.instantCard

        if (card is Shock) {
            val shockActor = ShockActor(card, player, gameLoop)
            shockActor.onEventReceived(event)
        }

        player.graveyard.add(card)
        val enteredGraveyardEvent = EnteredGraveyardEvent(card, player)
        gameLoop.sendEvent(enteredGraveyardEvent)
    }

    override fun isAlive(): Boolean = true
}