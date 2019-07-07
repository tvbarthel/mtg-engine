package fr.tvbarthel.mtg.experimentation.actorgameloop.event

import fr.tvbarthel.mtg.experimentation.Card
import fr.tvbarthel.mtg.experimentation.Player

class EnteredGraveyardEvent(val card: Card, val owner: Player) : Event