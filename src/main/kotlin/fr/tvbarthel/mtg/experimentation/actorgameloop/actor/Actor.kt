package fr.tvbarthel.mtg.experimentation.actorgameloop.actor

import fr.tvbarthel.mtg.experimentation.actorgameloop.event.Event

interface Actor {

    fun onEventReceived(event: Event)

    fun isAlive(): Boolean

}