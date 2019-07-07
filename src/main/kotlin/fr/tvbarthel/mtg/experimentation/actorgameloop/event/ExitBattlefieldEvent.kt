package fr.tvbarthel.mtg.experimentation.actorgameloop.event

import fr.tvbarthel.mtg.experimentation.Card
import fr.tvbarthel.mtg.experimentation.Player

class ExitBattlefieldEvent(val player: Player, val card: Card) : Event