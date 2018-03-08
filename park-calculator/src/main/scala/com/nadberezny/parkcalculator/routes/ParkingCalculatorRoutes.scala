package com.nadberezny.parkcalculator.routes

import akka.actor.{ ActorRef, ActorSystem, Inbox }
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.nadberezny.parkcalculator.actors.FeeCalculatorSupervisor
import com.nadberezny.parkcalculator.db.repositories.ParkingRepository
import com.nadberezny.parkcalculator.services.feecalculator.EitherCalculable

import scala.concurrent.{ ExecutionContextExecutor, Future }

trait ParkingCalculatorRoutes extends JsonSupport {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit val timeout: Timeout
  implicit val parkingRepository: ParkingRepository
  implicit val feeCalculatorSupervisor: ActorRef

  def getFee(vehicleId: String, currency: String): Future[Either[String, ParkingFeeResponse]] = {
    val getFeeMsg = FeeCalculatorSupervisor.GetFeeRequest(vehicleId, currency)

    (feeCalculatorSupervisor ? getFeeMsg)
      .mapTo[EitherCalculable]
      .map(_ match {
        case Right(c) =>
          Right(ParkingFeeResponse(c.vehicleId, c.duration, c.fee, c.currency))
        case Left(err) =>
          Left(err)
      })
  }

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
            getFee(vehicleId, currency).map[ToResponseMarshallable] {
              case Right(res) => res
              case Left(err) => BadRequest -> err
            }
          }
        }
      }
    }
  )
}
