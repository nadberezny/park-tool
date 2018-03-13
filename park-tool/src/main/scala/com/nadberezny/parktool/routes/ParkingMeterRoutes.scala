package com.nadberezny.parktool.routes

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.nadberezny.parktool.actors.ParkingMeterSupervisor
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import com.nadberezny.parktool.serializers.JsonSupport

import scala.concurrent.{ ExecutionContextExecutor, Future }

abstract class ParkingRequest
case class StartParkingRequest(parkingMeterId: Int, vehicleId: String) extends ParkingRequest
case class StopParkingRequest(parkingMeterId: Int, vehicleId: String) extends ParkingRequest

case class Response(message: String)

trait ParkingMeterRoutes extends JsonSupport {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit val timeout: Timeout

  val parkingMeterSupervisor: ActorRef
  DateTime.now.second.get()

  def start(req: StartParkingRequest): Future[Either[Response, Response]] = {
    val startMsg = ParkingMeterSupervisor.Start(
      req.parkingMeterId, req.vehicleId, DateTime.now.toString
    )
    (parkingMeterSupervisor ? startMsg).mapTo[Either[Response, Response]]
  }

  def stop(req: StopParkingRequest): Future[Either[Response, Response]] = {
    val stopReq = ParkingMeterSupervisor.Stop(
      req.parkingMeterId, req.vehicleId, DateTime.now.toString
    )
    (parkingMeterSupervisor ? stopReq).mapTo[Either[Response, Response]]
  }

  lazy val routes = pathPrefix("parking-meters") {
    concat(
      path("ping") {
        get {
          complete(StatusCodes.OK)
        }
      },
      path("start") {
        post {
          entity(as[StartParkingRequest]) { request =>
            complete {
              start(request).map[ToResponseMarshallable] {
                case Right(res) => res
                case Left(res) => BadRequest -> res
              }
            }
          }
        }
      },
      path("stop") {
        post {
          entity(as[StopParkingRequest]) { request =>
            complete {
              stop(request).map[ToResponseMarshallable] {
                case Right(res) => res
                case Left(res) => BadRequest -> res
              }
            }
          }
        }
      }
    )
  }
}
