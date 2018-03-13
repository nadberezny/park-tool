package com.nadberezny.parkcalculator.services.feecalculator

import com.nadberezny.parkcalculator.db.tables.ParkingRegistryRow
import com.nadberezny.parkcalculator.db.repositories.ParkingRepository
import org.joda.time.DateTime

object GetDuration {
  def apply(parkingRepository: ParkingRepository, calculable: Calculable): EitherCalculable =
    new GetDuration(parkingRepository, calculable).process
}

class GetDuration(parkingRepository: ParkingRepository, calculable: Calculable) {

  private[GetDuration] def process: EitherCalculable = {
    startedParkingOption match {
      case Some(parkingRegistry) =>
        val duration = getDuration(parkingRegistry)
        Right(calculable.copy(duration = duration))

      case _ =>
        Left("Invalid vehicle ID.")
    }
  }

  private lazy val startedParkingOption = {
    parkingRepository.findStarted(calculable.vehicleId)
  }

  private def getDuration(parkingRegistry: ParkingRegistryRow): Long = {
    val stop: DateTime = parkingRegistry.stop match {
      case Some(dateTime) => dateTime
      case _ => DateTime.now
    }

    (stop.getMillis - parkingRegistry.start.getMillis) / 1000 / 60
  }
}
