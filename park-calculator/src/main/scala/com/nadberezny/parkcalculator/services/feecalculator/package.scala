package com.nadberezny.parkcalculator.services

package object feecalculator {
  type EitherCalculable = Either[String, Calculable]

  case class Calculable(
    vehicleId: String,
    currency: String,
    duration: Long = 0,
    isVip: Boolean = false,
    price: Double = 0.0,
    factors: List[Factor] = List.empty,
    fee: Double = 0
  )

  case class Factor(durationInterval: Long, value: Double)

  case class FactorRow(
    rank: Int, duration: Long, regular: Double, vip: Double
  )

  object FactorRepository {
    def all: Option[List[FactorRow]] = {
      Some(List(
        FactorRow(0, 60, 1, 0),
        FactorRow(1, 60, 2, 2),
        FactorRow(2, 60, 4, 3)
      ))
    }
  }

  case class PriceRow(currency: String, amount: Double)

  object PriceRepository {
    def find(currency: String): Option[PriceRow] = {
      currency match {
        case "USD" => Some(PriceRow(currency, 1.0))
        case _ => None
      }
    }
  }

  case class VehicleRow(vehicleId: String, isVip: Boolean)

  object VehicleRepository {
    def find(vehicleId: String): Option[VehicleRow] = {
      vehicleId match {
        case "" => None
        case "RSTNA01" => Some(VehicleRow(vehicleId, isVip = true))
        case _ => Some(VehicleRow(vehicleId, isVip = false))
      }
    }
  }
}
