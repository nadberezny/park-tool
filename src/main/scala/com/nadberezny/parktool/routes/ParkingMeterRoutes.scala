package com.nadberezny.parktool.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object ParkingMeterRoutes {
  lazy val routes: Route = pathPrefix("parking-meters") {
    concat(
      path("ping") {
        get {
          complete(StatusCodes.OK)
        }
      }
    )
  }
}
