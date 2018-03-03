package com.nadberezny.parkregistry

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.duration._

object ParkRegistryApp extends App {
  val conf = ConfigFactory.load

  implicit val system = ActorSystem(conf.getString("actorSystem"))
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(conf.getInt("timeoutMillis") millis)

  val database = Database.forConfig("db")
  db.services.Setup(database)

  val supervisor = system.actorOf(ParkRegistrySupervisor.props)
}
