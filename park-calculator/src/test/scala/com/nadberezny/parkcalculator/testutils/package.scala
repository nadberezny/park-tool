package com.nadberezny.parkcalculator

import com.nadberezny.parkcalculator.db.repositories.ParkingRepository
import com.nadberezny.parkcalculator.db.tables.ParkingRegistryRow
import com.github.nscala_time.time.Imports._

import org.joda.time.DateTime

package object testutils {
  class TestParkingRepo(duration: Long) extends ParkingRepository {
    override def findStarted(vehicleId: String) = {
      val now = DateTime.now

      vehicleId match {
        case "RST" => Some(ParkingRegistryRow(1, 1, vehicleId, now, Some(now + 30.minutes)))
        case _ => None
      }
    }
  }
}
