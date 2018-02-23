package com.nadberezny.parktool.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object ParkingMeterRoutes extends JsonSupport {
  import RequestHandler._

  lazy val routes: Route = pathPrefix("parking-meters") {
    concat(
      path("ping") {
        get {
          complete(StatusCodes.OK)
        }
      },
      path("start") {
        post {
          entity(as[StartParkingRequest]) { request =>
            onSuccess(start(request)) { response =>
              complete(response)
            }
          }
        }
      },
      path("stop") {
        post {
          entity(as[StopParkingRequest]) { request =>
            onSuccess(stop(request)) { response =>
              complete(response)
            }
          }
        }
      }
    )
  }
}
