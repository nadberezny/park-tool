package com.nadberezny.parkcalculator.services

import cats.data.Kleisli
import cats.implicits._
import com.nadberezny.parkcalculator.db.repositories.ParkingRepository
import com.nadberezny.parkcalculator.services.feecalculator._

class FeeCalculator(implicit val parkingRepository: ParkingRepository) {
  def calculate(vehicleId: String, currency: String): EitherCalculable = {
    calculation.run(
      Calculable(vehicleId = vehicleId, currency = currency)
    )
  }

  private lazy val calculation = {
    Kleisli(getDuration) andThen
      Kleisli(getVipInfo) andThen
      Kleisli(getPrice) andThen
      Kleisli(getFactors) andThen
      Kleisli(getFee)
  }

  private lazy val getDuration: Calculable => EitherCalculable = GetDuration(parkingRepository, _)
  private lazy val getVipInfo: Calculable => EitherCalculable = GetVipInfo(_)
  private lazy val getPrice: Calculable => EitherCalculable = GetPrice(_)
  private lazy val getFactors: Calculable => EitherCalculable = GetFactors(_)
  private lazy val getFee: Calculable => EitherCalculable = GetFee(_)
}
