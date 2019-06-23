package fr.tvbarthel.mtg.engine.playing.zone

import fr.tvbarthel.mtg.engine.Zone

/**
 * The stack is the game zone where spells and abilities are put when they are played and where they wait to resolve.
 *
 * Spells and abilities are put on top of the stack as the first step in being played, and are removed from it as the
 * last step of resolving.
 *
 * Any spell or ability that uses the stack can be 'responded to' by all players, meaning players have a chance to play
 * spells and abilities with it still on the stack. Since the stack resolves in order from top to bottom, those spells
 * and abilities will resolve before the spell they were played "in response" to. Spells and abilities on the stack
 * resolve one at a time, with a chance for each player to play spells and abilities in between each resolution. Actions
 * that do not use the stack, such as paying costs, playing mana abilities, or turning a face-down creature with morph
 * face-up, cannot be responded to.
 *
 * https://mtg.gamepedia.com/Stack
 */
class Stack : Zone()