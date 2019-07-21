package fr.tvbarthel.mtg.engine.card.white

import fr.tvbarthel.mtg.engine.Card
import fr.tvbarthel.mtg.engine.card.type.Creature

/**
 * https://gatherer.wizards.com/Pages/Card/Details.aspx?multiverseid=262864
 */
class SanctuaryCat : Card(
    id = 262864,
    types = listOf(Creature()),
    manaCost = Property(1),
    power = Property(1),
    toughness = Property(2)
)