package com.nadberezny.parktool.serializers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.nadberezny.parktool.routes.{ Response, StartParkingRequest, StopParkingRequest }

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val responseJsonFormat = jsonFormat1(Response)
  implicit val startRequestJsonFormat = jsonFormat2(StartParkingRequest)
  implicit val stopRequestJsonFormat = jsonFormat2(StopParkingRequest)
}
