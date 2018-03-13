package com.nadberezny.parkregistry.actors

import akka.actor.{ Actor, Props }
import com.github.tototoshi.slick.PostgresJodaSupport._
import com.nadberezny.parkregistry.actors.EventConsumer.Event
import com.nadberezny.parkregistry.db.queries._
import com.nadberezny.parkregistry.db.tables.ParkingRegistryRow
import org.joda.time.DateTime
import slick.jdbc.PostgresProfile.api._

object PersistEventService {
  def props = Props[PersistEventService]
}

class PersistEventService extends Actor {
  import com.nadberezny.parkregistry.ParkRegistryApp.database

  override def receive: Receive = {
    case event @ Event(Event.Start, _, _, _) =>
      createParkingRegistry(event)

    case event @ Event(Event.Stop, _, _, _) =>
      updateParkingRegistry(event)
  }

  def createParkingRegistry(event: Event) = {
    val registry = ParkingRegistryRow(
      id = 0, event.parkingMeterId, event.vehicleId, DateTime.parse(event.date), None
    )
    database run DBIO.seq(parkingRegisters += registry)
  }

  def updateParkingRegistry(event: Event) = {
    val query =
      for (
        r <- parkingRegisters if r.parkingMeterId === event.parkingMeterId &&
          r.vehicleId === event.vehicleId &&
          r.endDate.isEmpty
      ) yield r.endDate

    val endDate = Some(DateTime.parse(event.date))

    database run query.update(endDate)
  }
}
