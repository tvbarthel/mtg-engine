package fr.tvbarthel.mtg.experimentation.actorgameloop

import fr.tvbarthel.mtg.experimentation.*
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.Actor
import fr.tvbarthel.mtg.experimentation.actorgameloop.actor.PlayLandActor
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event
import fr.tvbarthel.mtg.experimentation.actorgameloop.event.ResolveActionEvent
import java.util.*

class ActorGameLoop : GameLoop() {

    private val actors = mutableListOf<Actor>()

    init {
        val playLandActor = PlayLandActor()
        attachActor(playLandActor)
    }

    override fun playStep(turnContext: TurnContext, step: Step) {
        val turn = turnContext.turnIndex
        val activePlayer = turnContext.activePlayer
        val opponentPlayer = turnContext.opponentPlayer
        val stepContext = StepContext(turnContext, step)

        val actionStack = Stack<ActionContext>()
        while (true) {
            val player1Action = activePlayer.getAction(turn, step)
            val player1Passes = player1Action == null || player1Action is PassAction
            if (!player1Passes) {
                println("\t Player $activePlayer adds to stack -> $player1Action")
                val actionContext = ActionContext(
                    player1Action!!,
                    activePlayer,
                    opponentPlayer
                )
                actionStack.push(actionContext)
            } else {
                println("\t Player $activePlayer passes.")
            }

            val player2Action = opponentPlayer.getAction(turn, step)
            val player2Passes = player2Action == null || player2Action is PassAction
            if (!player2Passes) {
                println("\t Player $opponentPlayer adds to stack -> $player2Action")
                val actionContext = ActionContext(
                    player2Action!!,
                    opponentPlayer,
                    activePlayer
                )
                actionStack.push(actionContext)
            } else {
                println("\t Player $opponentPlayer passes.")
            }

            if ((player1Passes || player2Passes) && actionStack.isNotEmpty()) {
                while (actionStack.isNotEmpty()) {
                    val actionContext = actionStack.pop()
                    val event = ResolveActionEvent(
                        stepContext,
                        actionContext
                    )
                    sendEvent(event)
                }
            }

            if (player1Passes && player2Passes) {
                break
            }
        }
    }

    private fun sendEvent(event: Event) {
        actors.filter { actor -> actor.isAlive() }
            .forEach { actor -> actor.onEventReceived(event) }

        actors.removeIf { actor -> !actor.isAlive() }
    }

    private fun attachActor(actor: Actor) {
        actors.add(actor)
    }

    class StepContext(val turnContext: TurnContext, val step: Step)

    class ActionContext(val action: Action, val activePlayer: Player, val opponent: Player)

}