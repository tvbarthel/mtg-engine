package fr.tvbarthel.mtg.experimentation.actorgameloop.event

import fr.tvbarthel.mtg.experimentation.StepContext

class StartStepEvent(
    val stepContext: StepContext
) : Event