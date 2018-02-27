package com.nadberezny.parktool.actors

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }
import com.nadberezny.parktool.actors.ParkingMeter.{ Start, Stop }
import com.nadberezny.parktool.routes.Response
import org.joda.time.DateTime
import org.scalatest.{ BeforeAndAfterAll, FunSuiteLike }

class ParkingMeterTest(_system: ActorSystem) extends TestKit(_system)
    with FunSuiteLike
    with ImplicitSender
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("ParkToolTest"))
  override def afterAll() = shutdown(system)
  val vehicleId = "VH"
  val parkingMeterId = 1
  val parkingMeter = system.actorOf(ParkingMeter.props(parkingMeterId))
  val startDate = DateTime.now.toString
  val stopDate = DateTime.now.toString

  test("responds with success to start request") {
    parkingMeter ! Start(vehicleId, startDate)
    expectMsg(Right(Response(s"Started PM#1 for VH at $startDate.")))
  }

  test("responds with failure to start request for already started vehicle") {
    parkingMeter ! Start(vehicleId, startDate)
    expectMsg(Left(Response(s"VH already started for PM#1 at $startDate.")))
  }

  test("responds with success to stop request for started vehicle") {
    parkingMeter ! Stop(vehicleId, stopDate)
    expectMsg(Right(Response(s"Stopped PM#1 for VH at $stopDate.")))
  }

  test("responds with failure to stop request for already stopped vehicle") {
    parkingMeter ! Stop(vehicleId, stopDate)
    expectMsg(Left(Response(s"PM#1 not running for VH.")))
  }
}
