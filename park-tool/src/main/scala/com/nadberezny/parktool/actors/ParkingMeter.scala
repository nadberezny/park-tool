package com.nadberezny.parktool.actors

import akka.actor.{ Actor, Props }
import com.nadberezny.parktool.routes.Response
import play.api.libs.json.Json

object ParkingMeter {
  type VehicleIdToDate = Map[String, String]

  def props(id: Int) = Props(new ParkingMeter(id))
  abstract class Message

  object Start {
    implicit val startMsgFormat = Json.format[Start]
  }

  case class Start(vehicleId: String, date: String)
  case class Stop(vehicleId: String, date: String)
}

class ParkingMeter(id: Int) extends Actor {
  import ParkingMeter._
  import context.become

  override def receive: Receive = active(Map.empty)

  def active(state: VehicleIdToDate): Receive = {
    case msg @ Start(vehicleId, date) =>
      state.get(vehicleId) match {
        case Some(startedAt) =>
          sender ! Left(Response(s"$vehicleId already started for PM#$id at $startedAt."))
          become(active(state))
        case None =>
          context.system.eventStream publish msg
          sender ! Right(Response(s"Started PM#$id for $vehicleId at $date."))
          become(active(state + (vehicleId -> date)))
      }

    case msg @ Stop(vehicleId, date) =>
      state.get(vehicleId) match {
        case Some(_) =>
          context.system.eventStream publish msg
          sender ! Right(Response(s"Stopped PM#$id for $vehicleId at $date."))
          become(active(state - vehicleId))
        case None =>
          sender ! Left(Response(s"PM#$id not running for $vehicleId."))
          become(active(state))
      }
  }
}