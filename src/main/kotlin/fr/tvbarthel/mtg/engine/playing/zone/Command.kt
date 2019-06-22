package fr.tvbarthel.mtg.engine.playing.zone

import fr.tvbarthel.mtg.engine.Zone

/**
 * The Command Zone is a zone used for special objects which affect the game like permanents do, but are not actually
 * permanents. This includes emblems, plane cards, scheme cards, and the Commander in Elder Dragon Highlander.
 * Commander 2013, Commander 2017, and Conspiracy contain cards that interact with the Command Zone.
 *
 * https://mtg.gamepedia.com/Command
 */
class Command : Zone()