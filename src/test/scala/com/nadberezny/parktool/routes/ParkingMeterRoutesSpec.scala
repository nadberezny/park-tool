package com.nadberezny.parktool.routes

import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model._

class ParkingMeterRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {
  "The App" should {
    "return OK for GET request to ping path" in {
      Get("/parking-meters/ping") ~> ParkingMeterRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }
}
