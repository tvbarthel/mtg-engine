package fr.tvbarthel.mtg.experimentation.actorgameloop.actor

import fr.tvbarthel.mtg.experimentation.CastEnchantmentAction
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent


@Suppress("FoldInitializerAndIfToElvis")
class CastEnchantmentActor : Actor {

    override fun onEventReceived(event: Event) {
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
    }

    override fun isAlive(): Boolean = true
}