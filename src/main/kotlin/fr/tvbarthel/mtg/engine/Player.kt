package fr.tvbarthel.mtg.engine

import fr.tvbarthel.mtg.engine.playing.zone.Hand
import fr.tvbarthel.mtg.engine.playing.zone.Library

/**
 * A player is one of the people in the game.
 *
 * https://mtg.gamepedia.com/Player
 */
data class Player(
    val id: Int,
    val library: Library = Library(),
    val hand: Hand = Hand(),
    var health: Int = 0
)