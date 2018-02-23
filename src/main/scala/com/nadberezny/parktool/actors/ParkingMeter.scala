package com.nadberezny.parktool.actors

import akka.actor.{ Actor, Props }
import com.github.nscala_time.time.Imports._
import com.nadberezny.parktool.routes.RequestHandler

object ParkingMeter {
  def props(id: Int) = Props(new ParkingMeter(id))

  case class Start(vehicleId: String, date: DateTime)
  case class Stop(vehicleId: String, date: DateTime)
}

class ParkingMeter(id: Int) extends Actor {
  import ParkingMeter._

  override def receive = {
    case _: Start =>
      sender ! RequestHandler.Response("Started")

    case _: Stop =>
      sender ! RequestHandler.Response("Stopped")
  }
}