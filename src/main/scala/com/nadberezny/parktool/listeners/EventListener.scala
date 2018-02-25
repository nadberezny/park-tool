package com.nadberezny.parktool.listeners

import akka.actor.{ Actor, ActorLogging, Props }
import com.nadberezny.parktool.actors._

object EventListener {
  def props = Props[EventListener]
}

class EventListener extends Actor with ActorLogging {

  override def receive: Receive = {
    case req: ParkingMeter.Start =>
      log.info(s"Received Start event of vehicle: ${req.vehicleId}")

    case req: ParkingMeter.Stop =>
      log.info(s"Received Stop event of vehicle: ${req.vehicleId}")
  }
}
