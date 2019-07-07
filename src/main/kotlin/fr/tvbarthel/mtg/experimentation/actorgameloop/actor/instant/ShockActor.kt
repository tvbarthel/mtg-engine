package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.instant

import fr.tvbarthel.mtg.experimentation.*
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ExitBattlefieldEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent

@Suppress("FoldInitializerAndIfToElvis")
class ShockActor(
    val shock: Shock,
    val owner: Player,
    val gameLoop: ActorGameLoop
) : Actor {

    override fun onEventReceived(event: Event) {
        if (event !is ResolveActionEvent) {
            return
        }

        val action = event.actionContext.action
        if (action !is CastInstantAction) {
            return
        }

        val player = event.actionContext.activePlayer
        if (action.instantCard != shock || player != owner) {
            return
        }

        if (shock.target is Player) {
            shock.target.life -= 2
            return
        }

        if (shock.target is CreatureCard) {
            val target = shock.target
            val turnContext = event.stepContext.turnContext
            target.toughness.addModifier(turnContext, -2)

            val isKilled = !target.isIndestructible()
                    && target.toughness.getCurrentValue() <= 0

            if (isKilled) {
                println("\t $shock killed $target")
                val targetOwner = getOwner(turnContext, target)

                val exitBattlefieldEvent = ExitBattlefieldEvent(targetOwner, target)
                gameLoop.sendEvent(exitBattlefieldEvent)

                targetOwner.board.remove(target)
            }
            return
        }
    }

    override fun isAlive(): Boolean = false

    private fun getOwner(turnContext: TurnContext, creatureCard: CreatureCard): Player {
        return when {
            turnContext.activePlayer.board.contains(creatureCard) -> turnContext.activePlayer
            turnContext.opponentPlayer.board.contains(creatureCard) -> turnContext.opponentPlayer
            else -> throw IllegalArgumentException("No owner found for creature $creatureCard")
        }
    }
}