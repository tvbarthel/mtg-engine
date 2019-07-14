package fr.tvbarthel.mtg.engine.card.type

import fr.tvbarthel.mtg.engine.Card

/**
 * Lands represent locations under the player's control, most of which can be used to generate mana. Because mana is
 * needed to use almost any card or ability, most decks need a high number of mana-producing lands (typically between
 * 33-40% of the total deck) in order to function effectively.
 *
 * https://mtg.gamepedia.com/Land
 */
class Land : Card.Type()