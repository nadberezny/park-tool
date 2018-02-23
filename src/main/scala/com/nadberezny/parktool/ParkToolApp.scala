package com.nadberezny.parktool

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.nadberezny.parktool.actors.ParkingMeterSupervisor
import com.nadberezny.parktool.routes.ParkingMeterRoutes
import com.typesafe.config.ConfigFactory

object ParkToolApp extends App {
  implicit val system = ActorSystem("ParkToolSystem")
  implicit val materializer = ActorMaterializer()

  lazy val parkingMeterSupervisor = system.actorOf(ParkingMeterSupervisor.props)

  val conf = ConfigFactory.load

  Http().bindAndHandle(
    ParkingMeterRoutes.routes, conf.getString("app.host"), conf.getInt("app.port")
  )
}
