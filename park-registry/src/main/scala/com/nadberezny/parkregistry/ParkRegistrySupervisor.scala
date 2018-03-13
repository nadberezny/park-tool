package com.nadberezny.parkregistry

import akka.actor.{ Actor, Props }
import com.nadberezny.parkregistry.actors.{ EventConsumer, PersistEventService }

object ParkRegistrySupervisor {
  def props = Props[ParkRegistrySupervisor]
}

class ParkRegistrySupervisor extends Actor {
  override def preStart() = {
    super.preStart()
    spawnEventConsumer()
  }

  override def receive = Actor.emptyBehavior

  def spawnEventConsumer() = {
    val persistEventService = context.actorOf(PersistEventService.props)
    context.actorOf(EventConsumer.props(persistEventService), "EventConsumer")
  }
}
