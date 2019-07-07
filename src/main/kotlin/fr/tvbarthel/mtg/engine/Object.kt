package fr.tvbarthel.mtg.engine

/**
 * An object is any concrete "thing" in a game of Magic: an ability on the stack, a card, a copy of a card, a token,
 * a spell, a permanent, or an emblem. No card currently references objects as such, only particular types of objects.
 *
 * An object can have many characteristics: Name, mana cost, color, card type, subtype, supertype, rules text, abilities,
 * power and toughness, loyalty, hand modifier and life modifier. Any other information regarding the object is not
 * considered a characteristic, but rather an attribute, a property or a quality.
 *
 * https://mtg.gamepedia.com/Object
 */
open class Object