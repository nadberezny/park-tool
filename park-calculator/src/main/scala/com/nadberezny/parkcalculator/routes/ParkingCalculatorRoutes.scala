package com.nadberezny.parkcalculator.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.util.Timeout
import com.nadberezny.parkcalculator.db.repositories.ParkingRepository
import com.nadberezny.parkcalculator.services.FeeCalculator

import scala.concurrent.ExecutionContextExecutor

trait ParkingCalculatorRoutes extends JsonSupport {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit val timeout: Timeout
  implicit val parkingRepository: ParkingRepository

  lazy val routes = concat(
    pathPrefix("admin") {
      (path("parking-fees-report") & get & parameters('from, 'to)) { (from, to) =>
        complete(StatusCodes.OK, (from, to).toString)
      }
    },
    pathPrefix("parking-fees") {
      path(Segment) { vehicleId =>
        (get & parameter('currency)) { currency =>
          complete {
            new FeeCalculator().calculate(vehicleId, currency) match {
              case Right(calculable) =>
                (
                  StatusCodes.OK,
                  ParkingFeeResponse(calculable.vehicleId, calculable.duration, calculable.fee, currency)
                )
              case Left(errMsg) =>
                (StatusCodes.BadRequest, errMsg)
            }
          }
        }
      }
    }
  )
}
