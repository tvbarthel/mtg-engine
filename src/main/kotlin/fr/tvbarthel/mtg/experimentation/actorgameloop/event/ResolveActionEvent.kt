package fr.tvbarthel.mtg.experimentation.actorgameloop.event

import fr.tvbarthel.mtg.experimentation.StepContext
import fr.tvbarthel.mtg.experimentation.actorgameloop.ActorGameLoop

class ResolveActionEvent(
    val stepContext: StepContext,
    val actionContext: ActorGameLoop.ActionContext
) : Event