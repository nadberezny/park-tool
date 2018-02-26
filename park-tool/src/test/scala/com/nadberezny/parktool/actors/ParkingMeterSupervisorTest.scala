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

  test("forwards start request") {
    val parkingMeterSupervisor = TestActorRef[ParkingMeterSupervisor]
    parkingMeterSupervisor ! Start(1, vehicleId, DateTime.now)
    expectMsg(Response("Started"))
  }

  test("forwards stop request") {
    val parkingMeterSupervisor = TestActorRef[ParkingMeterSupervisor]
    parkingMeterSupervisor ! Stop(1, vehicleId, DateTime.now)
    expectMsg(Response("Stopped"))
  }
}
