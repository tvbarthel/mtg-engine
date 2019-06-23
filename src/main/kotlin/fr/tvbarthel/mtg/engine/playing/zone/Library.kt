package fr.tvbarthel.mtg.engine.playing.zone

import fr.tvbarthel.mtg.engine.Zone

/**
 * The library is one of the six main zones in Magic.
 *
 * The library is the pile from which you draw cards, either the opening hand at the beginning of the game,
 * during your draw phase, or because of an effect of a spell or ability. Cards drawn are put into a player's hand,
 * from which they can be played. Some effects return cards to the top or bottom of your library, or cause cards to be
 * shuffled into your library. Other effects allow you to search your library for a card; these effects can be referred
 * to as "tutors".
 *
 * Typically, whenever a player has looked through the contents of his or her library, that player shuffles the library
 * before resuming play. This is to preserve the random order of the cards to be drawn, and allow for luck to play a
 * part in the game. In organized play, many judge policies concern preserving the randomness of the library when a
 * player illicitly obtains knowledge of the order of a player's library.
 *
 * The library also serves as an alternative win condition by being a finite resource. Once all of the cards in a
 * library are drawn or otherwise removed, a player will lose the game if an effect causes him or her to draw a card.
 * While most decks try to win by reducing life points, some aim at reducing another player's library to zero cards.
 *
 * https://mtg.gamepedia.com/Library
 */
class Library : Zone()