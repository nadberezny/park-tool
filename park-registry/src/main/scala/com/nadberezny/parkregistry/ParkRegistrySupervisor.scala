package com.nadberezny.parkregistry

import akka.actor.{ Actor, Props }

object ParkRegistrySupervisor {
  def props = Props[ParkRegistrySupervisor]
}

class ParkRegistrySupervisor extends Actor {
  val eventConsumer = context.actorOf(EventConsumer.props, "EventConsumer")

  override def receive = Actor.emptyBehavior
}
