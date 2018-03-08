package com.nadberezny.parkcalculator.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val parkingFeeResponseJsonFormat = jsonFormat4(ParkingFeeResponse)
}
