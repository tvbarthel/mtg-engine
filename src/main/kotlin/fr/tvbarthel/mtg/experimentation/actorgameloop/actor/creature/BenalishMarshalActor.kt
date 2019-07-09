package fr.tvbarthel.mtg.experimentation.actorgameloop.actor.creature

import fr.tvbarthel.mtg.experimentation.BenalishMarshal
import fr.tvbarthel.mtg.experimentation.CreatureCard
import fr.tvbarthel.mtg.experimentation.Player
import fr.tvbarthel.mtg.experimentation.StepContext
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.EnterBattlefieldEvent
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ExitBattlefieldEvent

class BenalishMarshalActor(
    private val benalishMarshal: BenalishMarshal,
    private val owner: Player
) : Actor {

    override fun onEventReceived(event: Event, stepContext: StepContext) {
        if (event is EnterBattlefieldEvent) {
            handleEnterBattlefieldEvent(event)
            return
        }

        if (event is ExitBattlefieldEvent) {
            handleExitBattlefieldEvent(event)
            return
        }
    }

    override fun isAlive(): Boolean {
        return owner.board.contains(benalishMarshal)
    }

    private fun handleEnterBattlefieldEvent(event: EnterBattlefieldEvent) {
        if (event.card == benalishMarshal) {
            owner.board
                .filterIsInstance<CreatureCard>()
                .filter { otherCreature -> otherCreature != benalishMarshal }
                .forEach { creatureCard -> applyEffect(creatureCard) }
        } else if (event.player == owner && event.card is CreatureCard) {
            applyEffect(event.card)
        }
    }

    private fun handleExitBattlefieldEvent(event: ExitBattlefieldEvent) {
        if (event.card == benalishMarshal) {
            owner.board
                .filterIsInstance<CreatureCard>()
                .filter { otherCreature -> otherCreature != benalishMarshal }
                .forEach { creatureCard -> removeEffect(creatureCard) }
        }
    }

    private fun applyEffect(creature: CreatureCard) {
        creature.power.addModifier(benalishMarshal, 1)
        creature.toughness.addModifier(benalishMarshal, 1)
    }

    private fun removeEffect(creature: CreatureCard) {
        creature.power.removeModifiers(benalishMarshal)
        creature.toughness.removeModifiers(benalishMarshal)
    }

}