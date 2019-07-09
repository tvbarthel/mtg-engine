package fr.tvbarthel.mtg.experimentation.actorgameloop.event

import fr.tvbarthel.mtg.experimentation.Card
import fr.tvbarthel.mtg.experimentation.Player

class EnterBattlefieldEvent(
    val player: Player,
    val card: Card
) : Event