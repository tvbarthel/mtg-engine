package fr.tvbarthel.mtg.engine

/**
 * A zone is one of several places where a card or object can be placed during a game. There are normally seven zones:
 * library, hand, battlefield, graveyard, stack, exile, and command. Some older cards also use the ante zone.
 */
abstract class Zone(private val cards: MutableList<Card> = mutableListOf()) : MutableList<Card> by cards