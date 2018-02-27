package com.nadberezny.parktool.actors

import akka.actor.{ Actor, ActorRef, Props }
import com.nadberezny.parktool.listeners.EventListener

object ParkingMeterSupervisor {
  type IdToActorRef = Map[Int, ActorRef]

  def props = Props[ParkingMeterSupervisor]

  case class Start(parkingMeterId: Int, vehicleId: String, date: String)
  case class Stop(parkingMeterId: Int, vehicleId: String, date: String)
}

class ParkingMeterSupervisor extends Actor {
  import ParkingMeterSupervisor._
  import context.become

  override def preStart(): Unit = {
    super.preStart()
    subscribeListener()
  }

  override def receive: Receive = active(Map.empty)

  def active(state: IdToActorRef): Receive = {
    case Start(parkingMeterId, vehicleId, date) =>
      val actorRef = state.getOrElse(parkingMeterId, spawn(parkingMeterId))
      actorRef forward ParkingMeter.Start(vehicleId, date)
      become(active(state + (parkingMeterId -> actorRef)))

    case Stop(parkingMeterId, vehicleId, date) =>
      val actorRef = state.getOrElse(parkingMeterId, spawn(parkingMeterId))
      actorRef forward ParkingMeter.Stop(vehicleId, date)
      become(active(state + (parkingMeterId -> actorRef)))
  }

  def spawn(id: Int) = context.actorOf(ParkingMeter.props(id), id.toString)

  def subscribeListener() = {
    val stream = context.system.eventStream
    val eventListener = context.actorOf(EventListener.props)
    stream subscribe (eventListener, classOf[ParkingMeter.Event])
  }
}
