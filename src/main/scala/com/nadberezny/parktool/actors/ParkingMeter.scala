package com.nadberezny.parktool.actors

import akka.actor.{ Actor, Props }
import com.github.nscala_time.time.Imports._
import com.nadberezny.parktool.routes.{ Response }

object ParkingMeter {
  def props(id: Int) = Props(new ParkingMeter(id))

  abstract class Message
  case class Start(vehicleId: String, date: DateTime) extends Message
  case class Stop(vehicleId: String, date: DateTime) extends Message
}

class ParkingMeter(id: Int) extends Actor {
  import ParkingMeter._

  override def receive = {
    case msg: Start =>
      context.system.eventStream publish msg
      sender ! Response("Started")

    case msg: Stop =>
      context.system.eventStream publish msg
      sender ! Response("Stopped")
  }
}