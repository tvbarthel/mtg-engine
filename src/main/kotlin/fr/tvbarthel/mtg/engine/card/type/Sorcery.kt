package fr.tvbarthel.mtg.engine.card.type

import fr.tvbarthel.mtg.engine.Card

/**
 * Sorceries, like instants, represent one-shot or short-term magical spells. They are never put onto battlefield;
 * instead, they take effect when their mana cost is paid and the spell resolves, and then are immediately put into its
 * owner's graveyard.
 *
 * https://mtg.gamepedia.com/Sorcery
 */
class Sorcery : Card.Type()