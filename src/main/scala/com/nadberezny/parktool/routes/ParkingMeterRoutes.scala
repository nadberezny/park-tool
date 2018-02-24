package com.nadberezny.parktool.routes

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.nadberezny.parktool.actors.ParkingMeterSupervisor
import org.joda.time.DateTime

import scala.concurrent.{ ExecutionContextExecutor, Future }

case class StartParkingRequest(parkingMeterId: Int, vehicleId: String)
case class StopParkingRequest(parkingMeterId: Int, vehicleId: String)
case class Response(message: String)

trait ParkingMeterRoutes extends JsonSupport {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit val timeout: Timeout

  val parkingMeterSupervisor: ActorRef

  def start(req: StartParkingRequest): Future[Either[Response, Response]] = {
    val startMsg = ParkingMeterSupervisor.Start(req.parkingMeterId, req.vehicleId, DateTime.now)
    (parkingMeterSupervisor ? startMsg).mapTo[Response].map { response =>
      response.message match {
        case "Started" => Right(response)
        case _ => Left(Response("Failure"))
      }
    }
  }

  def stop(req: StopParkingRequest): Future[Response] = {
    val stopReq = ParkingMeterSupervisor.Stop(req.parkingMeterId, req.vehicleId, DateTime.now)
    (parkingMeterSupervisor ? stopReq).mapTo[Response]
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
                case Left(res) => res
              }
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
