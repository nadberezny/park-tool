package com.nadberezny.parkcalculator.db.tables

import org.joda.time.DateTime
import slick.jdbc.PostgresProfile.api._
import com.github.tototoshi.slick.PostgresJodaSupport._

case class ParkingRegistryRow(
  id: Int,
  parkingMeterId: Int,
  vehicleId: String,
  start: DateTime,
  stop: Option[DateTime]
)

final class ParkingRegisters(tag: Tag)
    extends Table[ParkingRegistryRow](tag, "parking_registers") {

  def id = column[Int]("id")
  def parkingMeterId = column[Int]("parking_meter_id")
  def vehicleId = column[String]("vehicleId")
  def start = column[DateTime]("start_date")
  def stop = column[Option[DateTime]]("end_date")

  def * =
    (id, parkingMeterId, vehicleId, start, stop).mapTo[ParkingRegistryRow]
}
