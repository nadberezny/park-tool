package com.nadberezny.parktool.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val responseJsonFormat = jsonFormat1(Response)
  implicit val startRequestJsonFormat = jsonFormat2(StartParkingRequest)
  implicit val stopRequestJsonFormat = jsonFormat2(StopParkingRequest)
}
