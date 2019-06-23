package fr.tvbarthel.mtg.engine

/**
 * Encapsulate highest "step" of the game engine.
 */
interface Stage {

    /**
     * Must proceed the given stage.
     */
    fun proceed(agents: Map<Int, Agent>, state: GameState): GameState

}