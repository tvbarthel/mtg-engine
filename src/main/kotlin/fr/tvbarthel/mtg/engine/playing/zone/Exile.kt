package fr.tvbarthel.mtg.engine.playing.zone

import fr.tvbarthel.mtg.engine.Zone

/**
 * Exile is a game zone outside the field of play. It is also a keyword action, meaning "putting into the exile zone".
 *
 * Exile was known as "removed from the game" before it was renamed in the wake of the Magic 2010 rules update.
 * Unlike other zones, it is considerably harder to access cards from this zone. Only a few cards, such as Pull from
 * Eternity or Riftsweeper, can touch them without having the cards exiled themselves. Mark Rosewater tries to limit
 * such cards. The exile zone is used as a holding place for other cards, as in the case with the mechanics Imprint and
 * Suspend. However, it is also sometimes used similarly to the graveyard as a zone for cards that were eliminated by
 * the opponent, for example a creature being exiled by Swords to Plowshares. Exiling primarily occurs among white,
 * black, and colorless cards. Due to the multiple purposes of the exile zone, there is no unified flavor of exile,
 * though the flavor of cards exiled from the battlefield often is a flavor of disappearance or transformation.
 *
 * https://mtg.gamepedia.com/Exile
 */
class Exile : Zone()