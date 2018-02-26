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
  val parkingMeter = system.actorOf(ParkingMeter.props(1))
  val vehicleId = "A"

  test("responds to start request") {

    parkingMeter ! Start(vehicleId, DateTime.now)
    expectMsg(Response("Started"))
  }

  test("responds to stop request") {
    parkingMeter ! Stop(vehicleId, DateTime.now)
    expectMsg(Response("Stopped"))
  }
}
