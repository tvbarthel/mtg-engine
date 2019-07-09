package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.creature

import fr.tvbarthel.mtg.experimentation.*
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.EnterBattlefieldEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent

@Suppress("FoldInitializerAndIfToElvis")
class CastCreatureActor(
    private val gameLoop: ActorGameLoop
) : Actor {

    override fun onEventReceived(event: Event, stepContext: StepContext) {
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
        } else if (creatureCard is GhituLavarunner) {
            val ghituLavarunnerActor = GhituLavarunnerActor(creatureCard, player)
            gameLoop.attachActor(ghituLavarunnerActor)
        } else if (creatureCard is DauntlessBodyguard) {
            val dauntlessBodyguardActor = DauntlessBodyguardActor(creatureCard, player, gameLoop)
            gameLoop.attachActor(dauntlessBodyguardActor)
        }

        val enterBattlefieldEvent = EnterBattlefieldEvent(player, creatureCard)
        gameLoop.sendEvent(enterBattlefieldEvent, stepContext)
    }

    override fun isAlive(): Boolean = true

}