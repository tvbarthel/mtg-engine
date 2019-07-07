package fr.tvbarthel.mtg.experimentation.actorgameloop.actor

import fr.tvbarthel.mtg.experimentation.Card
import fr.tvbarthel.mtg.experimentation.CreatureCard
import fr.tvbarthel.mtg.experimentation.Step
import fr.tvbarthel.mtg.experimentation.TurnContext
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.EndStepEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event

class CleanToughnessModifierAfterTurnActor : Actor {

    override fun onEventReceived(event: Event) {
        if (event !is EndStepEvent) {
            return
        }

        if (event.stepContext.step != Step.EndingPhaseCleanupStep) {
            return
        }

        val turnContext = event.stepContext.turnContext
        removeToughnessModifierAssociatedWithTurn(turnContext, turnContext.activePlayer.board)
        removeToughnessModifierAssociatedWithTurn(turnContext, turnContext.opponentPlayer.board)
    }

    override fun isAlive(): Boolean = true

    private fun removeToughnessModifierAssociatedWithTurn(turnContext: TurnContext, cards: List<Card>) {
        cards.filterIsInstance<CreatureCard>()
            .forEach { creatureCard ->
                creatureCard.toughness.removeModifiers(turnContext)
            }
    }
}