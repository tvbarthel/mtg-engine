package fr.tvbarthel.mtg.engine.playing.zone

import fr.tvbarthel.mtg.engine.Zone

/**
 * A player's hand consists of cards that have been drawn, but not played. It is one of six zones of the game.
 *
 * Flavor-wise, the hand represents the conscious mind of the player as a planeswalker and the starting hand is the
 * first seven items that occur to you when you begin a duel with another planeswalker. The starting hand may be
 * reduced when a mulligan is performed. After the optional mulligan, it is called your opening hand.
 *
 * https://mtg.gamepedia.com/Hand
 */
class Hand : Zone()