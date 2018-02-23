package com.nadberezny.parktool.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val responseJsonFormat = jsonFormat1(RequestHandler.Response)
  implicit val startRequestJsonFormat = jsonFormat2(RequestHandler.StartParkingRequest)
  implicit val stopRequestJsonFormat = jsonFormat2(RequestHandler.StopParkingRequest)
}
