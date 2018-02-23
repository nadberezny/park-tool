package com.nadberezny.parktool.routes

import akka.pattern.ask
import akka.util.Timeout
import com.nadberezny.parktool.ParkToolApp
import com.nadberezny.parktool.actors.{ ParkingMeter, ParkingMeterSupervisor }
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.duration._

object RequestHandler {
  import ParkToolApp.parkingMeterSupervisor

  case class Response(message: String)

  case class StartParkingRequest(parkingMeterId: Int, vehicleId: String)
  case class StopParkingRequest(parkingMeterId: Int, vehicleId: String)

  implicit lazy val timeout = Timeout(5.seconds)

  def start(req: StartParkingRequest): Future[Response] = {
    val startReq = ParkingMeterSupervisor.Start(req.parkingMeterId, req.vehicleId, DateTime.now)
    (parkingMeterSupervisor ? startReq).mapTo[Response]
  }

  def stop(req: StopParkingRequest): Future[Response] = {
    val stopReq = ParkingMeterSupervisor.Stop(req.parkingMeterId, req.vehicleId, DateTime.now)
    (parkingMeterSupervisor ? stopReq).mapTo[Response]
  }
}
