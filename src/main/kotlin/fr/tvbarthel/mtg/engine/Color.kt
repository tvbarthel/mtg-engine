package fr.tvbarthel.mtg.engine

/**
 * Color is a basic property of cards in Magic: The Gathering, forming the core of the game's mana system and overall strategy.
 *
 * https://mtg.gamepedia.com/Color
 */
enum class Color {

    /**
     * White is one of the five colors of mana in Magic. It is drawn from the plains and embodies the principles of
     * morality and order. The mana symbol for white is represented by a sun. On the color pie, it is allied with
     * green and blue, and is enemies with black and red. White seeks peace through structure.
     *
     * https://mtg.gamepedia.com/White
     */
    WHITE,

    /**
     * Blue is one of the five colors of mana in Magic. It is drawn from islands and embodies the concepts of logic and
     * technology and seeks perfection through knowledge.The mana symbol for blue is a drop of water. On the color
     * pie, it is allied with white and black and is the enemy of red and green. For a time, blue was the dominant
     * color and R&D was forced to change its approach to the color in order to bring it in line with the strength of
     * the other colors.
     *
     * https://mtg.gamepedia.com/Blue
     */
    BLUE,

    /**
     * Black is one of the five colors of mana in Magic. It is drawn from the power of swamps and embodies the
     * principles of parasitism and amorality (though not necessarily immorality). The mana symbol for black is
     * represented by a skull. On the color pie, it is the ally of blue and red, and the enemy of white and green.
     * Black seeks power through ruthlessness or opportunity.
     *
     * https://mtg.gamepedia.com/Black
     */
    BLACK,

    /**
     * Red is one of the five colors of mana in Magic. It is drawn from the mountains and embodies the principles of
     * impulse and chaos. The mana symbol for red is represented by a fireball. On the color pie, it is allied with
     * black and green, and is enemies with white and blue. Red seeks freedom through action.
     *
     * https://mtg.gamepedia.com/Red
     */
    RED,

    /**
     * Green is one of the five colors of mana in Magic. It is drawn from the power of forests and embodies the
     * principles of instinct and interdependence. The mana symbol for green is represented by a tree. On the Color
     * Pie, it is the ally of white and red, and the enemy of blue and black. Green seeks acceptance through growth.
     *
     * https://mtg.gamepedia.com/Green
     */
    GREEN,
}