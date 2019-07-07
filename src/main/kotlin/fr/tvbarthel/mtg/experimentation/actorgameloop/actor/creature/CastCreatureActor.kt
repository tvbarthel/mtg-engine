package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.creature

import fr.tvbarthel.mtg.experimentation.BenalishMarshal
import fr.tvbarthel.mtg.experimentation.CastCreatureAction
import fr.tvbarthel.mtg.experimentation.GhituLavarunner
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.EnterBattlefieldEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent

@Suppress("FoldInitializerAndIfToElvis")
class CastCreatureActor(
    private val gameLoop: ActorGameLoop
) : Actor {

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

        if (creatureCard is BenalishMarshal) {
            val benalishMarshalActor = BenalishMarshalActor(creatureCard, player)
            gameLoop.attachActor(benalishMarshalActor)
        }

        if (creatureCard is GhituLavarunner) {
            val ghituLavarunnerActor = GhituLavarunnerActor(creatureCard, player)
            gameLoop.attachActor(ghituLavarunnerActor)
        }

        val enterBattlefieldEvent = EnterBattlefieldEvent(player, creatureCard)
        gameLoop.sendEvent(enterBattlefieldEvent)
    }

    override fun isAlive(): Boolean = true

}