package com.nadberezny.parkregistry.db.tables

import org.joda.time.DateTime
import slick.jdbc.PostgresProfile.api._
import com.github.tototoshi.slick.PostgresJodaSupport._

case class ParkingRegistryRow(
  id: Int,
  parkingMeterId: Int,
  vehicleId: String,
  startDate: DateTime,
  endDate: Option[DateTime]
)

final class ParkingRegisters(tag: Tag)
    extends Table[ParkingRegistryRow](tag, "parking_registers") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def parkingMeterId = column[Int]("parking_meter_id")
  def vehicleId = column[String]("vehicleId")
  def startDate = column[DateTime]("start_date")
  def endDate = column[Option[DateTime]]("end_date")

  def * =
    (id, parkingMeterId, vehicleId, startDate, endDate).mapTo[ParkingRegistryRow]

  def uniqueIdx =
    index("registers_uq_idx", (parkingMeterId, vehicleId, endDate), unique = true)
}
