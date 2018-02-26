package com.nadberezny.parktool.actors

import akka.actor.{ Actor, Props }
import com.nadberezny.parktool.routes.Response
import play.api.libs.json.Json

object ParkingMeter {
  def props(id: Int) = Props(new ParkingMeter(id))
  abstract class Message

  object Start {
    implicit val startMsgFormat = Json.format[Start]
  }

  case class Start(vehicleId: String, date: String) extends Message
  case class Stop(vehicleId: String, date: String) extends Message
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