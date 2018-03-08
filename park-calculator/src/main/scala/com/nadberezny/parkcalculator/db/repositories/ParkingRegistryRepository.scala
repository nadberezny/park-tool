package com.nadberezny.parkcalculator.db.repositories
import com.nadberezny.parkcalculator.ParkCalculatorApp.{ database, timeout }
import com.nadberezny.parkcalculator.db.queries.parkingRegisters
import com.nadberezny.parkcalculator.db.tables.ParkingRegistryRow

import scala.concurrent.Await
import slick.jdbc.PostgresProfile.api._
import com.github.tototoshi.slick.PostgresJodaSupport._

class ParkingRegistryRepository extends ParkingRepository {
  override def findStarted(vehicleId: String): Option[ParkingRegistryRow] = {
    val query =
      for (
        p <- parkingRegisters if p.vehicleId === vehicleId && p.stop.isEmpty
      ) yield p

    val futureOptionResult = database.run(query.result.headOption)
    Await.result(futureOptionResult, timeout.duration)
  }
}
