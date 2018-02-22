package com.nadberezny.parktool

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.nadberezny.parktool.routes._
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object ParkToolApp extends App {
  implicit val system = ActorSystem("ParkToolSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val conf = ConfigFactory.load
  val host = conf.getString("app.host")
  val port = conf.getInt("app.port")
  val binding = Http().bindAndHandle(ParkingMeterRoutes.routes, host, port)

  println(s"Server online at http://$host:$port/\nPress RETURN to stop.")
  StdIn.readLine()
  binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
