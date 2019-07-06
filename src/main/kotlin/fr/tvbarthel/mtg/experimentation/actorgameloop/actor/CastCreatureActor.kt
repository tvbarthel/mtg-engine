package fr.tvbarthel.mtg.experimentation.actorgameloop.actor

import fr.tvbarthel.mtg.experimentation.CastCreatureAction
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.EnterBattlefieldEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent

@Suppress("FoldInitializerAndIfToElvis")
class CastCreatureActor(private val gameLoop: ActorGameLoop) : Actor {

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

        val event = EnterBattlefieldEvent(player, creatureCard)
        gameLoop.sendEvent(event)
    }

    override fun isAlive(): Boolean = true

}