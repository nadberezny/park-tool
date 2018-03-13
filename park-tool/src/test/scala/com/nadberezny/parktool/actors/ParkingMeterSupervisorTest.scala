package com.nadberezny.parktool.actors

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestActorRef, TestKit }
import com.nadberezny.parktool.actors.ParkingMeterSupervisor.{ Start, Stop }
import com.nadberezny.parktool.routes.Response
import org.joda.time.DateTime
import org.scalatest.{ BeforeAndAfterAll, FunSuiteLike }

class ParkingMeterSupervisorTest(_system: ActorSystem) extends TestKit(_system) with FunSuiteLike with ImplicitSender with BeforeAndAfterAll {
  def this() = this(ActorSystem("ParkToolTest"))
  override def afterAll() = shutdown(system)
  val vehicleId = "A"
  val parkingMeterSupervisor = TestActorRef[ParkingMeterSupervisor]

  test("forwards start request") {
    val startDate = DateTime.now.toString
    parkingMeterSupervisor ! Start(1, vehicleId, startDate)
    expectMsg(Right(Response(s"Started PM#1 for A at $startDate.")))

  }

  test("forwards stop request") {
    val stopDate = DateTime.now.toString
    parkingMeterSupervisor ! Stop(1, vehicleId, stopDate)
    expectMsg(Right(Response(s"Stopped PM#1 for A at $stopDate.")))
  }
}
