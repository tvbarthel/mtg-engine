package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.enchantment

import fr.tvbarthel.mtg.experimentation.AjanisWelcome
import fr.tvbarthel.mtg.experimentation.CastEnchantmentAction
import fr.tvbarthel.mtg.experimentation.StepContext
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent

@Suppress("FoldInitializerAndIfToElvis")
class CastEnchantmentActor(private val gameLoop: ActorGameLoop) :
    Actor {

    override fun onEventReceived(event: Event, stepContext: StepContext) {
        if (event !is ResolveActionEvent) {
            return
        }

        val action = event.actionContext.action
        if (action !is CastEnchantmentAction) {
            return
        }

        val enchantmentCard = action.enchantmentCard
        val player = event.actionContext.activePlayer
        player.board.add(enchantmentCard)

        if (enchantmentCard is AjanisWelcome) {
            val ajanisWelcomeActor = AjanisWelcomeActor(enchantmentCard, player)
            gameLoop.attachActor(ajanisWelcomeActor)
        }
    }

    override fun isAlive(): Boolean = true
}