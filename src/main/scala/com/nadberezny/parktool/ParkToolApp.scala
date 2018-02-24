package com.nadberezny.parktool

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.nadberezny.parktool.actors.ParkingMeterSupervisor
import com.nadberezny.parktool.routes.ParkingMeterRoutes
import com.typesafe.config.ConfigFactory

import akka.util.Timeout
import scala.concurrent.duration._

object ParkToolApp extends App with ParkingMeterRoutes {
  val conf = ConfigFactory.load

  override implicit val system = ActorSystem(conf.getString("actorSystem"))
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()
  override implicit val timeout = Timeout(conf.getInt("timeoutMillis") millis)

  override val parkingMeterSupervisor = system.actorOf(ParkingMeterSupervisor.props)

  Http().bindAndHandle(
    routes, conf.getString("http.host"), conf.getInt("http.port")
  )
}
