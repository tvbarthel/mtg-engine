package fr.tvbarthel.mtg.engine.agent

import fr.tvbarthel.mtg.engine.Action
import fr.tvbarthel.mtg.engine.Agent
import fr.tvbarthel.mtg.engine.GameState
import fr.tvbarthel.mtg.engine.Logger

/**
 * Agent asking to choose an action through the console
 */
class ConsoleAgent : Agent() {
    override fun chose(state: GameState, actions: List<Action>): Action {

        var choosen: Int? = null
        while (choosen == null) {
            println()
            actions.forEachIndexed { i, a -> println("$i -> $a") }
            choosen = readLine()?.toInt()
            if (choosen == null || choosen !in (0 until actions.size)) {
                Logger.e("\n\nConsole Agent: Wrong action index $choosen \n\n")
                choosen = null
            }
        }
        println()
        return actions[choosen]
    }

}