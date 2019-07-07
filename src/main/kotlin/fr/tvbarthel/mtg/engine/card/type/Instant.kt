package fr.tvbarthel.mtg.engine.card.type

import fr.tvbarthel.mtg.engine.Card

/**
 * Instants, like sorceries, represent one-shot or short-term magical spells. They are never put into the in-play zone;
 * instead, they take effect when their mana cost is paid and the spell resolves, and then are immediately put into the
 * player's graveyard.
 *
 * https://mtg.gamepedia.com/Instant
 */
class Instant : Card.Type()