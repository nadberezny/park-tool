package com.nadberezny.parktool.routes

import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.util.Timeout
import com.nadberezny.parktool.actors.ParkingMeterSupervisor
import scala.concurrent.duration._

class ParkingMeterRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with ParkingMeterRoutes {
  override implicit val timeout: Timeout = Timeout(500 millis)
  override val parkingMeterSupervisor = system.actorOf(ParkingMeterSupervisor.props)

  "The App" should {
    "return OK for GET request to ping path" in {
      Get("/parking-meters/ping") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "return json response for POST request to start path" in {
      Post("/parking-meters/start", StartParkingRequest(1, "VH")) ~> routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
      }
    }

    "return bad request for start request of already started vehicle" in {
      Post("/parking-meters/start", StartParkingRequest(1, "VH")) ~> routes ~> check {
        status shouldBe BadRequest
        contentType shouldBe `application/json`
      }
    }

    "return json response for POST request to stop path" in {
      Post("/parking-meters/stop", StopParkingRequest(1, "VH")) ~> routes ~> check {
        status shouldBe OK
        contentType shouldBe `application/json`
      }
    }

    "return bad request for stop request of already stopped vehicle" in {
      Post("/parking-meters/stop", StopParkingRequest(1, "VH")) ~> routes ~> check {
        status shouldBe BadRequest
        contentType shouldBe `application/json`
      }
    }
  }
}
