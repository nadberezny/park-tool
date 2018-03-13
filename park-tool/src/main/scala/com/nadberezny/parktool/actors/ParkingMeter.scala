package com.nadberezny.parktool.actors

import akka.actor.{ Actor, Props }
import com.nadberezny.parktool.routes.Response
import play.api.libs.json.Json

object ParkingMeter {
  type VehicleIdToDate = Map[String, String]

  def props(id: Int) = Props(new ParkingMeter(id))

  case class Start(vehicleId: String, date: String)
  case class Stop(vehicleId: String, date: String)

  object Event { implicit val eventMsgFormat = Json.writes[Event] }
  case class Event(eventType: String, parkingMeterId: Int, vehicleId: String, date: String)
}

class ParkingMeter(id: Int) extends Actor {
  import ParkingMeter._
  import context.become

  val stream = context.system.eventStream

  override def receive: Receive = active(Map.empty)

  def active(state: VehicleIdToDate): Receive = {
    case Start(vehicleId, date) =>
      state.get(vehicleId) match {
        case Some(startedAt) =>
          sender ! Left(Response(s"$vehicleId already started for PM#$id at $startedAt."))
          become(active(state))

        case None =>
          stream publish Event("start", id, vehicleId, date)
          sender ! Right(Response(s"Started PM#$id for $vehicleId at $date."))
          become(active(state + (vehicleId -> date)))
      }

    case Stop(vehicleId, date) =>
      state.get(vehicleId) match {
        case Some(_) =>
          stream publish Event("stop", id, vehicleId, date)
          sender ! Right(Response(s"Stopped PM#$id for $vehicleId at $date."))
          become(active(state - vehicleId))

        case None =>
          sender ! Left(Response(s"PM#$id not running for $vehicleId."))
          become(active(state))
      }
  }
}