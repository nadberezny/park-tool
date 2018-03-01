package com.nadberezny.parkregistry

import akka.actor.{ Actor, Props }

object ParkRegistrySupervisor {
  def props = Props[ParkRegistrySupervisor]
}

class ParkRegistrySupervisor extends Actor {
  override def preStart() = {
    super.preStart()
    spawnEventConsumer()
  }

  override def receive = Actor.emptyBehavior

  def spawnEventConsumer() =
    context.actorOf(EventConsumer.props, "EventConsumer")
}
