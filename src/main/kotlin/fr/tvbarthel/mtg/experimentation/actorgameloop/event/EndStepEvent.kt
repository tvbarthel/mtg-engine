package fr.tvbarthel.mtg.experimentation.actorgameloop.event

import fr.tvbarthel.mtg.experimentation.StepContext

class EndStepEvent(
    val stepContext: StepContext
) : Event