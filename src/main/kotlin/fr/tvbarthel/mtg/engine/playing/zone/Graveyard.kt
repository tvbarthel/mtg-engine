package fr.tvbarthel.mtg.engine.playing.zone

import fr.tvbarthel.mtg.engine.Zone

/**
 * The graveyard is one of the six main zones in the game of Magic: The Gathering.
 *
 * The graveyard is the pile into which you discard, where instant and sorcery spells go once they have resolved, and
 * where permanents go when they have been sacrificed, destroyed, or "put into the graveyard" due to a state-based
 * effect.
 *
 * Cards in the graveyard are usually no longer relevant to the game, but some mechanics do interact with the graveyard.
 * Examples are Flashback, unearth, dredge and delve. A notable creature type that often comes back from the graveyard
 * is Zombies. The threshold and delirium mechanics also make use of the graveyard. Decks such as reanimator are built
 * to use or re-use cards in the graveyard, often making it as useful a resource as a player's hand.
 *
 * https://mtg.gamepedia.com/Graveyard
 */
class Graveyard : Zone()