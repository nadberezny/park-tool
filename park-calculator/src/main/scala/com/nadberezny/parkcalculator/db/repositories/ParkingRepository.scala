package com.nadberezny.parkcalculator.db.repositories

import com.nadberezny.parkcalculator.db.tables.ParkingRegistryRow

trait ParkingRepository {
  def findStarted(vehicleId: String): Option[ParkingRegistryRow]
}
