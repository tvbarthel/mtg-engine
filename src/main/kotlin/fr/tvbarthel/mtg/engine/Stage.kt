package fr.tvbarthel.mtg.engine

/**
 * Encapsulate highest "step" of the game engine.
 */
interface Stage<in I, out O> {

    /**
     * Must proceed the given stage.
     */
    fun proceed(input: I): O

}