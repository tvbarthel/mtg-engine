package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.enchantment

import fr.tvbarthel.mtg.experimentation.AjanisWelcome
import fr.tvbarthel.mtg.experimentation.CreatureCard
import fr.tvbarthel.mtg.experimentation.Player
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.EnterBattlefieldEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event

class AjanisWelcomeActor(
    private val card: AjanisWelcome,
    private val owner: Player
) : Actor {

    override fun onEventReceived(event: Event) {
        if (event !is EnterBattlefieldEvent) {
            return
        }

        if (event.card !is CreatureCard) {
            return
        }

        if (event.player != owner) {
            return
        }

        println("\t $card triggered for player $owner)")
        event.player.life += 1
    }

    override fun isAlive(): Boolean {
        return owner.board.contains(card)
    }

}