package fr.tvbarthel.mtg.engine

/**
 * In Magic: The Gathering, a card is the standard component of the game. The word card usually refers to a Magic card
 * with a Magic card front and a Magic card back, or to double-faced card.
 *
 * https://mtg.gamepedia.com/Card
 */
open class Card(
    val id: Int,
    val types: List<Type>,
    val manaCost: Property<Int> = Property(0),
    val power: Property<Int> = Property(0),
    val toughness: Property<Int> = Property(0)
) {

    /**
     * Card type is a characteristic found on every Magic: the Gathering card. It appears in the type line,
     * between any supertypes and subtypes that card might have.
     *
     * Other objects, such as tokens and some non-traditional Magic card, also have card types.
     *
     * Card type dictates many of the general rules regarding when and how a card may be played.
     *
     * https://mtg.gamepedia.com/Card_type
     */
    open class Type


    /**
     * Hold the value of any properties that could be attached to a card.
     */
    class Property<T>(
        private val initialValue: T,
        private val modifiers: MutableList<Modifier<T>> = mutableListOf()
    ) {
        /**
         * Current value of the property.
         *
         * Include the modification applied by every modifier currently set to the property.
         */
        fun value(): T = modifiers.fold(initialValue) { v, m -> m.modified(v) }

        /**
         * Add a given property modifier.
         */
        fun addModifier(modifier: Modifier<T>) = modifiers.add(modifier)

        /**
         * Remove a given property modifier.
         */
        fun removeModifier(modifier: Modifier<T>): Boolean = modifiers.remove(modifier)
    }

    /**
     * Allow to modify the value of a property.
     */
    abstract class Modifier<T> {
        /**
         * Must modify the current value of the property and return a new one.
         */
        abstract fun modified(value: T): T
    }
}