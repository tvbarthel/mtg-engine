package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.creature

import fr.tvbarthel.mtg.experimentation.GhituLavarunner
import fr.tvbarthel.mtg.experimentation.InstantCard
import fr.tvbarthel.mtg.experimentation.Player
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.EnterBattlefieldEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.EnteredGraveyardEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event

class GhituLavarunnerActor(
    val ghituLavarunner: GhituLavarunner,
    val owner: Player
) : Actor {

    private var bonusActivated = false

    override fun onEventReceived(event: Event) {
        if (event is EnterBattlefieldEvent) {
            handleEnterBattlefieldEvent(event)
            return
        }

        if (event is EnteredGraveyardEvent) {
            handleEnteredGraveyardEvent(event)
            return
        }
    }

    private fun handleEnteredGraveyardEvent(event: EnteredGraveyardEvent) {
        if (event.owner != owner) {
            return
        }

        if (event.card !is InstantCard) {
            return
        }

        activateBonusIfPossible()
    }

    override fun isAlive(): Boolean {
        return owner.board.contains(ghituLavarunner)
    }

    private fun handleEnterBattlefieldEvent(event: EnterBattlefieldEvent) {
        if (event.card != ghituLavarunner) {
            return
        }

        activateBonusIfPossible()
    }

    private fun activateBonusIfPossible() {
        if (bonusActivated) {
            return
        }

        if (owner.graveyard.count { card -> card is InstantCard } < 2) {
            return
        }

        ghituLavarunner.power.addModifier(ghituLavarunner, 1)
        ghituLavarunner.haste.addModifier(ghituLavarunner, true)
        bonusActivated = true
    }
}