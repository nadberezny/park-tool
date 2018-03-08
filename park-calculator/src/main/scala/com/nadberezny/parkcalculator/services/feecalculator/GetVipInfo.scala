package com.nadberezny.parkcalculator.services.feecalculator

object GetVipInfo {
  def apply(calculable: Calculable): EitherCalculable =
    new GetVipInfo(calculable).process
}

class GetVipInfo(calculable: Calculable) {

  private[GetVipInfo] def process: EitherCalculable = {
    vehicleOption match {
      case Some(vehicle) =>
        Right(calculable.copy(isVip = vehicle.isVip))

      case None =>
        Left("Invalid Vehicle Id.")
    }
  }

  private lazy val vehicleOption =
    VehicleRepository.find(calculable.vehicleId)
}
