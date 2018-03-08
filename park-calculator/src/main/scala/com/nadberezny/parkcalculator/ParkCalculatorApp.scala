package com.nadberezny.parkcalculator

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.nadberezny.parkcalculator.db.repositories.ParkingRegistryRepository
import com.nadberezny.parkcalculator.routes.ParkingCalculatorRoutes
import com.typesafe.config.ConfigFactory
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.duration.MILLISECONDS

object ParkCalculatorApp extends App with ParkingCalculatorRoutes {
  val conf = ConfigFactory.load
  val database = Database.forConfig("db")

  override implicit val system = ActorSystem(conf.getString("actorSystem"))
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()
  override implicit val parkingRepository = new ParkingRegistryRepository

  override implicit val timeout = Timeout(conf.getLong("timeoutMillis"), MILLISECONDS)

  Http().bindAndHandle(
    routes, conf.getString("http.host"), conf.getInt("http.port")
  )
}